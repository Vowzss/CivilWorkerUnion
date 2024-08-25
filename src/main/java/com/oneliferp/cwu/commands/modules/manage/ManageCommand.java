package com.oneliferp.cwu.commands.modules.manage;

import com.oneliferp.cwu.cache.ProfileCache;
import com.oneliferp.cwu.commands.CwuCommand;
import com.oneliferp.cwu.commands.modules.manage.misc.actions.ManageModalType;
import com.oneliferp.cwu.commands.modules.manage.misc.actions.WorkforceButtonType;
import com.oneliferp.cwu.commands.modules.manage.utils.ManageBuilderUtils;
import com.oneliferp.cwu.commands.modules.profile.exceptions.ProfileNotFoundException;
import com.oneliferp.cwu.commands.modules.profile.exceptions.ProfilePermissionException;
import com.oneliferp.cwu.commands.modules.profile.exceptions.ProfileValidationException;
import com.oneliferp.cwu.commands.modules.profile.misc.ProfilePageType;
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
import com.oneliferp.cwu.exceptions.IdentityException;
import com.oneliferp.cwu.exceptions.IdentityMalformedException;
import com.oneliferp.cwu.models.IdentityModel;
import com.oneliferp.cwu.utils.SimpleDate;
import com.oneliferp.cwu.utils.Toolbox;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.Objects;

public class ManageCommand extends CwuCommand {
    private final ProfileCache profileCache;
    private final ProfileDatabase profileDB;

    private final SessionDatabase sessionDB;
    private final ReportDatabase reportDB;

