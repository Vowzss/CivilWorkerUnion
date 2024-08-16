package com.oneliferp.cwu.commands.session;

import com.oneliferp.cwu.cache.SessionCache;
import com.oneliferp.cwu.commands.CwuCommand;
import com.oneliferp.cwu.database.CwuDatabase;
import com.oneliferp.cwu.database.SessionDatabase;
import com.oneliferp.cwu.exceptions.CwuException;
import com.oneliferp.cwu.commands.CommandContext;
import com.oneliferp.cwu.commands.session.misc.ParticipantType;
import com.oneliferp.cwu.commands.session.misc.ZoneType;
import com.oneliferp.cwu.models.CwuModel;
import com.oneliferp.cwu.commands.session.misc.ids.*;
import com.oneliferp.cwu.commands.session.models.SessionModel;
import com.oneliferp.cwu.commands.profile.exceptions.ProfileNotFoundException;
import com.oneliferp.cwu.commands.session.exceptions.SessionNotFoundException;
import com.oneliferp.cwu.commands.session.exceptions.SessionValidationException;
import com.oneliferp.cwu.commands.session.misc.*;
import com.oneliferp.cwu.commands.session.utils.SessionBuilderUtils;
import com.oneliferp.cwu.utils.RegexUtils;
import com.oneliferp.cwu.utils.Toolbox;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInput.Builder;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.ArrayList;

public class SessionCommand extends CwuCommand {
    private final SessionDatabase sessionDatabase;
    private final SessionCache sessionCache;

    private final CwuDatabase cwuDatabase;

    public SessionCommand() {
        super("session", "session", "Vous permet de créer des sessions de travail.");
        this.sessionDatabase = SessionDatabase.get();
        this.sessionCache = SessionCache.get();

        this.cwuDatabase = CwuDatabase.get();
    }

    /*
    Abstract Handlers
    */
    @Override
    public void handleCommandInteraction(final SlashCommandInteractionEvent event) throws CwuException {
        final CwuModel cwu = this.cwuDatabase.getFromId(event.getUser().getIdLong());
        if (cwu == null) throw new ProfileNotFoundException();

        final SessionModel ongoingSession = this.sessionCache.get(cwu.getCid());
        if (ongoingSession != null && ongoingSession.getType() != SessionType.UNKNOWN) {
            event.replyEmbeds(SessionBuilderUtils.ongoingMessage(ongoingSession))
                    .setComponents(SessionBuilderUtils.resumeOrOverwriteRow(cwu.getCid()))
                    .queue();
            return;
        }

        // Create session and set default values
        final SessionModel session = new SessionModel(cwu);
        this.sessionCache.add(cwu.getCid(), session);

        event.replyEmbeds(SessionBuilderUtils.beginMessage(session.getType()))
                .setComponents(SessionBuilderUtils.beginRow(session))
                .queue();
    }

    @Override
    public void handleButtonInteraction(final ButtonInteractionEvent event, final CommandContext eventType) throws CwuException {
        final String cid = eventType.getCid();
        final SessionModel session = this.sessionCache.get(cid);
        if (session == null) throw new SessionNotFoundException();

        switch ((SessionButtonType) eventType.enumType) {
            default -> throw new IllegalArgumentException();
            case BEGIN -> this.handleBeginButton(event, session);
            case ABORT -> this.handleCancelButton(event, session);
            case RESUME -> this.handleResumeButton(event, session);
            case OVERWRITE -> this.handleOverwriteButton(event, session);
            case EDIT -> this.handleEditButton(event, session);
            case SUBMIT -> this.handleSubmitButton(event, session);
            case CLEAR -> this.handleClearButton(event, session);
            case FILL -> this.handleFillButton(event, session);
            case PREVIEW -> this.handlePreviewButton(event, session);
            case PAGE_NEXT -> this.handlePageButton(event, session, true);
            case PAGE_PREV -> this.handlePageButton(event, session, false);
        }
    }

