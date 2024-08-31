package com.oneliferp.cwu.commands.modules.manage;

import com.oneliferp.cwu.commands.CwuCommand;
import com.oneliferp.cwu.commands.modules.manage.misc.actions.ManageModalType;
import com.oneliferp.cwu.commands.modules.manage.misc.actions.WorkforceButtonType;
import com.oneliferp.cwu.commands.modules.manage.utils.ManageBuilderUtils;
import com.oneliferp.cwu.commands.modules.profile.exceptions.ProfileNotFoundException;
import com.oneliferp.cwu.commands.modules.profile.exceptions.ProfilePermissionException;
import com.oneliferp.cwu.commands.modules.profile.models.ProfileModel;
import com.oneliferp.cwu.commands.modules.profile.utils.ProfileBuilderUtils;
import com.oneliferp.cwu.commands.modules.report.exceptions.ReportNotFoundException;
import com.oneliferp.cwu.commands.modules.report.misc.actions.ReportButtonType;
import com.oneliferp.cwu.commands.modules.report.models.ReportModel;
import com.oneliferp.cwu.commands.modules.report.utils.ReportBuilderUtils;
import com.oneliferp.cwu.commands.modules.session.exceptions.SessionNotFoundException;
import com.oneliferp.cwu.commands.modules.session.misc.actions.SessionButtonType;
import com.oneliferp.cwu.commands.modules.session.models.SessionModel;
import com.oneliferp.cwu.commands.modules.session.utils.SessionBuilderUtils;
import com.oneliferp.cwu.commands.utils.CommandContext;
import com.oneliferp.cwu.database.ProfileDatabase;
import com.oneliferp.cwu.database.ReportDatabase;
import com.oneliferp.cwu.database.SessionDatabase;
import com.oneliferp.cwu.exceptions.CwuException;
import com.oneliferp.cwu.utils.Toolbox;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Objects;

public class ManageCommand extends CwuCommand {
    private final ProfileDatabase profileDB;
    private final SessionDatabase sessionDB;
    private final ReportDatabase reportDB;

    public ManageCommand() {
        super("manage", "gestion", "Vous permet de gérer les différents aspect de la CWU.");

        this.profileDB = ProfileDatabase.get();
        this.sessionDB = SessionDatabase.get();
        this.reportDB = ReportDatabase.get();
    }

    @Override
    public SlashCommandData configure(SlashCommandData slashCommand) {
        final SubcommandData employeeSucCommand = new SubcommandData("effectif", "Vous permet de gérer l'effectif de la CWU.");
        final SubcommandData sessionSubCommand = new SubcommandData("session", "Vous permet de gérer les sessions de travail.");
        final SubcommandData reportSubCommand = new SubcommandData("rapport", "Vous permet de gérer les rapports.");

        slashCommand.addSubcommands(employeeSucCommand, sessionSubCommand, reportSubCommand);
        return super.configure(slashCommand);
    }

    @Override
    public void handleCommandInteraction(final SlashCommandInteractionEvent event) throws CwuException {
        final ProfileModel profile = this.profileDB.getFromId(event.getUser().getIdLong());
        if (profile == null) throw new ProfileNotFoundException(event);
        if (!profile.isAdministration()) throw new ProfilePermissionException(event);

        switch (Objects.requireNonNull(event.getSubcommandName())) {
            default -> throw new IllegalArgumentException();

            case "effectif" -> event.replyEmbeds(ManageBuilderUtils.workforceOverviewMessage())
                    .setComponents(ManageBuilderUtils.workforceOverviewComponent(profile.getCid()))
                    .setEphemeral(true).queue();

            case "session" -> event.replyEmbeds(SessionBuilderUtils.overviewMessage(this.sessionDB.resolveFromWeek(2)))
                    .setComponents(ManageBuilderUtils.sessionOverviewComponent(profile.getCid()))
                    .setEphemeral(true).queue();

            case "rapport" -> event.replyEmbeds(ReportBuilderUtils.overviewMessage(this.reportDB.resolveFromWeek(2)))
                    .setComponents(ManageBuilderUtils.reportOverviewComponent(profile.getCid()))
                    .setEphemeral(true).queue();
        }
    }

    @Override
    public void handleButtonInteraction(final ButtonInteractionEvent event, final CommandContext ctx) throws CwuException {
        final ProfileModel cwu = this.profileDB.getFromCid(ctx.getCid());
        if (cwu == null) throw new ProfileNotFoundException(event);

        final var type = ctx.getEnumType();
        if (type instanceof SessionButtonType choice) {
            this.handleSessionChoice(event, choice, ctx);
        } else if (type instanceof ReportButtonType choice) {
            this.handleReportChoice(event, choice, ctx);
        } else if (type instanceof WorkforceButtonType choice) {
            this.handleEmployeeChoice(event, choice, ctx.getCid());
        } else throw new IllegalArgumentException();
    }

