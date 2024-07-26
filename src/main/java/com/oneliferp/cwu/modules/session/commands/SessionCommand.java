package com.oneliferp.cwu.modules.session.commands;

import com.oneliferp.cwu.Cache.SessionCache;
import com.oneliferp.cwu.Commands.CwuCommand;
import com.oneliferp.cwu.Database.CwuDatabase;
import com.oneliferp.cwu.Database.SessionDatabase;
import com.oneliferp.cwu.Models.CwuModel;
import com.oneliferp.cwu.Models.SessionModel;
import com.oneliferp.cwu.Utils.Toolbox;
import com.oneliferp.cwu.misc.PageType;
import com.oneliferp.cwu.misc.ParticipantType;
import com.oneliferp.cwu.misc.SessionType;
import com.oneliferp.cwu.modules.profile.exceptions.ProfileNotFoundException;
import com.oneliferp.cwu.modules.session.exceptions.ReportValidationException;
import com.oneliferp.cwu.modules.session.exceptions.SessionNotFoundException;
import com.oneliferp.cwu.modules.session.misc.SessionButtonType;
import com.oneliferp.cwu.modules.session.misc.SessionCommandType;
import com.oneliferp.cwu.modules.session.misc.SessionModalType;
import com.oneliferp.cwu.modules.session.utils.SessionBuilderUtils;
import com.oneliferp.cwu.modules.session.utils.SessionFeedbackUtils;
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

import java.util.Arrays;
import java.util.List;

import static com.oneliferp.cwu.modules.session.misc.SessionCommandType.RATION;

public class SessionCommand extends CwuCommand {
    private final SessionCache sessionCache;
    private final SessionDatabase sessionDatabase;

    public SessionCommand() {
        super(SessionCommandType.BASE.getId(), "Vous permet de créer des raports de session.");
        this.sessionCache = SessionCache.get();
        this.sessionDatabase = SessionDatabase.get();
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
        SessionType sessionType;
        switch (SessionCommandType.fromId(event.getSubcommandName())) {
            default -> throw new IllegalArgumentException();
            case RATION -> sessionType = SessionType.RATION;
            case LAUNDRY -> sessionType = SessionType.LAUNDRY;
        }

        final String cid = ""; //this.profileCache.get(event.getUser().getIdLong());
        if (cid == null) throw new ProfileNotFoundException();

        this.sessionCache.remove(cid);

        event.replyEmbeds(SessionBuilderUtils.startMessage(sessionType))
                .setActionRow(SessionBuilderUtils.startAndCancelRow(cid, sessionType))
                .setEphemeral(true).queue();
    }

    @Override
    public void handleButtonEvent(final ButtonInteractionEvent event, final String buttonID) {
        try {
            if (buttonID.contains("page")) {
                this.handlePageButton(event, buttonID);
                return;
            }

            final List<String> parts = Arrays.asList(buttonID.split("\\."));
            final String cid = parts.get(1).split(":")[1];

            switch (SessionButtonType.fromId(parts.get(0))) {
                default -> throw new IllegalArgumentException();
                case START -> this.handleStartButton(event, cid, SessionType.valueOf(parts.get(1).toUpperCase()));
                case CANCEL -> this.handleCancelButton(event, cid);
                case FILL -> this.handleFillButton(event, cid);
                case PREVIEW -> this.handlePreviewButton(event, cid);
                case CLEAR -> this.handleClearButton(event, cid);
                case SUBMIT -> this.handleSubmitButton(event, cid);
            }
        } catch (Exception e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
        }
    }

    @Override
    public void handleModalEvent(final ModalInteractionEvent event, final String modalID) {
        final List<String> parts = Arrays.asList(modalID.split(":"));

        switch (SessionModalType.fromId(parts.get(0))) {
            default -> throw new IllegalArgumentException();
            case PARTICIPANTS -> {

            }
            case INFO -> {
            }
            case EARNINGS -> {
            }
        }
    }

    /*
    @Override
    public void handleReplyEvent(final Message message, final String buttonID) {
        // Skip event other than pagination
        if (!buttonID.contains("page")) return;

        // Check if user has ongoing session
        final SessionModel session = this.sessionCache.get(message.getAuthor().getIdLong());
        if (session == null) {
            message.reply("Vous n'avez aucune session de travail en cours.").queue();
            message.delete().queue();
            return;
        }

        // Delete previous message to avoid misleading instructions
        final Message reference = message.getReferencedMessage();
        reference.delete().queue();

        // Get message content
        final String content = message.getContentRaw();

        // Assign content depending on session page and display feedback
        switch (session.currentPage) {
            case LOYALISTS: {
                RegexUtils.parseApply(content).forEach(session::addLoyalist);
                SessionFeedbackUtils.loyalist(reference, session).queue();
                break;
            }
            case CITIZENS: {
                RegexUtils.parseApply(content).forEach(session::addCitizen);
                SessionFeedbackUtils.citizen(reference, session).queue();
                break;
            }
            case VORTIGAUNTS: {
                RegexUtils.parseApply(content).forEach(session::addVortigaunt);
                SessionFeedbackUtils.vortigaunt(reference, session).queue();
                break;
            }
            case ANTI_CITIZENS: {
                RegexUtils.parseApply(content).forEach(session::addAntiCitizen);
                SessionFeedbackUtils.antiCitizen(reference, session).queue();
                break;
            }
            case INFO: {
                session.setInfo(content);
                SessionFeedbackUtils.info(reference, session).queue();
                break;
            }
            case EARNINGS: {
                session.setEarnings(RegexUtils.parseEarnings(content));
                SessionFeedbackUtils.earnings(reference, session).queue();
                break;
            }
        }
    }
*/