    @Override
    public void handleModalInteraction(final ModalInteractionEvent event, final CommandContext eventType) throws CwuException {
        final SessionModel session = this.sessionCache.get(eventType.getCid());
        if (session == null) throw new SessionNotFoundException();

        final SessionPageType currentPage = session.getCurrentPage();
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
                session.setEarnings(RegexUtils.parseTokens(content));
                event.editMessageEmbeds(SessionBuilderUtils.earningsMessage(session))
                        .setComponents(SessionBuilderUtils.pageRow(session))
                        .queue();
            }
        }
    }

    @Override
    public void handleSelectionInteraction(final StringSelectInteractionEvent event, final CommandContext eventType) throws CwuException {
        final SessionModel session = this.sessionCache.get(eventType.getCid());
        if (session == null) throw new SessionNotFoundException();

        switch ((SessionMenuType) eventType.enumType) {
            case SELECT_TYPE -> {
                final SessionType type = SessionType.valueOf(event.getValues().get(0));
                if (session.getType() != type) session.setType(type);

                event.editMessageEmbeds(SessionBuilderUtils.beginMessage(session.getType()))
                        .setComponents(SessionBuilderUtils.beginRow(session))
                        .queue();
            }
            case SELECT_ZONE -> {
                final ZoneType zone = ZoneType.valueOf(event.getValues().get(0));
                if (session.getZone() != zone) session.setZone(zone);

                event.editMessageEmbeds(SessionBuilderUtils.zoneMessage(session))
                        .setComponents(SessionBuilderUtils.zoneRows(session))
                        .queue();
            }
        }
    }

    /*
    Button handlers
    */
    private void handleCancelButton(final ButtonInteractionEvent event, final SessionModel session) {
        this.sessionCache.remove(session.getManagerCid());

        event.reply("❌ Vous avez annuler votre session de travail.")
                .queue();
    }

    private void handleEditButton(final ButtonInteractionEvent event, final SessionModel session) {
        session.goFirstPage();
        this.goToPage(event, session);
    }

    private void handleOverwriteButton(final ButtonInteractionEvent event, final SessionModel session) {
        // Reset to default values
        for (final ParticipantType type : ParticipantType.values()) {
            session.clearParticipants(type);
        }
        session.setType(SessionType.UNKNOWN);
        session.setZone(ZoneType.UNKNOWN);
        session.setEarnings(null);
        session.setInfo(null);

        session.begin();

        this.goToPage(event, session);
    }

    private void handleResumeButton(final ButtonInteractionEvent event, final SessionModel session) {
        session.goPreviewPage();
        this.goToPage(event, session);
    }

    private void handleFillButton(final ButtonInteractionEvent event, final SessionModel session) {
        final SessionPageType currentPage = session.getCurrentPage();
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
                inputBuilder.setPlaceholder("Rien à signaler");

                final var info = session.getInfo();
                if (info == null) break;
                inputBuilder.setValue(info);
            }
            case TOKENS -> {
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

    private void handleClearButton(final ButtonInteractionEvent event, final SessionModel session) {
        final SessionPageType currentPage = session.getCurrentPage();
        switch (currentPage) {
            case INFO -> session.setInfo(null);
            case TOKENS -> session.setEarnings(null);
            case ZONE -> session.setZone(ZoneType.UNKNOWN);
            default -> session.clearParticipants(ParticipantType.fromPage(currentPage));
        }

        this.goToPage(event, session);
    }

    protected void handleBeginButton(final ButtonInteractionEvent event, final SessionModel session) {
        if (session.getType() == SessionType.UNKNOWN) {
            event.reply("⛔ Vous devez sélectionner un type de session.").queue();
            return;
        }

        session.begin();
        this.goToPage(event, session);
    }

    private void handlePreviewButton(final ButtonInteractionEvent event, final SessionModel session)  {
        session.goPreviewPage();
        this.goToPage(event, session);
    }

    private void handleSubmitButton(final ButtonInteractionEvent event, final SessionModel session) throws SessionValidationException {
        if (!session.isValid()) throw new SessionValidationException();

        session.end();
        this.sessionCache.remove(session.getManagerCid());

        // Save session within database
        this.sessionDatabase.addOne(session);
        this.sessionDatabase.save();

        event.editMessageEmbeds(SessionBuilderUtils.submitMessage(session))
                .setComponents(new ArrayList<>())
                .queue();
    }

    private void handlePageButton(final ButtonInteractionEvent event, final SessionModel session, final boolean isNext) {
        if (isNext) session.goNextPage();
        else session.goPrevPage();

        this.goToPage(event, session);
    }

    /*
    Utils
    */
    protected void goToPage(final ButtonInteractionEvent event, final SessionModel session) {
        final SessionPageType page = session.getCurrentPage();

        final MessageEmbed embed = (switch (page) {
            case ZONE -> SessionBuilderUtils.zoneMessage(session);
            case LOYALISTS, CITIZENS, VORTIGAUNTS, ANTI_CITIZENS -> SessionBuilderUtils.participantMessage(session);
            case INFO -> SessionBuilderUtils.infoMessage(session);
            case TOKENS -> SessionBuilderUtils.earningsMessage(session);
            case PREVIEW -> SessionBuilderUtils.previewMessage(session);
        });

        final var callback = event.editMessageEmbeds(embed);
        if (page == SessionPageType.ZONE) {
            callback.setComponents(SessionBuilderUtils.zoneRows(session));
        } else {
            callback.setComponents(SessionBuilderUtils.pageRow(session));
        }

        callback.queue();
    }
}
