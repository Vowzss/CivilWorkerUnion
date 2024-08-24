package com.oneliferp.cwu.commands.modules.manage;

import com.oneliferp.cwu.cache.EmployeeCache;
import com.oneliferp.cwu.commands.utils.CommandContext;
import com.oneliferp.cwu.commands.CwuCommand;
import com.oneliferp.cwu.commands.modules.manage.exceptions.EmployeeNotFoundException;
import com.oneliferp.cwu.commands.modules.manage.exceptions.EmployeeValidationException;
import com.oneliferp.cwu.commands.modules.manage.misc.EmployeePageType;
import com.oneliferp.cwu.commands.modules.manage.misc.actions.EmployeeChoiceType;
import com.oneliferp.cwu.commands.modules.manage.misc.actions.ManageModalType;
import com.oneliferp.cwu.commands.modules.manage.misc.actions.ReportChoiceType;
import com.oneliferp.cwu.commands.modules.manage.misc.actions.SessionChoiceType;
import com.oneliferp.cwu.commands.modules.manage.models.EmployeeModel;
import com.oneliferp.cwu.commands.modules.manage.utils.ManageBuilderUtils;
import com.oneliferp.cwu.commands.modules.profile.exceptions.ProfileNotFoundException;
import com.oneliferp.cwu.commands.modules.profile.models.ProfileModel;
import com.oneliferp.cwu.commands.modules.profile.utils.ProfileBuilderUtils;
import com.oneliferp.cwu.database.ProfileDatabase;
import com.oneliferp.cwu.exceptions.CwuException;
import com.oneliferp.cwu.exceptions.IdentityException;
import com.oneliferp.cwu.exceptions.IdentityMalformedException;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.misc.CwuRank;
import com.oneliferp.cwu.models.IdentityModel;
import com.oneliferp.cwu.utils.SimpleDate;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.Objects;

public class ManageCommand extends CwuCommand {
    private final EmployeeCache employeeCache;
    private final ProfileDatabase profileDatabase;

