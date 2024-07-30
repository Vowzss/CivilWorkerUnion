package com.oneliferp.cwu.modules.session.commands;

import com.oneliferp.cwu.cache.SessionCache;
import com.oneliferp.cwu.commands.CwuCommand;
import com.oneliferp.cwu.database.CwuDatabase;
import com.oneliferp.cwu.database.SessionDatabase;
import com.oneliferp.cwu.models.CwuModel;
import com.oneliferp.cwu.models.SessionModel;
import com.oneliferp.cwu.utils.RegexUtils;
import com.oneliferp.cwu.utils.Toolbox;
import com.oneliferp.cwu.exceptions.CwuException;
import com.oneliferp.cwu.misc.EventTypeData;
import com.oneliferp.cwu.misc.PageType;
import com.oneliferp.cwu.misc.ParticipantType;
import com.oneliferp.cwu.misc.SessionType;
import com.oneliferp.cwu.modules.profile.exceptions.ProfileNotFoundException;
import com.oneliferp.cwu.modules.session.exceptions.SessionValidationException;
import com.oneliferp.cwu.modules.session.exceptions.SessionNotFoundException;
import com.oneliferp.cwu.modules.session.misc.SessionButtonType;
import com.oneliferp.cwu.modules.session.misc.SessionCommandType;
import com.oneliferp.cwu.modules.session.misc.SessionModalType;
import com.oneliferp.cwu.modules.session.utils.SessionBuilderUtils;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInput.Builder;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import static com.oneliferp.cwu.modules.session.misc.SessionCommandType.RATION;

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
        final SubcommandData rationSubCommand = new SubcommandData(RATION.getId(), RATION.getDescription());

        slashCommand.addSubcommands(rationSubCommand);
        return slashCommand;
    }

    /*
    Abstract Handlers
    */
    @Override
    public void handleCommandEvent(final SlashCommandInteractionEvent event) throws Exception {
        final CwuModel cwu = this.cwuDatabase.getFromId(event.getUser().getIdLong());
        if (cwu == null) throw new ProfileNotFoundException();

        SessionType sessionType;
        switch (SessionCommandType.fromId(event.getSubcommandName())) {
            default -> throw new IllegalArgumentException();
            case RATION -> sessionType = SessionType.RATION;
            case LAUNDRY -> sessionType = SessionType.LAUNDRY;
        }

        final SessionModel ongoingSession = this.sessionCache.get(cwu.getCid());
        if (ongoingSession != null) {
            event.replyEmbeds(SessionBuilderUtils.ongoingMessage(ongoingSession.getType()))
                    .setActionRow(SessionBuilderUtils.resumeOrCancelRow(cwu.getCid()))
                    .setEphemeral(true).queue();
            return;
        }

        // Create rapport and set default values
        final SessionModel session = new SessionModel(cwu);
        session.setType(sessionType);
        session.setZone(sessionType.getZone());
        this.sessionCache.add(cwu.getCid(), session);

        event.replyEmbeds(SessionBuilderUtils.startMessage(sessionType))
                .setActionRow(SessionBuilderUtils.startAndCancelRow(cwu.getCid()))
                .setEphemeral(true).queue();
    }

    @Override
    public void handleButtonEvent(final ButtonInteractionEvent event, final String buttonID) throws CwuException {
        final EventTypeData eventType = new EventTypeData(buttonID);

        final String cid = eventType.id;
        final SessionModel session = this.sessionCache.get(cid);
        if (session == null) throw new SessionNotFoundException();

        switch ((SessionButtonType) eventType.enumType) {
            default -> throw new IllegalArgumentException();
            case START -> this.handleStartButton(event, cid);
            case CANCEL -> this.handleCancelButton(event, cid);
            case RESUME -> this.handleResumeButton(event, cid);
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
    public void handleModalEvent(final ModalInteractionEvent event, final String modalID) throws CwuException {
        final EventTypeData eventType = new EventTypeData(modalID);

        final SessionModel session = this.sessionCache.get(eventType.id);
        if (session == null) throw new SessionNotFoundException();

        final String content = event.getValue("cwu_session.fill/data%" + session.currentPage.getID()).getAsString();

        switch ((SessionModalType) eventType.enumType) {
            default -> throw new IllegalArgumentException();
            case FILL_PARTICIPANTS -> {
                session.addParticipants(ParticipantType.fromPage(session.currentPage), RegexUtils.parseIdentities(content));
                event.editMessageEmbeds(SessionBuilderUtils.participantMessage(session))
                        .setActionRow(SessionBuilderUtils.pageRow(session))
                        .queue();
            }
            case FILL_INFO -> {
                session.setInfo(content);
                event.editMessageEmbeds(SessionBuilderUtils.infoMessage(session))
                        .setActionRow(SessionBuilderUtils.pageRow(session))
                        .queue();
            }
            case FILL_EARNINGS -> {
                session.setEarnings(RegexUtils.parseEarnings(content));
                event.editMessageEmbeds(SessionBuilderUtils.earningsMessage(session))
                        .setActionRow(SessionBuilderUtils.pageRow(session))
                        .queue();
            }
        }
    }

    /*
    Button Handlers
    */
    private void handleCancelButton(final ButtonInteractionEvent event, final String cid) throws SessionNotFoundException {
        final SessionModel session = this.sessionCache.get(cid);
        if (session == null) throw new SessionNotFoundException();

        this.sessionCache.remove(cid);

        event.getMessage().delete().queue();
        event.reply("❌ Vous avez annuler votre session de travail.")
                .setEphemeral(true).queue();
    }

    private void handleEditButton(final ButtonInteractionEvent event, final String cid) throws SessionNotFoundException {
        final SessionModel session = this.sessionCache.get(cid);
        if (session == null) throw new SessionNotFoundException();

        session.currentPage = PageType.LOYALISTS;
        this.goToPage(event, session);
    }

    private void handleResumeButton(final ButtonInteractionEvent event, final String cid) throws SessionNotFoundException {
        final SessionModel session = this.sessionCache.get(cid);
        if (session == null) throw new SessionNotFoundException();

        session.currentPage = PageType.PREVIEW;
        this.goToPage(event, session);
    }

    private void handleFillButton(final ButtonInteractionEvent event, final String cid) throws SessionNotFoundException {
        final SessionModel session = this.sessionCache.get(cid);
        if (session == null) throw new SessionNotFoundException();

        final Builder inputBuilder = TextInput.create("cwu_session.fill/data%" + session.currentPage.getID(), session.currentPage.getDescription(), TextInputStyle.PARAGRAPH)
                .setRequired(true);

        // Fill input if data exist at page
        SessionModalType modalType;
        switch (session.currentPage) {
            default -> throw new IllegalArgumentException();
            case LOYALISTS, CITIZENS, VORTIGAUNTS, ANTI_CITIZENS -> {
                modalType = SessionModalType.FILL_PARTICIPANTS;
                inputBuilder.setPlaceholder("Nom Prénom, #12345");

                final var participants = session.getParticipants(ParticipantType.fromPage(session.currentPage));
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

        final PageType currentPage = session.currentPage;
        switch (currentPage) {
            case INFO -> session.setInfo(null);
            case EARNINGS -> session.setEarnings(null);
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

        session.currentPage = PageType.PREVIEW;
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
                .queue();
    }

    private void handlePageButton(final ButtonInteractionEvent event, final String cid, final boolean isNext) throws SessionNotFoundException {
        final SessionModel session = this.sessionCache.get(cid);
        if (session == null) throw new SessionNotFoundException();

        final PageType currentPage = session.currentPage;
        session.currentPage = isNext ? currentPage.getNext() : currentPage.getPrevious();
        this.goToPage(event, session);
    }

    /*
    Utils
    */
    protected void goToPage(final ButtonInteractionEvent event, final SessionModel session) {
        final MessageEmbed embed = (switch (session.currentPage) {
            case LOYALISTS, CITIZENS, VORTIGAUNTS, ANTI_CITIZENS -> SessionBuilderUtils.participantMessage(session);
            case INFO -> SessionBuilderUtils.infoMessage(session);
            case EARNINGS -> SessionBuilderUtils.earningsMessage(session);
            case PREVIEW -> SessionBuilderUtils.previewMessage(session);
        });

        event.editMessageEmbeds(embed)
                .setActionRow(SessionBuilderUtils.pageRow(session))
                .queue();
    }
}
