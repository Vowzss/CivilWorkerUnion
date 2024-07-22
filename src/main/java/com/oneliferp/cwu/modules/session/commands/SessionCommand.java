package com.oneliferp.cwu.modules.session.commands;

import com.oneliferp.cwu.Cache.SessionCache;
import com.oneliferp.cwu.Commands.ClearCommand;
import com.oneliferp.cwu.Commands.CwuCommand;
import com.oneliferp.cwu.Database.CwuDatabase;
import com.oneliferp.cwu.Models.CwuModel;
import com.oneliferp.cwu.Models.SessionModel;
import com.oneliferp.cwu.Utils.RegexUtils;
import com.oneliferp.cwu.misc.PageType;
import com.oneliferp.cwu.misc.ParticipantType;
import com.oneliferp.cwu.misc.SessionType;
import com.oneliferp.cwu.modules.session.misc.SessionButtonType;
import com.oneliferp.cwu.modules.session.misc.SessionCommandType;
import com.oneliferp.cwu.modules.session.utils.SessionBuilderUtils;
import com.oneliferp.cwu.modules.session.utils.SessionFeedbackUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Arrays;
import java.util.List;

import static com.oneliferp.cwu.modules.session.misc.SessionCommandType.RATION;

public class SessionCommand extends CwuCommand {
    protected final SessionCache sessionCache;

