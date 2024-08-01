package com.oneliferp.cwu.modules.session.commands;

import com.oneliferp.cwu.cache.SessionCache;
import com.oneliferp.cwu.commands.CwuCommand;
import com.oneliferp.cwu.database.CwuDatabase;
import com.oneliferp.cwu.database.SessionDatabase;
import com.oneliferp.cwu.exceptions.CwuException;
import com.oneliferp.cwu.misc.*;
import com.oneliferp.cwu.models.CwuModel;
import com.oneliferp.cwu.models.SessionModel;
import com.oneliferp.cwu.modules.profile.exceptions.ProfileNotFoundException;
import com.oneliferp.cwu.modules.session.exceptions.SessionNotFoundException;
import com.oneliferp.cwu.modules.session.exceptions.SessionValidationException;
import com.oneliferp.cwu.modules.session.misc.SessionButtonType;
import com.oneliferp.cwu.modules.session.misc.SessionCommandType;
import com.oneliferp.cwu.modules.session.misc.SessionModalType;
import com.oneliferp.cwu.modules.session.utils.SessionBuilderUtils;
import com.oneliferp.cwu.utils.RegexUtils;
import com.oneliferp.cwu.utils.Toolbox;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInput.Builder;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SessionCommand extends CwuCommand {
    private final SessionCache sessionCache;
    private final SessionDatabase sessionDatabase;

    private final CwuDatabase cwuDatabase;

    public SessionCommand() {
        super(SessionCommandType.BASE.getId(), "Vous permet de créer des raports de session.");
        this.sessionCache = SessionCache.get();
        this.sessionDatabase = SessionDatabase.get();

        this.cwuDatabase = CwuDatabase.getInstance();
    }

    @Override
    public SlashCommandData configure(final SlashCommandData slashCommand) {
        final List<SubcommandData> subCommands = Arrays.stream(SessionCommandType.values())
                .skip(1)
                .map(type -> new SubcommandData(type.getId(), type.getDescription()))
                .toList();

        slashCommand.addSubcommands(subCommands);
        return slashCommand;
    }

    /*
    Abstract Handlers
    */
    @Override
    public void handleCommandInteraction(final SlashCommandInteractionEvent event) throws Exception {
        final CwuModel cwu = this.cwuDatabase.getFromId(event.getUser().getIdLong());
        if (cwu == null) throw new ProfileNotFoundException();

        final SessionType sessionType = SessionType.fromCommandType(SessionCommandType.fromId(event.getSubcommandName()));
        final SessionModel ongoingSession = this.sessionCache.get(cwu.getCid());
        if (ongoingSession != null) {
            event.replyEmbeds(SessionBuilderUtils.ongoingMessage(ongoingSession, sessionType))
                    .setComponents(SessionBuilderUtils.resumeOrOverwriteRow(cwu.getCid(), sessionType))
                    .setEphemeral(true).queue();
            return;
        }

        // Create rapport and set default values
        final SessionModel session = new SessionModel(cwu);
        session.setType(sessionType);
        session.setZone(sessionType.getZone());
        this.sessionCache.add(cwu.getCid(), session);

        event.replyEmbeds(SessionBuilderUtils.startMessage(sessionType))
                .setComponents(SessionBuilderUtils.startAndCancelRow(cwu.getCid()))
                .setEphemeral(true).queue();
    }

    @Override
    public void handleButtonInteraction(final ButtonInteractionEvent event, final String eventID) throws CwuException {
        final EventTypeData eventType = new EventTypeData(eventID);

        final String cid = eventType.getCid();
        final SessionModel session = this.sessionCache.get(cid);
        if (session == null) throw new SessionNotFoundException();

        switch ((SessionButtonType) eventType.enumType) {
            default -> throw new IllegalArgumentException();
            case START -> this.handleStartButton(event, cid);
            case CANCEL -> this.handleCancelButton(event, cid);
            case RESUME -> this.handleResumeButton(event, cid);
            case REPLACE -> this.handleOverwriteButton(event, cid, eventType.getSession());
            case EDIT -> this.handleEditButton(event, cid);
            case SUBMIT -> this.handleSubmitButton(event, cid);
            case CLEAR -> this.handleClearButton(event, cid);
            case FILL -> this.handleFillButton(event, cid);
            case PREVIEW -> this.handlePreviewButton(event, cid);
            case PAGE_NEXT -> this.handlePageButton(event, cid, true);
            case PAGE_PREV -> this.handlePageButton(event, cid, false);
        }
    }

    @Override
    public void handleModalInteraction(final ModalInteractionEvent event, final String eventID) throws CwuException {
        final EventTypeData eventType = new EventTypeData(eventID);

        final SessionModel session = this.sessionCache.get(eventType.getCid());
        if (session == null) throw new SessionNotFoundException();

        final PageType currentPage = session.getCurrentPage();
        final String content = event.getValue("cwu_session.fill/data").getAsString();

        switch ((SessionModalType) eventType.enumType) {
            default -> throw new IllegalArgumentException();
            case FILL_PARTICIPANTS -> {
                session.addParticipants(ParticipantType.fromPage(currentPage), RegexUtils.parseIdentities(content));
                event.editMessageEmbeds(SessionBuilderUtils.participantMessage(session))
                        .setComponents(SessionBuilderUtils.pageRow(session))
                        .queue();
            }
            case FILL_INFO -> {
                session.setInfo(content);
                event.editMessageEmbeds(SessionBuilderUtils.infoMessage(session))
                        .setComponents(SessionBuilderUtils.pageRow(session))
                        .queue();
            }
            case FILL_EARNINGS -> {
                session.setEarnings(RegexUtils.parseEarnings(content));
                event.editMessageEmbeds(SessionBuilderUtils.earningsMessage(session))
                        .setComponents(SessionBuilderUtils.pageRow(session))
                        .queue();
            }
        }
    }

    @Override
    public void handleSelectionInteraction(final StringSelectInteractionEvent event, final String eventID) throws Exception {
        final EventTypeData eventType = new EventTypeData(eventID);

        final SessionModel session = this.sessionCache.get(eventType.getCid());
        if (session == null) throw new SessionNotFoundException();

        final ZoneType zone = ZoneType.valueOf(event.getValues().get(0));
        session.setZone(zone);

        event.editMessageEmbeds(SessionBuilderUtils.zoneMessage(session))
                .setComponents(SessionBuilderUtils.zoneRows(session))
                .queue();
    }

    /*
        Button Handlers
        */
    private void handleCancelButton(final ButtonInteractionEvent event, final String cid) throws SessionNotFoundException {
        final SessionModel session = this.sessionCache.get(cid);
        if (session == null) throw new SessionNotFoundException();

        this.sessionCache.remove(cid);

        event.reply("❌ Vous avez annuler votre session de travail.")
                .setEphemeral(true).queue();
    }

    private void handleEditButton(final ButtonInteractionEvent event, final String cid) throws SessionNotFoundException {
        final SessionModel session = this.sessionCache.get(cid);
        if (session == null) throw new SessionNotFoundException();

        session.setCurrentPage(PageType.LOYALISTS);
        this.goToPage(event, session);
    }

    private void handleOverwriteButton(final ButtonInteractionEvent event, final String cid, final SessionType type) throws SessionNotFoundException {
        final SessionModel session = this.sessionCache.get(cid);
        if (session == null) throw new SessionNotFoundException();

        // Update default values
        session.setType(type);
        session.setZone(type.getZone());

        // Setup session data
        session.begin();

        this.goToPage(event, session);
    }

    private void handleResumeButton(final ButtonInteractionEvent event, final String cid) throws SessionNotFoundException {
        final SessionModel session = this.sessionCache.get(cid);
        if (session == null) throw new SessionNotFoundException();

        session.setCurrentPage(PageType.PREVIEW);
        this.goToPage(event, session);
    }

    private void handleFillButton(final ButtonInteractionEvent event, final String cid) throws SessionNotFoundException {
        final SessionModel session = this.sessionCache.get(cid);
        if (session == null) throw new SessionNotFoundException();

        final PageType currentPage = session.getCurrentPage();

        final Builder inputBuilder = TextInput.create("cwu_session.fill/data", currentPage.getDescription(), TextInputStyle.PARAGRAPH)
                .setRequired(true);

        // Fill input if data exist at page
        SessionModalType modalType;
        switch (currentPage) {
            default -> throw new IllegalArgumentException();
            case LOYALISTS, CITIZENS, VORTIGAUNTS, ANTI_CITIZENS -> {
                modalType = SessionModalType.FILL_PARTICIPANTS;
                inputBuilder.setPlaceholder("Nom Prénom, #12345");

                final var participants = session.getParticipants(ParticipantType.fromPage(currentPage));
                if (participants.isEmpty()) break;
                inputBuilder.setValue(Toolbox.flatten(participants));
            }
            case INFO -> {
                modalType = SessionModalType.FILL_INFO;
                inputBuilder.setPlaceholder("Rien à signaler.");

                final var info = session.getInfo();
                if (info == null) break;
                inputBuilder.setValue(info);
            }
            case EARNINGS -> {
                modalType = SessionModalType.FILL_EARNINGS;
                inputBuilder.setPlaceholder("ex: 1438");

                final var earnings = session.getIncome().getEarnings();
                if (earnings == null) break;
                inputBuilder.setValue(earnings.toString());
            }
        }

        final Modal modal = Modal.create(modalType.build(session.getManagerCid()), "Ajout des informations")
                .addComponents(ActionRow.of(inputBuilder.build()))
                .build();

        event.replyModal(modal).queue();
    }

    private void handleClearButton(final ButtonInteractionEvent event, final String cid) throws SessionNotFoundException {
        final SessionModel session = this.sessionCache.get(cid);
        if (session == null) throw new SessionNotFoundException();

        final PageType currentPage = session.getCurrentPage();
        switch (currentPage) {
            case INFO -> session.setInfo(null);
            case EARNINGS -> session.setEarnings(null);
            case ZONE -> session.setZone(ZoneType.UNKNOWN);
            default -> session.clearParticipants(ParticipantType.fromPage(currentPage));
        }

        this.goToPage(event, session);
    }

    protected void handleStartButton(final ButtonInteractionEvent event, final String cid) throws SessionNotFoundException {
        final SessionModel session = this.sessionCache.get(cid);
        if (session == null) throw new SessionNotFoundException();

        // Setup session data
        session.begin();

        this.goToPage(event, session);
    }

    private void handlePreviewButton(final ButtonInteractionEvent event, final String cid) throws SessionNotFoundException {
        final SessionModel session = this.sessionCache.get(cid);
        if (session == null) throw new SessionNotFoundException();

        session.setCurrentPage(PageType.PREVIEW);
        this.goToPage(event, session);
    }

    private void handleSubmitButton(final ButtonInteractionEvent event, final String cid) throws SessionNotFoundException, SessionValidationException {
        final SessionModel session = this.sessionCache.get(cid);
        if (session == null) throw new SessionNotFoundException();
        if (!session.isValid()) throw new SessionValidationException();

        // Compute session data
        session.end();

        this.sessionCache.remove(cid);

        // Save session within database
        this.sessionDatabase.addOne(session);
        this.sessionDatabase.writeToCache();

        event.editMessageEmbeds(SessionBuilderUtils.submitMessage(session))
                .setComponents(new ArrayList<>())
                .queue();
    }

    private void handlePageButton(final ButtonInteractionEvent event, final String cid, final boolean isNext) throws SessionNotFoundException {
        final SessionModel session = this.sessionCache.get(cid);
        if (session == null) throw new SessionNotFoundException();

        final PageType currentPage = session.getCurrentPage();
        session.setCurrentPage(isNext ? currentPage.getNext() : currentPage.getPrevious());
        this.goToPage(event, session);
    }

    /*
    Utils
    */
    protected void goToPage(final ButtonInteractionEvent event, final SessionModel session) {
        final PageType page = session.getCurrentPage();

        final MessageEmbed embed = (switch (page) {
            case ZONE -> SessionBuilderUtils.zoneMessage(session);
            case LOYALISTS, CITIZENS, VORTIGAUNTS, ANTI_CITIZENS -> SessionBuilderUtils.participantMessage(session);
            case INFO -> SessionBuilderUtils.infoMessage(session);
            case EARNINGS -> SessionBuilderUtils.earningsMessage(session);
            case PREVIEW -> SessionBuilderUtils.previewMessage(session);
        });

        final var callback = event.editMessageEmbeds(embed);
        if (page == PageType.ZONE) {
            callback.setComponents(SessionBuilderUtils.zoneRows(session));
        } else {
            callback.setComponents(SessionBuilderUtils.pageRow(session));
        }

        callback.queue();
    }
}
