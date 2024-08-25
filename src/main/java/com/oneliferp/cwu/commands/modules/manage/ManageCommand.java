package com.oneliferp.cwu.commands.modules.manage;

import com.oneliferp.cwu.cache.ProfileCache;
import com.oneliferp.cwu.commands.CwuCommand;
import com.oneliferp.cwu.commands.modules.manage.misc.actions.ManageModalType;
import com.oneliferp.cwu.commands.modules.manage.misc.actions.ReportChoiceType;
import com.oneliferp.cwu.commands.modules.manage.misc.actions.SessionChoiceType;
import com.oneliferp.cwu.commands.modules.manage.misc.actions.WorkforceButtonType;
import com.oneliferp.cwu.commands.modules.manage.utils.ManageBuilderUtils;
import com.oneliferp.cwu.commands.modules.profile.exceptions.ProfileNotFoundException;
import com.oneliferp.cwu.commands.modules.profile.exceptions.ProfileValidationException;
import com.oneliferp.cwu.commands.modules.profile.misc.ProfilePageType;
import com.oneliferp.cwu.commands.modules.profile.models.ProfileModel;
import com.oneliferp.cwu.commands.modules.profile.utils.ProfileBuilderUtils;
import com.oneliferp.cwu.commands.utils.CommandContext;
import com.oneliferp.cwu.database.ProfileDatabase;
import com.oneliferp.cwu.exceptions.CwuException;
import com.oneliferp.cwu.exceptions.IdentityException;
import com.oneliferp.cwu.exceptions.IdentityMalformedException;
import com.oneliferp.cwu.models.IdentityModel;
import com.oneliferp.cwu.utils.SimpleDate;
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
    private final ProfileDatabase profileDatabase;

    public ManageCommand() {
        super("manage", "gestion", "Vous permet de gérer les différents aspect de la CWU.");

        this.profileCache = ProfileCache.get();
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
            default:
                throw new IllegalArgumentException();
            case "effectif": {
                event.replyEmbeds(ManageBuilderUtils.workforceOverviewMessage())
                        .setComponents(ManageBuilderUtils.workforceOverviewComponent(cwu.getCid()))
                        .setEphemeral(true).queue();
                break;
            }
            case "session": {
                event.replyEmbeds(ManageBuilderUtils.sessionOverviewComponent())
                        .setComponents(ManageBuilderUtils.sessionOverviewComponent(cwu.getCid()))
                        .setEphemeral(true).queue();
                break;
            }
            case "rapport": {
                event.replyEmbeds(ManageBuilderUtils.reportsLatestMessage())
                        .setComponents(ManageBuilderUtils.reportsOverviewComponent(cwu.getCid()))
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
        } else if (type instanceof WorkforceButtonType choice) {
            this.handleEmployeeChoice(event, choice, ctx);
        } else throw new IllegalArgumentException();
    }

    @Override
    public void handleModalInteraction(final ModalInteractionEvent event, final CommandContext ctx) throws CwuException {
        final ProfileModel profile = this.profileDatabase.getFromCid(ctx.getCid());
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
                event.editMessageEmbeds(ProfileBuilderUtils.infoMessage(profile))
                        .setComponents(ProfileBuilderUtils.infoComponent(profile.getCid()))
                        .queue();
            }
        }
    }

    /* Handlers */
    private void handleSessionChoice(final ButtonInteractionEvent event, final SessionChoiceType choice, final String cid) {
        switch (choice) {
            case OVERVIEW -> {
                event.editMessageEmbeds(ManageBuilderUtils.sessionOverviewComponent())
                        .setComponents(ManageBuilderUtils.sessionOverviewComponent(cid)).queue();
            }
            case STATS -> {
                event.editMessageEmbeds(ManageBuilderUtils.sessionStatsMessage())
                        .setComponents(ManageBuilderUtils.sessionOverviewComponent(cid)).queue();
            }
            case MANAGE -> {
                event.replyModal(ManageBuilderUtils.sessionSearchModal(cid)).queue();
            }
        }
    }

    private void handleReportChoice(final ButtonInteractionEvent event, final ReportChoiceType choice, final String cid) {
        switch (choice) {
            // TODO
            case OVERVIEW, MANAGE -> {
                event.replyModal(ManageBuilderUtils.reportSearchModal(cid)).queue();
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

                this.profileDatabase.addOne(employee);
                this.profileDatabase.save();

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