    public SessionCommand() {
        super(SessionCommandType.BASE.getId(), "Vous permet de créer des raports de session.");
        this.sessionCache = SessionCache.getInstance();
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
    public void handleCommandEvent(final SlashCommandInteractionEvent event) {
        final SessionType sessionType = SessionType.RATION;

        switch (SessionCommandType.fromId(event.getSubcommandName())) {
            default -> throw new IllegalArgumentException();
            case RATION -> {

            }
            case LAUNDRY -> {
            }
        }

        final User user = event.getUser();

        // Delete user existing session if any
        this.sessionCache.remove(user.getIdLong());

        if (!event.isFromGuild()) {
            // Delete all previous messages from bot to avoid misleading events
            final PrivateChannel channel = event.getChannel().asPrivateChannel();
            channel.getHistory().retrievePast(20).queue(history -> {
                ClearCommand.deleteBotHistory(channel, history, event.getUser().getIdLong());
            });

            // Command triggered from private message
            event.replyEmbeds(SessionBuilderUtils.startMessage(sessionType))
                    .setActionRow(SessionBuilderUtils.startAndCancelRow(sessionType))
                    .queue();
        } else {
            // Command triggered from guild
            user.openPrivateChannel().queue(channel -> {
                // Delete all previous messages from bot to avoid misleading events
                channel.getHistory().retrievePast(20).queue(history -> {
                    ClearCommand.deleteBotHistory(channel, history, event.getUser().getIdLong());
                });

                // Send session creation feedback
                channel.sendMessageEmbeds(SessionBuilderUtils.startMessage(sessionType))
                        .setActionRow(SessionBuilderUtils.startAndCancelRow(sessionType))
                        .queue(success -> SessionFeedbackUtils.sessionCreationSuccess(event, channel).queue(),
                                failure -> SessionFeedbackUtils.sessionCreationFailure(event).queue()
                        );
            });
        }
    }

    @Override
    public void handleButtonEvent(final ButtonInteractionEvent event, final String buttonID) {
        if (buttonID.contains("page")) {
            this.handlePageButton(event, buttonID);
            return;
        }

        final List<String> parts = Arrays.asList(buttonID.split(":"));
        final SessionModel session = this.sessionCache.get(event.getUser().getIdLong());

        switch (SessionButtonType.fromId(parts.get(0))) {
            default -> throw new IllegalArgumentException();
            case START -> this.handleStartButton(event, SessionType.valueOf(parts.get(1).toUpperCase()));
            case CANCEL -> this.handleCancelButton(event, session.getType());
            case PREVIEW -> this.handlePreviewButton(event, session);
            case CLEAR -> this.handleClearButton(event, session);
            case SUBMIT -> this.handleSubmitButton(event, session);
        }
    }

    @Override
    public void handleReplyEvent(final Message message, final String buttonID) {
        // Skip event other than pagination
        if (!buttonID.contains("page")) return;

        // Check if user has ongoing session
        final SessionModel session = this.sessionCache.get(message.getAuthor().getIdLong());
        if (session == null) {
            message.reply("Aucun rapport n'est en cours de rédaction.").queue();
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

    /*
    Specific handlers
    */
    protected void handleStartButton(final ButtonInteractionEvent event, final SessionType sessionType) {
        // Find CWU profile
        final CwuModel cwu = CwuDatabase.getInstance().getByUserId(event.getUser().getIdLong());
        if (cwu == null) {
            event.reply("Votre profil n'existe pas.").queue();
            return;
        }

        // Create rapport and set default values
        final SessionModel session = new SessionModel(cwu);
        session.setType(sessionType);
        session.setZone(sessionType.getZone());

        // Save session in cache
        this.sessionCache.add(event.getUser().getIdLong(), session);

        // Handle event
        this.goToPage(event, session);
    }

    private void handleCancelButton(final ButtonInteractionEvent event, final SessionType sessionType) {
        // Delete user ongoing session
        this.sessionCache.remove(event.getUser().getIdLong());

        // Send feedback
        event.getMessage().delete().queue();
        event.replyEmbeds(SessionBuilderUtils.cancelMessage(sessionType)).queue();
    }

    private void handleClearButton(final ButtonInteractionEvent event, final SessionModel session) {
        final PageType currentPage = session.currentPage;

        switch (currentPage) {
            case INFO -> session.setInfo(null);
            case EARNINGS -> session.setEarnings(null);
            default -> session.clearParticipants(ParticipantType.getFromPage(currentPage));
        }

        // Send feedback
        this.goToPage(event, session);
    }

    private void handlePreviewButton(final ButtonInteractionEvent event, final SessionModel session) {
        session.currentPage = PageType.PREVIEW;

        // Send feedback
        this.goToPage(event, session);
    }

    private void handleSubmitButton(final ButtonInteractionEvent event, final SessionModel session) {
        if (!session.isValid()) {
            event.reply("La génération du rapport a échoué car des données sont manquantes.").queue();
            return;
        }

        session.getCwu().addSession(session);
        event.reply("Le rapport à été transmit avec succès.").queue();
    }

    private void handlePageButton(final ButtonInteractionEvent event, final String buttonID) {
        // Check if user has ongoing session
        if (!this.sessionCache.contains(event.getUser().getIdLong())) {
            event.getMessage().delete().queue();
            event.reply("Aucun rapport n'est en cours de rédaction.").setEphemeral(true).queue();
            return;
        }

        final SessionModel session = this.sessionCache.get(event.getUser().getIdLong());
        final PageType currentPage = session.currentPage;
        session.currentPage = buttonID.contains("next") ? currentPage.getNext() : currentPage.getPrevious();

        this.goToPage(event, session);
    }

    protected void goToPage(final ButtonInteractionEvent event, final SessionModel session) {
        event.getMessage().delete().queue();

        switch (session.currentPage) {
            case LOYALISTS: {
                SessionFeedbackUtils.loyalist(event, session).queue();
                break;
            }
            case CITIZENS: {
                SessionFeedbackUtils.citizen(event, session).queue();
                break;
            }
            case VORTIGAUNTS: {
                SessionFeedbackUtils.vortigaunt(event, session).queue();
                break;
            }
            case ANTI_CITIZENS: {
                SessionFeedbackUtils.antiCitizen(event, session).queue();
                break;
            }
            case INFO: {
                SessionFeedbackUtils.info(event, session).queue();
                break;
            }
            case EARNINGS: {
                SessionFeedbackUtils.earnings(event, session).queue();
                break;
            }
            case PREVIEW: {
                SessionFeedbackUtils.preview(event, session).queue();
                break;
            }
        }
    }
}