    public ManageCommand() {
        super("manage", "gestion", "Vous permet de gérer les différents aspect de la CWU.");

        this.employeeCache = EmployeeCache.get();
        this.profileDatabase = ProfileDatabase.get();
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
    public void handleCommandInteraction(final SlashCommandInteractionEvent event) throws ProfileNotFoundException {
        final ProfileModel cwu = this.profileDatabase.getFromId(event.getUser().getIdLong());
        if (cwu == null) throw new ProfileNotFoundException(event);

        switch (Objects.requireNonNull(event.getSubcommandName())) {
            default: throw new IllegalArgumentException();
            case "effectif": {
                event.replyEmbeds(ManageBuilderUtils.employeeSummaryMessage())
                        .setComponents(ManageBuilderUtils.employeeSummaryComponent(cwu.getCid()))
                        .setEphemeral(true).queue();
                break;
            }
            case "session": {
                event.replyEmbeds(ManageBuilderUtils.sessionsSummaryMessage())
                        .setComponents(ManageBuilderUtils.sessionsSummaryMessage(cwu.getCid()))
                        .setEphemeral(true).queue();
                break;
            }
            case "rapport": {
                event.replyEmbeds(ManageBuilderUtils.reportsLatestMessage())
                        .setComponents(ManageBuilderUtils.reportsComponent(cwu.getCid()))
                        .setEphemeral(true).queue();
                break;
            }
        }
    }

    @Override
    public void handleButtonInteraction(final ButtonInteractionEvent event, final CommandContext ctx) throws CwuException {
        final ProfileModel cwu = this.profileDatabase.getFromCid(ctx.getCid());
        if (cwu == null) throw new ProfileNotFoundException(event);

        final var type = ctx.getEnumType();
        if (type instanceof SessionChoiceType choice) {
            this.handleSessionChoice(event, choice, ctx.getCid());
        } else if (type instanceof ReportChoiceType choice) {
            this.handleReportChoice(event, choice, ctx.getCid());
        } else if (type instanceof EmployeeChoiceType choice) {
            this.handleEmployeeChoice(event, choice, ctx);
        } else throw new IllegalArgumentException();
    }

    @Override
    public void handleModalInteraction(final ModalInteractionEvent event, final CommandContext ctx) throws CwuException {
        final ProfileModel cwu = this.profileDatabase.getFromCid(ctx.getCid());
        if (cwu == null) throw new ProfileNotFoundException(event);

        final String content = Objects.requireNonNull(event.getValue("cwu_manage.fill/data")).getAsString();

        final ManageModalType type = ctx.getEnumType();
        switch (type) {
            default -> throw new IllegalArgumentException();
            case FILL_IDENTITY -> {
                final EmployeeModel employee = this.employeeCache.get(ctx.getCid());
                if (employee == null) throw new EmployeeNotFoundException(event);

                try {
                    employee.setIdentity(IdentityModel.parseIdentity(content));
                } catch (IdentityMalformedException e) {
                    throw new IdentityException(event);
                }
                this.employeeGoToPage(event, employee);
            }
            case FILL_ID -> {
                final EmployeeModel employee = this.employeeCache.get(ctx.getCid());
                if (employee == null) throw new EmployeeNotFoundException(event);

                employee.setId(Long.parseLong(content));
                this.employeeGoToPage(event, employee);
            }
            case FILL_JOINED_AT -> {
                final EmployeeModel employee = this.employeeCache.get(ctx.getCid());
                if (employee == null) throw new EmployeeNotFoundException(event);

                employee.setJoinedAt(SimpleDate.parseDate(content));
                this.employeeGoToPage(event, employee);
            }
            case EMPLOYEE_SEARCH -> {
                final ProfileModel profile = this.profileDatabase.getFromCid(content);
                if (profile == null) throw new ProfileNotFoundException(event);

                event.editMessageEmbeds(ProfileBuilderUtils.profileMessage(profile))
                        .setComponents(ProfileBuilderUtils.statsAndDeleteComponent(profile.getCid()))
                        .queue();
            }
        }
    }

    @Override
    public void handleSelectionInteraction(final StringSelectInteractionEvent event, final CommandContext ctx) throws CwuException {
        final ProfileModel cwu = this.profileDatabase.getFromCid(ctx.getCid());
        if (cwu == null) throw new ProfileNotFoundException(event);

        final EmployeeModel employee = this.employeeCache.get(ctx.getCid());
        if (employee == null) throw new EmployeeNotFoundException(event);

        final String content = Objects.requireNonNull(event.getValues().get(0));
        switch (ctx.specifiers.get(1)) {
            default -> throw new IllegalArgumentException();
            case "branch" -> employee.setBranch(CwuBranch.valueOf(content.toUpperCase()));
            case "rank" -> employee.setRank(CwuRank.valueOf(content.toUpperCase()));
        }

        this.employeeGoToPage(event, employee);
    }

    /* Handlers */
    private void handleSessionChoice(final ButtonInteractionEvent event, final SessionChoiceType choice, final String cid) {
        switch (choice) {
            case SUMMARY -> {
                event.editMessageEmbeds(ManageBuilderUtils.sessionsSummaryMessage())
                        .setComponents(ManageBuilderUtils.sessionsSummaryMessage(cid)).queue();
            }
            case STATS -> {
                event.editMessageEmbeds(ManageBuilderUtils.sessionStatsMessage())
                        .setComponents(ManageBuilderUtils.sessionStatsComponent(cid)).queue();
            }
            case MANAGE -> {
                event.replyModal(ManageBuilderUtils.sessionSearchModal(cid)).queue();
            }
        }
    }

    private void handleReportChoice(final ButtonInteractionEvent event, final ReportChoiceType choice, final String cid) {
        switch (choice) {
            // TODO
            case SUMMARY, MANAGE -> {
                event.replyModal(ManageBuilderUtils.reportSearchModal(cid)).queue();
            }
        }
    }

    private void handleEmployeeChoice(final ButtonInteractionEvent event, final EmployeeChoiceType choice, final CommandContext ctx) throws CwuException {
        switch (choice) {
            case SUMMARY -> {
                event.replyEmbeds(ManageBuilderUtils.employeeSummaryMessage())
                        .setComponents(ManageBuilderUtils.employeeSummaryComponent(ctx.getCid()))
                        .setEphemeral(true).queue();
            }
            case STATS -> {
                event.editMessageEmbeds(ManageBuilderUtils.employeeStatsView())
                        .setComponents(ManageBuilderUtils.employeeStatsComponent(ctx.getCid()))
                        .queue();
            }
            case MANAGE -> {
                event.replyModal(ManageBuilderUtils.employeeSearchModal(ctx.getCid()))
                        .queue();
            }

            case CREATE -> {
                final String cid = ctx.getCid();

                if (this.employeeCache.exist(cid)) {
                    final EmployeeModel employeeModel = this.employeeCache.get(cid);
                    event.editMessageEmbeds(ManageBuilderUtils.employeeAlreadyExistMessage(employeeModel.getIdentity(), employeeModel.getId()))
                            .setComponents(ManageBuilderUtils.employeeResumeOrOverwriteComponent(cid))
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
                final EmployeeModel employee = this.employeeCache.get(ctx.getCid());
                if (employee == null) throw new EmployeeNotFoundException(event);
                if (!employee.isValid()) throw new EmployeeValidationException(event);

                this.profileDatabase.addOne(employee.toProfile());
                this.profileDatabase.save();

                this.employeeCache.remove(employee.getManagerCid());

                event.reply("Création de l'employé avec succès.")
                        .setEphemeral(true).queue();
                event.getMessage().delete().queue();
            }
            case RESUME, EDIT -> {
                final EmployeeModel employee = this.employeeCache.get(ctx.getCid());
                if (employee == null) throw new EmployeeNotFoundException(event);

                employee.goFirstPage();
                this.employeeGoToPage(event, employee);
            }
            case OVERWRITE, START -> {
                final String cid = ctx.getCid();
                final EmployeeModel employee = new EmployeeModel(cid);
                this.employeeCache.add(cid, employee);
                employee.begin();

                employee.goFirstPage();
                this.employeeGoToPage(event, employee);
            }
            case CLEAR -> {
                final EmployeeModel employee = this.employeeCache.get(ctx.getCid());
                if (employee == null) throw new EmployeeNotFoundException(event);

                this.employeeHandleClearButton(event, employee);
            }
            case FILL -> {
                final EmployeeModel employee = this.employeeCache.get(ctx.getCid());
                if (employee == null) throw new EmployeeNotFoundException(event);

                this.employeeHandleFillButton(event, employee);
            }
            case PREVIEW -> {
                final EmployeeModel employee = this.employeeCache.get(ctx.getCid());
                if (employee == null) throw new EmployeeNotFoundException(event);

                employee.goPreviewPage();
                this.employeeGoToPage(event, employee);
            }
            case PAGE_NEXT -> {
                final EmployeeModel employee = this.employeeCache.get(ctx.getCid());
                if (employee == null) throw new EmployeeNotFoundException(event);

                employee.goNextPage();
                this.employeeGoToPage(event, employee);
            }
            case PAGE_PREV -> {
                final EmployeeModel employee = this.employeeCache.get(ctx.getCid());
                if (employee == null) throw new EmployeeNotFoundException(event);

                employee.goPrevPage();
                this.employeeGoToPage(event, employee);
            }
        }
    }

    /* Utils */
    protected void employeeGoToPage(final IMessageEditCallback event, final EmployeeModel employee) {
        final EmployeePageType page = employee.getCurrentPage();

        final var callback = (switch (page) {
            case IDENTITY -> event.editMessageEmbeds(ManageBuilderUtils.employeeIdentityMessage(employee.getIdentity()))
                    .setComponents(ManageBuilderUtils.profileIdentityComponent(employee.getManagerCid()));
            case ID -> event.editMessageEmbeds(ManageBuilderUtils.profileIdMessage(employee.getId()))
                    .setComponents(ManageBuilderUtils.profileIdComponent(employee.getManagerCid()));
            case BRANCH -> event.editMessageEmbeds(ManageBuilderUtils.employeeBranchMessage(employee.getBranch()))
                    .setComponents(ManageBuilderUtils.profileBranchComponents(employee.getManagerCid(), employee.getBranch()));
            case RANK -> event.editMessageEmbeds(ManageBuilderUtils.profileRankMessage(employee.getRank()))
                    .setComponents(ManageBuilderUtils.profileRankComponents(employee.getManagerCid(), employee.getRank()));
            case JOINED_AT -> event.editMessageEmbeds(ManageBuilderUtils.employeeJoinedAtMessage(employee.getJoinedAt()))
                    .setComponents(ManageBuilderUtils.profileJoinedAtComponent(employee.getManagerCid()));
            case PREVIEW -> event.editMessageEmbeds(ManageBuilderUtils.employeePreviewMessage(employee))
                    .setComponents(ManageBuilderUtils.profilePreviewComponents(employee.getManagerCid()));
        });

        callback.queue();
    }

    private void employeeHandleClearButton(final ButtonInteractionEvent event, final EmployeeModel employee) {
        switch (employee.getCurrentPage()) {
            case IDENTITY -> employee.setIdentity(null);
            case ID -> employee.setId(null);
            case BRANCH -> employee.setBranch(null);
            case RANK -> employee.setRank(null);
            case JOINED_AT -> employee.setJoinedAt(null);
        }

        this.employeeGoToPage(event, employee);
    }

    private void employeeHandleFillButton(final ButtonInteractionEvent event, final EmployeeModel employee) {
        final var currentPage = employee.getCurrentPage();
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

        final Modal modal = Modal.create(modalType.build(employee.getManagerCid()), "Ajout des informations")
                .addComponents(ActionRow.of(inputBuilder.build()))
                .build();

        event.replyModal(modal).queue();
    }
}