    /*
    Specific handlers
    */
    protected void handleStartButton(final ButtonInteractionEvent event, final String cid, final SessionType sessionType) throws ProfileNotFoundException {
        final CwuModel cwu = CwuDatabase.get().get(cid);
        if (cwu == null) throw new ProfileNotFoundException();

        // Create rapport and set default values
        final SessionModel session = new SessionModel(cwu);
        session.setType(sessionType);
        session.setZone(sessionType.getZone());

        this.sessionCache.add(cwu.getCid(), session);

        this.goToPage(event, session);
    }

    private void handleCancelButton(final ButtonInteractionEvent event, final String cid) throws SessionNotFoundException {
        final SessionModel session = this.sessionCache.get(cid);
        if (session == null) throw new SessionNotFoundException();

        this.sessionCache.remove(cid);

        event.getMessage().delete().queue();
        event.replyEmbeds(SessionBuilderUtils.cancelMessage(session.getType()))
                .setEphemeral(true).queue();
    }

    private void handleFillButton(final ButtonInteractionEvent event, final String cid) throws SessionNotFoundException {
        final SessionModel session = this.sessionCache.get(cid);
        if (session == null) throw new SessionNotFoundException();

        final Builder inputBuilder = TextInput.create("cwu_session_fill_data", session.currentPage.getDescription(), TextInputStyle.PARAGRAPH)
                .setPlaceholder("ex: Lavrenti Casela, #21105")
                .setRequired(true);

        // Fill input if data exist at page
        SessionModalType modalType;
        switch (session.currentPage) {
            default -> throw new IllegalArgumentException();
            case LOYALISTS -> {
                modalType = SessionModalType.PARTICIPANTS;

                final var participants = session.getLoyalists();
                if (participants.isEmpty()) break;
                inputBuilder.setValue(Toolbox.flatten(participants));

            }
            case CITIZENS -> {
                modalType = SessionModalType.PARTICIPANTS;

                final var participants = session.getCitizens();
                if (participants.isEmpty()) break;
                inputBuilder.setValue(Toolbox.flatten(participants));

            }
            case VORTIGAUNTS -> {
                modalType = SessionModalType.PARTICIPANTS;

                final var participants = session.getVortigaunts();
                if (participants.isEmpty()) break;
                inputBuilder.setValue(Toolbox.flatten(participants));

            }
            case ANTI_CITIZENS -> {
                modalType = SessionModalType.PARTICIPANTS;

                final var participants = session.getAntiCitizens();
                if (participants.isEmpty()) break;
                inputBuilder.setValue(Toolbox.flatten(participants));

            }
            case INFO -> {
                modalType = SessionModalType.INFO;

                final var info = session.getInfo();
                if (info.isBlank()) break;
                inputBuilder.setValue(info);

            }
            case EARNINGS -> {
                modalType = SessionModalType.EARNINGS;

                final var earnings = session.getEarnings();
                if (earnings == null) break;
                inputBuilder.setValue(earnings.toString());
            }
        }

        final Modal modal = Modal.create(modalType.getId() + ":" + session.currentPage.getID(), "Ajout des informations")
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
            default -> session.clearParticipants(ParticipantType.getFromPage(currentPage));
        }

        this.goToPage(event, session);
    }

    private void handlePreviewButton(final ButtonInteractionEvent event, final String cid) throws SessionNotFoundException {
        final SessionModel session = this.sessionCache.get(cid);
        if (session == null) throw new SessionNotFoundException();

        session.currentPage = PageType.PREVIEW;

        this.goToPage(event, session);
    }

    private void handleSubmitButton(final ButtonInteractionEvent event, final String cid) throws SessionNotFoundException, ReportValidationException {
        final SessionModel session = this.sessionCache.get(cid);
        if (session == null) throw new SessionNotFoundException();
        if (!session.isValid()) throw new ReportValidationException();

        this.sessionCache.remove(cid);

        // Save session within database
        this.sessionDatabase.addOne(session);
        this.sessionDatabase.writeToCache();

        event.reply("Le rapport à été transmit avec succès.")
                .setEphemeral(true).queue();
    }

    private void handlePageButton(final ButtonInteractionEvent event, final String buttonID) throws SessionNotFoundException {
        final SessionModel session = this.sessionCache.get("");
        if (session == null) throw new SessionNotFoundException();

        final PageType currentPage = session.currentPage;
        session.currentPage = buttonID.contains("next") ? currentPage.getNext() : currentPage.getPrevious();

        this.goToPage(event, session);
    }

    protected void goToPage(final ButtonInteractionEvent event, final SessionModel session) {
        event.getMessage().delete().queue();

        (switch (session.currentPage) {
            case LOYALISTS -> SessionFeedbackUtils.loyalist(event, session);
            case CITIZENS -> SessionFeedbackUtils.citizen(event, session);
            case VORTIGAUNTS -> SessionFeedbackUtils.vortigaunt(event, session);
            case ANTI_CITIZENS -> SessionFeedbackUtils.antiCitizen(event, session);
            case INFO -> SessionFeedbackUtils.info(event, session);
            case EARNINGS -> SessionFeedbackUtils.earnings(event, session);
            case PREVIEW -> SessionFeedbackUtils.preview(event, session);
        }).setEphemeral(true).queue();
    }
}