    @Override
    public void handleModalInteraction(final ModalInteractionEvent event, final CommandContext ctx) throws CwuException {
        final ProfileModel profile = this.profileDB.getFromCid(ctx.getCid());
        if (profile == null) throw new ProfileNotFoundException(event);

        final String content = Objects.requireNonNull(event.getValue("cwu_manage.fill/data")).getAsString();

        switch ((ManageModalType) ctx.getEnumType()) {
            case EMPLOYEE_SEARCH -> {
                final ProfileModel employee = this.profileDB.getFromCid(content);
                if (employee == null) throw new ProfileNotFoundException(event);

                event.editMessageEmbeds(ProfileBuilderUtils.displayMessage(employee))
                        .setComponents(ProfileBuilderUtils.displayComponent(employee.getCid(), employee.getBranch(), employee.getRank()))
                        .queue();
            }
            case SESSION_SEARCH -> {
                final SessionModel session = this.sessionDB.get(content);
                if (session == null) throw new SessionNotFoundException(event);

                event.editMessageEmbeds(SessionBuilderUtils.displayMessage(session))
                        .setComponents(SessionBuilderUtils.displayComponent(profile.getCid(), session.getId()))
                        .queue();
            }
            case REPORT_SEARCH -> {
                final ReportModel report = this.reportDB.get(content);
                if (report == null) throw new ReportNotFoundException(event);

                event.editMessageEmbeds(ReportBuilderUtils.displayMessage(report))
                        .setComponents(ReportBuilderUtils.displayComponent(profile.getCid(), report.getId()))
                        .queue();
            }
        }
    }

    /* Handlers */
    private void handleSessionChoice(final ButtonInteractionEvent event, final SessionButtonType type, final CommandContext ctx) {
        switch (type) {
            case OVERVIEW -> {
                event.editMessageEmbeds(SessionBuilderUtils.overviewMessage(this.sessionDB.resolveFromWeek(2)))
                        .setComponents(ManageBuilderUtils.sessionOverviewComponent(ctx.getCid()))
                        .queue();
            }
            case HISTORY -> {
                final int maxPage = (int) Math.ceil((double) this.sessionDB.getCount() / 5);

                event.editMessageEmbeds(SessionBuilderUtils.historyMessage(this.sessionDB.resolveFromWeek(5), 1, maxPage))
                        .setComponents(ManageBuilderUtils.sessionPageComponent(ctx.getCid(), 1, maxPage))
                        .queue();
            }
            case MANAGE -> {
                event.replyModal(ManageBuilderUtils.sessionSearchModal(ctx.getCid()))
                        .queue();
            }
            case PAGE -> {
                final var sessions = this.sessionDB.getAll();
                final int maxPage = (sessions.size() + 4) / 5;
                final int currPage = ctx.getAsInt("page");

                final int startIdx = (currPage - 1) * 5;
                final int endIdx = Math.min(startIdx + 5, sessions.size());

                event.editMessageEmbeds(SessionBuilderUtils.historyMessage(Toolbox.select(sessions, startIdx, endIdx), currPage, maxPage))
                        .setComponents(ManageBuilderUtils.sessionPageComponent(ctx.getCid(), currPage, maxPage))
                        .queue();
            }
        }
    }

    private void handleReportChoice(final ButtonInteractionEvent event, final ReportButtonType choice, final CommandContext ctx) {
        switch (choice) {
            case OVERVIEW -> {
                event.editMessageEmbeds(ReportBuilderUtils.overviewMessage(this.reportDB.resolveFromWeek(2)))
                        .setComponents(ManageBuilderUtils.reportOverviewComponent(ctx.getCid()))
                        .queue();
            }
            case HISTORY -> {
                final int maxPage = (int) Math.ceil((double) this.reportDB.getCount() / 5);

                event.editMessageEmbeds(ReportBuilderUtils.historyMessage(this.reportDB.resolveFromWeek(5), 1, maxPage))
                        .setComponents(ManageBuilderUtils.reportPageComponent(ctx.getCid(), 1, maxPage))
                        .queue();
            }
            case MANAGE -> {
                event.replyModal(ManageBuilderUtils.reportSearchModal(ctx.getCid()))
                        .queue();
            }
            case PAGE -> {
                final var reports = this.reportDB.getAll();
                final int maxPage = (reports.size() + 4) / 5;
                final int currPage = ctx.getAsInt("page");

                final int startIdx = (currPage - 1) * 5;
                final int endIdx = Math.min(startIdx + 5, reports.size());

                event.editMessageEmbeds(ReportBuilderUtils.historyMessage(Toolbox.select(reports, startIdx, endIdx), currPage, maxPage))
                        .setComponents(ManageBuilderUtils.reportPageComponent(ctx.getCid(), currPage, maxPage))
                        .queue();
            }
        }
    }

    private void handleEmployeeChoice(final ButtonInteractionEvent event, final WorkforceButtonType choice, final String cid) {
        switch (choice) {
            case STATS -> event.editMessageEmbeds(ManageBuilderUtils.employeeStatsView())
                    .setComponents(ManageBuilderUtils.employeeStatsComponent(cid))
                    .queue();

            case OVERVIEW -> event.editMessageEmbeds(ManageBuilderUtils.workforceOverviewMessage())
                    .setComponents(ManageBuilderUtils.workforceOverviewComponent(cid))
                    .queue();

            case MANAGE -> event.replyModal(ManageBuilderUtils.employeeSearchModal(cid))
                    .queue();
        }
    }
}
