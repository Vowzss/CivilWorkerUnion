package com.oneliferp.cwu.commands.manage;

import com.oneliferp.cwu.cache.EmployeeCache;
import com.oneliferp.cwu.commands.CommandContext;
import com.oneliferp.cwu.commands.CwuCommand;
import com.oneliferp.cwu.commands.manage.exceptions.EmployeeNotFoundException;
import com.oneliferp.cwu.commands.manage.exceptions.EmployeeValidationException;
import com.oneliferp.cwu.commands.manage.misc.EmployeePageType;
import com.oneliferp.cwu.commands.manage.misc.actions.GestionModalType;
import com.oneliferp.cwu.commands.manage.misc.actions.ProfileChoiceType;
import com.oneliferp.cwu.commands.manage.misc.actions.ReportChoiceType;
import com.oneliferp.cwu.commands.manage.misc.actions.SessionChoiceType;
import com.oneliferp.cwu.commands.manage.models.EmployeeModel;
import com.oneliferp.cwu.commands.manage.utils.ManageBuilderUtils;
import com.oneliferp.cwu.commands.profile.exceptions.ProfileNotFoundException;
import com.oneliferp.cwu.commands.profile.models.ProfileModel;
import com.oneliferp.cwu.commands.report.utils.ReportBuilderUtils;
import com.oneliferp.cwu.database.ProfileDatabase;
import com.oneliferp.cwu.exceptions.CwuException;
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
        final SubcommandData createProfileCommand = new SubcommandData("profil", "Vous permet de gérer les employés de la CWU.");
        final SubcommandData sessionSubCommand = new SubcommandData("session", "Vous permet de gérer les sessions de travail.");
        final SubcommandData reportSubCommand = new SubcommandData("rapport", "Vous permet de gérer les rapports.");
        slashCommand.addSubcommands(createProfileCommand, sessionSubCommand, reportSubCommand);
        return super.configure(slashCommand);
    }

    @Override
    public void handleCommandInteraction(SlashCommandInteractionEvent event) throws ProfileNotFoundException {
        final ProfileModel cwu = this.profileDatabase.getFromId(event.getUser().getIdLong());
        if (cwu == null) throw new ProfileNotFoundException();

        switch (Objects.requireNonNull(event.getSubcommandName())) {
            default:
                throw new IllegalArgumentException();
            case "profil": {
                event.replyEmbeds(ManageBuilderUtils.profileView())
                        .setComponents(ManageBuilderUtils.profileActionRow(cwu.getCid()))
                        .queue();
                break;
            }
            case "session": {
                event.replyEmbeds(ManageBuilderUtils.sessionView())
                        .setComponents(ManageBuilderUtils.sessionActionRow(cwu.getCid()))
                        .queue();
                break;
            }
            case "rapport": {
                event.replyEmbeds(ManageBuilderUtils.reportView())
                        .setComponents(ManageBuilderUtils.reportActionRow(cwu.getCid()))
                        .queue();
                break;
            }
        }
    }

    @Override
    public void handleButtonInteraction(final ButtonInteractionEvent event, final CommandContext ctx) throws CwuException {
        final ProfileModel cwu = this.profileDatabase.getFromCid(ctx.getCid());
        if (cwu == null) throw new ProfileNotFoundException();

        final var type = ctx.getEnumType();
        if (type instanceof SessionChoiceType choice) {
            this.handleSessionChoice(event, choice, ctx.getCid());
        } else if (type instanceof ReportChoiceType choice) {
            this.handleReportChoice(event, choice, ctx.getCid());
        } else if (type instanceof ProfileChoiceType choice) {
            this.handleProfileChoice(event, choice, ctx);
        } else throw new IllegalArgumentException();
    }

    @Override
    public void handleModalInteraction(final ModalInteractionEvent event, final CommandContext ctx) throws CwuException {
        final ProfileModel cwu = this.profileDatabase.getFromCid(ctx.getCid());
        if (cwu == null) throw new ProfileNotFoundException();

        final EmployeeModel employee = this.employeeCache.get(ctx.getCid());
        if (employee == null) throw new EmployeeNotFoundException();

        final String content = Objects.requireNonNull(event.getValue("cwu_manage.fill/data")).getAsString();

        final GestionModalType type = ctx.getEnumType();
        switch (type) {
            default -> throw new IllegalArgumentException();
            case FILL_IDENTITY -> employee.setIdentity(IdentityModel.parseIdentity(content));
            case FILL_ID -> employee.setId(Long.parseLong(content));
            case FILL_JOINED_AT -> employee.setJoinedAt(SimpleDate.parseDate(content));
        }

        this.goToPage(event, employee);
    }

    @Override
    public void handleSelectionInteraction(final StringSelectInteractionEvent event, final CommandContext ctx) throws CwuException {
        final ProfileModel cwu = this.profileDatabase.getFromCid(ctx.getCid());
        if (cwu == null) throw new ProfileNotFoundException();

        final EmployeeModel employee = this.employeeCache.get(ctx.getCid());
        if (employee == null) throw new EmployeeNotFoundException();

        final String content = Objects.requireNonNull(event.getValues().get(0));
        switch (ctx.specifiers.get(1)) {
            default -> throw new IllegalArgumentException();
            case "branch" -> employee.setBranch(CwuBranch.valueOf(content.toUpperCase()));
            case "rank" -> employee.setRank(CwuRank.valueOf(content.toUpperCase()));
        }

        this.goToPage(event, employee);
    }

    /* Handlers */
    private void handleSessionChoice(final ButtonInteractionEvent event, final SessionChoiceType choice, final String cid) {
        switch (choice) {
            default -> throw new IllegalArgumentException();
            case VIEW, DELETE -> {
                event.replyModal(ManageBuilderUtils.sessionSearchModal(cid)).queue();
            }
        }
    }

    private void handleReportChoice(final ButtonInteractionEvent event, final ReportChoiceType choice, final String cid) {
        switch (choice) {
            default -> throw new IllegalArgumentException();
            case VIEW, DELETE -> {
                event.replyModal(ManageBuilderUtils.reportSearchModal(cid)).queue();
            }
        }
    }

    private void handleProfileChoice(final ButtonInteractionEvent event, final ProfileChoiceType choice, final CommandContext ctx) throws CwuException {
        switch (choice) {
            default -> throw new IllegalArgumentException();
            case VIEW, DELETE, UPDATE -> {
                event.replyModal(ManageBuilderUtils.profileSearchModal()).queue();
            }
            case CREATE -> {
                event.editMessageEmbeds(ManageBuilderUtils.createProfileMessage())
                        .setComponents(ManageBuilderUtils.createProfileComponent(ctx.getCid()))
                        .queue();
            }
            case ABORT -> {
                event.reply("Vous avez annuler la création du profil employé.")
                        .setEphemeral(true).queue();
                event.getMessage().delete().queue();
            }
            case SUBMIT -> {
                final EmployeeModel employee = this.employeeCache.get(ctx.getCid());
                if (employee == null) throw new EmployeeNotFoundException();

                if (!employee.isValid()) throw new EmployeeValidationException();

                this.profileDatabase.addOne(employee.toProfile());
                this.profileDatabase.save();

                this.employeeCache.remove(employee.getManagerCid());

                event.reply("Création de l'employé avec succès.")
                        .setEphemeral(true).queue();
                event.getMessage().delete().queue();
            }
            case RESUME -> {
                final EmployeeModel employee = this.employeeCache.get(ctx.getCid());
                if (employee == null) throw new EmployeeNotFoundException();

                employee.goFirstPage();
                this.goToPage(event, employee);
            }
            case OVERWRITE -> {
                final String cid = ctx.getCid();
                final EmployeeModel employee = new EmployeeModel(cid);
                this.employeeCache.add(cid, employee);
                employee.begin();

                employee.goFirstPage();
                this.goToPage(event, employee);
            }
            case START -> {
                final String cid = ctx.getCid();
                if (this.employeeCache.exist(cid)) {
                    final IdentityModel identity = this.employeeCache.get(cid).getIdentity();
                    event.editMessageEmbeds(ManageBuilderUtils.profileAlreadyExistMessage(identity))
                            .setComponents(ManageBuilderUtils.profileResumeOrOverwriteComponent(cid))
                            .queue();
                    return;
                }

                final EmployeeModel employee = new EmployeeModel(cid);
                this.employeeCache.add(cid, employee);
                employee.begin();

                employee.goFirstPage();
                this.goToPage(event, employee);
            }
            case EDIT -> {
                final EmployeeModel employee = this.employeeCache.get(ctx.getCid());
                if (employee == null) throw new EmployeeNotFoundException();

                employee.goFirstPage();
                this.goToPage(event, employee);
            }
            case CLEAR -> {
                final EmployeeModel employee = this.employeeCache.get(ctx.getCid());
                if (employee == null) throw new EmployeeNotFoundException();

                this.handleClearButton(event, employee);
            }
            case FILL -> {
                final EmployeeModel employee = this.employeeCache.get(ctx.getCid());
                if (employee == null) throw new EmployeeNotFoundException();

                this.handleFillButton(event, employee);
            }
            case PREVIEW -> {
                final EmployeeModel employee = this.employeeCache.get(ctx.getCid());
                if (employee == null) throw new EmployeeNotFoundException();

                employee.goPreviewPage();
                this.goToPage(event, employee);
            }
            case PAGE_NEXT -> {
                final EmployeeModel employee = this.employeeCache.get(ctx.getCid());
                if (employee == null) throw new EmployeeNotFoundException();

                employee.goNextPage();
                this.goToPage(event, employee);
            }
            case PAGE_PREV -> {
                final EmployeeModel employee = this.employeeCache.get(ctx.getCid());
                if (employee == null) throw new EmployeeNotFoundException();

                employee.goPrevPage();
                this.goToPage(event, employee);
            }
        }
    }

    /* Utils */
    protected void goToPage(final IMessageEditCallback event, final EmployeeModel employee) {
        final EmployeePageType page = employee.getCurrentPage();

        final var callback = (switch (page) {
            default -> throw new IllegalArgumentException();
            case IDENTITY -> event.editMessageEmbeds(ManageBuilderUtils.profileIdentityMessage(employee.getIdentity()))
                    .setComponents(ManageBuilderUtils.profileIdentityComponent(employee.getManagerCid()));
            case ID -> event.editMessageEmbeds(ManageBuilderUtils.profileIdMessage(employee.getId()))
                    .setComponents(ManageBuilderUtils.profileIdComponent(employee.getManagerCid()));
            case BRANCH -> event.editMessageEmbeds(ManageBuilderUtils.profileBranchMessage(employee.getBranch()))
                    .setComponents(ManageBuilderUtils.profileBranchComponents(employee.getManagerCid(), employee.getBranch()));
            case RANK -> event.editMessageEmbeds(ManageBuilderUtils.profileRankMessage(employee.getRank()))
                    .setComponents(ManageBuilderUtils.profileRankComponents(employee.getManagerCid(), employee.getRank()));
            case JOINED_AT -> event.editMessageEmbeds(ManageBuilderUtils.profileJoinedAtMessage(employee.getJoinedAt()))
                    .setComponents(ManageBuilderUtils.profileJoinedAtComponent(employee.getManagerCid()));
            case PREVIEW -> event.editMessageEmbeds(ManageBuilderUtils.profilePreviewMessage(employee))
                    .setComponents(ManageBuilderUtils.profilePreviewComponents(employee.getManagerCid()));
        });

        callback.queue();
    }

    private void handleClearButton(final ButtonInteractionEvent event, final EmployeeModel employee) {
        switch (employee.getCurrentPage()) {
            default -> throw new IllegalArgumentException();
            case IDENTITY -> employee.setIdentity(null);
            case ID -> employee.setId(null);
            case BRANCH -> employee.setBranch(null);
            case RANK -> employee.setRank(null);
            case JOINED_AT -> employee.setJoinedAt(null);
        }

        this.goToPage(event, employee);
    }

    private void handleFillButton(final ButtonInteractionEvent event, final EmployeeModel employee) {
        final var currentPage = employee.getCurrentPage();
        final TextInput.Builder inputBuilder = TextInput.create("cwu_manage.fill/data", currentPage.getDescription(), TextInputStyle.SHORT)
                .setRequired(true);

        final var modalType = switch (currentPage) {
            default -> throw new IllegalArgumentException();
            case IDENTITY -> {
                inputBuilder.setPlaceholder("ex: Nom Prénom, #12345");
                yield GestionModalType.FILL_IDENTITY;
            }
            case ID -> {
                inputBuilder.setPlaceholder("ex: 1262848159386828811");
                yield GestionModalType.FILL_ID;
            }
            case JOINED_AT -> {
                inputBuilder.setPlaceholder("ex: 01/01/1999");
                yield GestionModalType.FILL_JOINED_AT;
            }
        };

        final Modal modal = Modal.create(modalType.build(employee.getManagerCid()), "Ajout des informations")
                .addComponents(ActionRow.of(inputBuilder.build()))
                .build();

        event.replyModal(modal).queue();
    }
}