    public ManageCommand() {
        super("manage", "gestion", "Vous permet de gérer les différents aspect de la CWU.");

        this.profileCache = ProfileCache.get();
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
            default:
                throw new IllegalArgumentException();
            case "effectif": {
                event.replyEmbeds(ManageBuilderUtils.workforceOverviewMessage())
                        .setComponents(ManageBuilderUtils.workforceOverviewComponent(profile.getCid()))
                        .setEphemeral(true).queue();
                break;
            }
            case "session": {
                event.replyEmbeds(ManageBuilderUtils.sessionOverviewMessage(this.sessionDB.resolveFromWeek(2)))
                        .setComponents(ManageBuilderUtils.sessionOverviewComponent(profile.getCid()))
                        .setEphemeral(true).queue();
                break;
            }
            case "rapport": {
                event.replyEmbeds(ManageBuilderUtils.reportOverviewMessage(this.reportDB.resolveFromWeek(2)))
                        .setComponents(ManageBuilderUtils.reportOverviewComponent(profile.getCid()))
                        .setEphemeral(true).queue();
                break;
            }
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
            this.handleEmployeeChoice(event, choice, ctx);
        } else throw new IllegalArgumentException();
    }

    @Override
    public void handleModalInteraction(final ModalInteractionEvent event, final CommandContext ctx) throws CwuException {
        final ProfileModel profile = this.profileDB.getFromCid(ctx.getCid());
        if (profile == null) throw new ProfileNotFoundException(event);

        final String content = Objects.requireNonNull(event.getValue("cwu_manage.fill/data")).getAsString();

        switch ((ManageModalType) ctx.getEnumType()) {
            default -> throw new IllegalArgumentException();
            case FILL_IDENTITY -> {
                final ProfileModel employee = this.profileCache.get(profile.getCid());
                if (employee == null) throw new ProfileNotFoundException(event);

                try {
                    profile.setIdentity(IdentityModel.parseIdentity(content));
                } catch (IdentityMalformedException e) {
                    throw new IdentityException(event);
                }
                this.employeeGoToPage(event, employee, profile.getCid());
            }
            case FILL_ID -> {
                final ProfileModel employee = this.profileCache.get(profile.getCid());
                if (employee == null) throw new ProfileNotFoundException(event);

                employee.setId(Long.parseLong(content));
                this.employeeGoToPage(event, employee, profile.getCid());
            }
            case FILL_JOINED_AT -> {
                final ProfileModel employee = this.profileCache.get(profile.getCid());
                if (employee == null) throw new ProfileNotFoundException(event);

                employee.setJoinedAt(SimpleDate.parseDate(content));
                this.employeeGoToPage(event, employee, profile.getCid());
            }
            case PROFILE_SEARCH -> {
                final ProfileModel employee = this.profileDB.getFromCid(content);
                if (employee == null) throw new ProfileNotFoundException(event);

                event.editMessageEmbeds(ProfileBuilderUtils.displayMessage(employee))
                        .setComponents(ProfileBuilderUtils.displayComponent(employee.getCid()))
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
                event.editMessageEmbeds(ManageBuilderUtils.sessionOverviewMessage(this.sessionDB.resolveFromWeek(2)))
                        .setComponents(ManageBuilderUtils.sessionOverviewComponent(ctx.getCid()))
                        .queue();
            }
            case HISTORY -> {
                final int maxPage = (int) Math.ceil((double) this.sessionDB.getCount() / 5);

                event.editMessageEmbeds(ManageBuilderUtils.sessionPageMessage(this.sessionDB.resolveFromWeek(5), 1, maxPage))
                        .setComponents(ManageBuilderUtils.sessionPageComponent(ctx.getCid(), 1, maxPage))
                        .queue();
            }
            case MANAGE -> {
                event.replyModal(ManageBuilderUtils.sessionSearchModal(ctx.getCid()))
                        .queue();
            }
            case PAGE -> {
                final var sessions = this.sessionDB.getAll();

                final int pageIndex = ctx.getAsInt("index");
                final int maxPage = (sessions.size() + 4) / 5;

                final int startIdx = (pageIndex - 1) * 5;
                final int endIdx = Math.min(startIdx + 5, sessions.size());

                event.editMessageEmbeds(ManageBuilderUtils.sessionPageMessage(Toolbox.select(sessions, startIdx, endIdx), pageIndex, maxPage))
                        .setComponents(ManageBuilderUtils.sessionPageComponent(ctx.getCid(), pageIndex, maxPage))
                        .queue();
            }
        }
    }

    private void handleReportChoice(final ButtonInteractionEvent event, final ReportButtonType choice, final CommandContext ctx) {
        switch (choice) {
            case OVERVIEW -> {
                event.editMessageEmbeds(ManageBuilderUtils.reportOverviewMessage(this.reportDB.resolveFromWeek(2)))
                        .setComponents(ManageBuilderUtils.reportOverviewComponent(ctx.getCid()))
                        .queue();
            }
            case HISTORY -> {
                final int maxPage = (int) Math.ceil((double) this.reportDB.getCount() / 5);

                event.editMessageEmbeds(ManageBuilderUtils.reportPageMessage(this.reportDB.resolveFromWeek(5), 1, maxPage))
                        .setComponents(ManageBuilderUtils.reportPageComponent(ctx.getCid(), 1, maxPage))
                        .queue();
            }
            case MANAGE -> {
                event.replyModal(ManageBuilderUtils.reportSearchModal(ctx.getCid()))
                        .queue();
            }
            case PAGE -> {
                final var reports = this.reportDB.getAll();

                final int pageIndex = ctx.getAsInt("index");
                final int maxPage = (reports.size() + 4) / 5;

                final int startIndex = (pageIndex - 1) * 5;
                final int endIndex = Math.min(startIndex + 5, reports.size());

                event.editMessageEmbeds(ManageBuilderUtils.reportPageMessage(Toolbox.select(reports, startIndex, endIndex), pageIndex, maxPage))
                        .setComponents(ManageBuilderUtils.reportPageComponent(ctx.getCid(), pageIndex, maxPage))
                        .queue();
            }
        }
    }

    private void handleEmployeeChoice(final ButtonInteractionEvent event, final WorkforceButtonType choice, final CommandContext ctx) throws CwuException {
        switch (choice) {
            default -> {
                System.out.println();
            }
            case STATS -> {
                event.editMessageEmbeds(ManageBuilderUtils.employeeStatsView())
                        .setComponents(ManageBuilderUtils.employeeStatsComponent(ctx.getCid()))
                        .queue();
            }
            case OVERVIEW -> {
                event.editMessageEmbeds(ManageBuilderUtils.workforceOverviewMessage())
                        .setComponents(ManageBuilderUtils.workforceOverviewComponent(ctx.getCid()))
                        .queue();
            }
            case MANAGE -> {
                event.replyModal(ManageBuilderUtils.employeeSearchModal(ctx.getCid()))
                        .queue();
            }

            case CREATE -> {
                if (this.profileCache.exist(ctx.getCid())) {
                    final ProfileModel profile = this.profileCache.get(ctx.getCid());
                    event.editMessageEmbeds(ManageBuilderUtils.employeeAlreadyExistMessage(profile.getIdentity(), profile.getId()))
                            .setComponents(ManageBuilderUtils.employeeResumeOrOverwriteComponent(profile.getCid()))
                            .queue();
                    return;
                }

                event.editMessageEmbeds(ManageBuilderUtils.createEmployeeMessage())
                        .setComponents(ManageBuilderUtils.createEmployeeComponent(ctx.getCid()))
                        .queue();
            }
            case ABORT -> {
                event.reply("Vous avez annuler la création du profil employé.")
                        .setEphemeral(true).queue();
                event.getMessage().delete().queue();
            }
            case SUBMIT -> {
                final ProfileModel employee = this.profileCache.get(ctx.getCid());
                if (employee == null) throw new ProfileNotFoundException(event);
                if (!employee.verify()) throw new ProfileValidationException(event);

                this.profileDB.addOne(employee);
                this.profileDB.save();

                this.profileCache.remove(ctx.getCid());

                event.reply("Création de l'employé avec succès.")
                        .setEphemeral(true).queue();
                event.getMessage().delete().queue();
            }
            case RESUME, EDIT -> {
                final ProfileModel employee = this.profileCache.get(ctx.getCid());
                if (employee == null) throw new ProfileNotFoundException(event);

                employee.goFirstPage();
                this.employeeGoToPage(event, employee, ctx.getCid());
            }
            case OVERWRITE, START -> {
                final String cid = ctx.getCid();
                final ProfileModel employee = new ProfileModel();
                this.profileCache.add(cid, employee);

                employee.start();
                employee.goFirstPage();
                this.employeeGoToPage(event, employee, ctx.getCid());
            }
            case CLEAR -> {
                final ProfileModel employee = this.profileCache.get(ctx.getCid());
                if (employee == null) throw new ProfileNotFoundException(event);

                this.employeeHandleClearButton(event, employee, ctx.getCid());
            }
            case FILL -> {
                final ProfileModel employee = this.profileCache.get(ctx.getCid());
                if (employee == null) throw new ProfileNotFoundException(event);

                this.employeeHandleFillButton(event, employee, ctx.getCid());
            }
            case PREVIEW -> {
                final ProfileModel employee = this.profileCache.get(ctx.getCid());
                if (employee == null) throw new ProfileNotFoundException(event);

                employee.goPreviewPage();
                this.employeeGoToPage(event, employee, ctx.getCid());
            }
            case PAGE_NEXT -> {
                final ProfileModel employee = this.profileCache.get(ctx.getCid());
                if (employee == null) throw new ProfileNotFoundException(event);

                employee.goNextPage();
                this.employeeGoToPage(event, employee, ctx.getCid());
            }
            case PAGE_PREV -> {
                final ProfileModel employee = this.profileCache.get(ctx.getCid());
                if (employee == null) throw new ProfileNotFoundException(event);

                employee.goPrevPage();
                this.employeeGoToPage(event, employee, ctx.getCid());
            }
        }
    }

    /* Utils */
    protected void employeeGoToPage(final IMessageEditCallback event, final ProfileModel profile, final String managerCid) {
        final ProfilePageType page = profile.getCurrentPage();

        final var callback = (switch (page) {
            case IDENTITY -> event.editMessageEmbeds(ManageBuilderUtils.employeeIdentityMessage(profile.getIdentity()))
                    .setComponents(ManageBuilderUtils.employeeIdentityComponent(managerCid));
            case ID -> event.editMessageEmbeds(ManageBuilderUtils.profileIdMessage(profile.getId()))
                    .setComponents(ManageBuilderUtils.employeeIdComponent(managerCid));
            case BRANCH -> event.editMessageEmbeds(ManageBuilderUtils.employeeBranchMessage(profile.getBranch()))
                    .setComponents(ManageBuilderUtils.employeeBranchComponents(managerCid, profile.getBranch()));
            case RANK -> event.editMessageEmbeds(ManageBuilderUtils.profileRankMessage(profile.getRank()))
                    .setComponents(ManageBuilderUtils.employeeRankComponents(managerCid, profile.getRank()));
            case JOINED_AT -> event.editMessageEmbeds(ManageBuilderUtils.employeeJoinedAtMessage(profile.getJoinedAt()))
                    .setComponents(ManageBuilderUtils.employeeJoinedAtComponent(managerCid));
            case PREVIEW -> event.editMessageEmbeds(ManageBuilderUtils.employeePreviewMessage(profile))
                    .setComponents(ManageBuilderUtils.employeePreviewComponents(managerCid));
        });

        callback.queue();
    }

    private void employeeHandleClearButton(final ButtonInteractionEvent event, final ProfileModel profile, final String managerCid) {
        switch (profile.getCurrentPage()) {
            case IDENTITY -> profile.setIdentity(null);
            case ID -> profile.setId(null);
            case BRANCH -> profile.setBranch(null);
            case RANK -> profile.setRank(null);
            case JOINED_AT -> profile.setJoinedAt(null);
        }

        this.employeeGoToPage(event, profile, managerCid);
    }

    private void employeeHandleFillButton(final ButtonInteractionEvent event, final ProfileModel profile, final String managerCid) {
        final var currentPage = profile.getCurrentPage();
        final TextInput.Builder inputBuilder = TextInput.create("cwu_manage.fill/data", currentPage.getDescription(), TextInputStyle.SHORT)
                .setRequired(true);

        final var modalType = switch (currentPage) {
            default -> throw new IllegalArgumentException();
            case IDENTITY -> {
                inputBuilder.setPlaceholder("ex: Nom Prénom, #12345");
                yield ManageModalType.FILL_IDENTITY;
            }
            case ID -> {
                inputBuilder.setPlaceholder("ex: 1262848159386828811");
                yield ManageModalType.FILL_ID;
            }
            case JOINED_AT -> {
                inputBuilder.setPlaceholder("ex: 01/01/1999");
                yield ManageModalType.FILL_JOINED_AT;
            }
        };

        final Modal modal = Modal.create(modalType.build(managerCid), "Ajout des informations")
                .addComponents(ActionRow.of(inputBuilder.build()))
                .build();

        event.replyModal(modal).queue();
    }
}
