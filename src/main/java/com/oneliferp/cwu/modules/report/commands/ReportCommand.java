package com.oneliferp.cwu.modules.report.commands;

import com.oneliferp.cwu.Cache.ReportCache;
import com.oneliferp.cwu.Commands.ClearCommand;
import com.oneliferp.cwu.Commands.CwuCommand;
import com.oneliferp.cwu.Database.CwuDatabase;
import com.oneliferp.cwu.Models.CwuModel;
import com.oneliferp.cwu.Models.SessionModel;
import com.oneliferp.cwu.Utils.RegexUtils;
import com.oneliferp.cwu.misc.PageType;
import com.oneliferp.cwu.misc.ParticipantType;
import com.oneliferp.cwu.misc.SessionType;
import com.oneliferp.cwu.modules.report.misc.ReportButtonType;
import com.oneliferp.cwu.modules.report.misc.ReportCommandType;
import com.oneliferp.cwu.modules.report.utils.ReportBuilderUtils;
import com.oneliferp.cwu.modules.report.utils.ReportFeedbackUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Arrays;
import java.util.List;

import static com.oneliferp.cwu.modules.report.misc.ReportCommandType.RATION;

public class ReportCommand extends CwuCommand {
    protected final ReportCache reportCache;

    public ReportCommand() {
        super(ReportCommandType.BASE.getId(), "Vous permet de créer des raports de session.");
        this.reportCache = ReportCache.getInstance();
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

        switch (ReportCommandType.fromId(event.getSubcommandName())) {
            default -> throw new IllegalArgumentException();
            case RATION -> {

            }
            case LAUNDRY -> {
            }
        }

        final User user = event.getUser();

        // Delete user existing report if any
        this.reportCache.remove(user.getIdLong());

        if (!event.isFromGuild()) {
            // Delete all previous messages from bot to avoid misleading events
            final PrivateChannel channel = event.getChannel().asPrivateChannel();
            channel.getHistory().retrievePast(20).queue(history -> {
                ClearCommand.deleteBotHistory(channel, history, event.getUser().getIdLong());
            });

            // Command triggered from private message
            event.replyEmbeds(ReportBuilderUtils.startMessage(sessionType))
                    .setActionRow(ReportBuilderUtils.startAndCancelRow(sessionType))
                    .queue();
        } else {
            // Command triggered from guild
            user.openPrivateChannel().queue(channel -> {
                // Delete all previous messages from bot to avoid misleading events
                channel.getHistory().retrievePast(20).queue(history -> {
                    ClearCommand.deleteBotHistory(channel, history, event.getUser().getIdLong());
                });

                // Send report creation feedback
                channel.sendMessageEmbeds(ReportBuilderUtils.startMessage(sessionType))
                        .setActionRow(ReportBuilderUtils.startAndCancelRow(sessionType))
                        .queue(success -> ReportFeedbackUtils.sessionCreationSuccess(event, channel).queue(),
                                failure -> ReportFeedbackUtils.sessionCreationFailure(event).queue()
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
        final SessionModel session = this.reportCache.get(event.getUser().getIdLong());

        switch (ReportButtonType.fromId(parts.get(0))) {
            default -> throw new IllegalArgumentException();
            case START -> this.handleStartButton(event, SessionType.valueOf(parts.get(1).toUpperCase()));
            case CANCEL -> this.handleCancelButton(event, session.getType());
            case PREVIEW -> this.handlePreviewButton(event, session);
            case CLEAR -> this.handleClearButton(event, session);
        }
    }

    @Override
    public void handleReplyEvent(final Message message, final String buttonID) {
        // Skip event other than pagination
        if (!buttonID.contains("page")) return;

        // Check if user has ongoing report
        final SessionModel report = this.reportCache.get(message.getAuthor().getIdLong());
        if (report == null) {
            message.reply("Aucun rapport n'est en cours de rédaction.").queue();
            message.delete().queue();
            return;
        }

        // Delete previous message to avoid misleading instructions
        final Message reference = message.getReferencedMessage();
        reference.delete().queue();

        // Get message content
        final String content = message.getContentRaw();

        // Assign content depending on report page and display feedback
        switch (report.currentPage) {
            case LOYALISTS: {
                RegexUtils.parseApply(content).forEach(report::addLoyalist);
                ReportFeedbackUtils.loyalist(reference, report).queue();
                break;
            }
            case CITIZENS: {
                RegexUtils.parseApply(content).forEach(report::addCitizen);
                ReportFeedbackUtils.citizen(reference, report).queue();
                break;
            }
            case VORTIGAUNTS: {
                RegexUtils.parseApply(content).forEach(report::addVortigaunt);
                ReportFeedbackUtils.vortigaunt(reference, report).queue();
                break;
            }
            case ANTI_CITIZENS: {
                RegexUtils.parseApply(content).forEach(report::addAntiCitizen);
                ReportFeedbackUtils.antiCitizen(reference, report).queue();
                break;
            }
            case INFO: {
                report.setInfo(content);
                ReportFeedbackUtils.info(reference, report).queue();
                break;
            }
            case EARNINGS: {
                report.setEarnings(RegexUtils.parseEarnings(content));
                ReportFeedbackUtils.earnings(reference, report).queue();
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
        final SessionModel report = new SessionModel(cwu);
        report.setType(sessionType);
        report.setZone(sessionType.getZone());

        // Save report in cache
        this.reportCache.add(event.getUser().getIdLong(), report);

        // Handle event
        this.goToPage(event, report);
    }

    private void handleCancelButton(final ButtonInteractionEvent event, final SessionType sessionType) {
        // Delete user ongoing report
        this.reportCache.remove(event.getUser().getIdLong());

        // Send feedback
        event.getMessage().delete().queue();
        event.replyEmbeds(ReportBuilderUtils.cancelMessage(sessionType)).queue();
    }

    private void handleClearButton(final ButtonInteractionEvent event, final SessionModel report) {
        final PageType currentPage = report.currentPage;

        switch (currentPage) {
            case INFO -> report.setInfo(null);
            case EARNINGS -> report.setEarnings(null);
            default -> report.clearParticipants(ParticipantType.getFromPage(currentPage));
        }

        // Send feedback
        this.goToPage(event, report);
    }

    private void handlePreviewButton(final ButtonInteractionEvent event, final SessionModel report) {
        final boolean isReportValid = report.validate();

        report.currentPage = PageType.PREVIEW;

        // Send feedback
        this.goToPage(event, report);
    }

    private void handlePageButton(final ButtonInteractionEvent event, final String buttonID) {
        // Check if user has ongoing report
        if (!this.reportCache.contains(event.getUser().getIdLong())) {
            event.getMessage().delete().queue();
            event.reply("Aucun rapport n'est en cours de rédaction.").setEphemeral(true).queue();
            return;
        }

        final SessionModel report = this.reportCache.get(event.getUser().getIdLong());
        final PageType currentPage = report.currentPage;
        report.currentPage = buttonID.contains("next") ? currentPage.getNext() : currentPage.getPrevious();

        this.goToPage(event, report);
    }

    protected void goToPage(final ButtonInteractionEvent event, final SessionModel report) {
        event.getMessage().delete().queue();

        switch (report.currentPage) {
            case LOYALISTS: {
                ReportFeedbackUtils.loyalist(event, report).queue();
                break;
            }
            case CITIZENS: {
                ReportFeedbackUtils.citizen(event, report).queue();
                break;
            }
            case VORTIGAUNTS: {
                ReportFeedbackUtils.vortigaunt(event, report).queue();
                break;
            }
            case ANTI_CITIZENS: {
                ReportFeedbackUtils.antiCitizen(event, report).queue();
                break;
            }
            case INFO: {
                ReportFeedbackUtils.info(event, report).queue();
                break;
            }
            case EARNINGS: {
                ReportFeedbackUtils.earnings(event, report).queue();
                break;
            }
            case PREVIEW: {
                ReportFeedbackUtils.preview(event, report).queue();
                break;
            }
        }
    }
}
