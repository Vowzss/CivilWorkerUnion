package com.oneliferp.cwu.commands.modules.profile;

import com.oneliferp.cwu.cache.ProfileCache;
import com.oneliferp.cwu.commands.CwuCommand;
import com.oneliferp.cwu.commands.modules.manage.utils.ManageBuilderUtils;
import com.oneliferp.cwu.commands.modules.profile.exceptions.ProfileNotFoundException;
import com.oneliferp.cwu.commands.modules.profile.exceptions.ProfileValidationException;
import com.oneliferp.cwu.commands.modules.profile.misc.ProfilePageType;
import com.oneliferp.cwu.commands.modules.profile.misc.actions.ProfileButtonType;
import com.oneliferp.cwu.commands.modules.profile.misc.actions.ProfileModalType;
import com.oneliferp.cwu.commands.modules.profile.models.ProfileModel;
import com.oneliferp.cwu.commands.modules.profile.utils.ProfileBuilderUtils;
import com.oneliferp.cwu.commands.utils.CommandContext;
import com.oneliferp.cwu.database.ProfileDatabase;
import com.oneliferp.cwu.exceptions.CwuException;
import com.oneliferp.cwu.exceptions.IdentityException;
import com.oneliferp.cwu.exceptions.IdentityMalformedException;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.misc.CwuRank;
import com.oneliferp.cwu.models.CwuIdentityModel;
import com.oneliferp.cwu.models.IdentityModel;
import com.oneliferp.cwu.utils.SimpleDate;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.Objects;

public class ProfileCommand extends CwuCommand {
    private final ProfileDatabase profileDB;
    private final ProfileCache profileCache;

    public ProfileCommand() {
        super("profile", "profil", "Vous permet d'accéder à votre fiche d'employé.");
        this.profileDB = ProfileDatabase.get();
        this.profileCache = ProfileCache.get();
    }

    @Override
    public void handleCommandInteraction(final SlashCommandInteractionEvent event) throws CwuException {
        final ProfileModel profile = this.profileDB.getFromId(event.getUser().getIdLong());
        if (profile == null) throw new ProfileNotFoundException(event);

        event.replyEmbeds(ProfileBuilderUtils.displayMessage(profile))
                .setEphemeral(true).queue();
    }

    @Override
    public void handleModalInteraction(final ModalInteractionEvent event, final CommandContext ctx) throws CwuException {
        final ProfileModel profile = this.profileDB.getFromCid(ctx.getCid());
        if (profile == null) throw new ProfileNotFoundException(event);

        final String content = Objects.requireNonNull(event.getValue("cwu_manage.fill/data")).getAsString();

        switch ((ProfileModalType) ctx.getEnumType()) {
            default -> throw new IllegalArgumentException();
            case FILL_IDENTITY -> {
                final ProfileModel employee = this.profileCache.find(profile.getCid());
                if (employee == null) throw new ProfileNotFoundException(event);

                try {
                    profile.setIdentity(CwuIdentityModel.parseCwuIdentity(content));
                } catch (IdentityMalformedException e) {
                    throw new IdentityException(event);
                }
                this.goToPage(event, employee, profile.getCid());
            }
            case FILL_ID -> {
                final ProfileModel employee = this.profileCache.find(profile.getCid());
                if (employee == null) throw new ProfileNotFoundException(event);

                employee.setId(Long.parseLong(content));
                this.goToPage(event, employee, profile.getCid());
            }
            case FILL_JOINED_AT -> {
                final ProfileModel employee = this.profileCache.find(profile.getCid());
                if (employee == null) throw new ProfileNotFoundException(event);

                employee.setJoinedAt(SimpleDate.parseDate(content));
                this.goToPage(event, employee, profile.getCid());
            }
        }
    }

    @Override
    public void handleSelectionInteraction(final StringSelectInteractionEvent event, final CommandContext ctx) throws CwuException {
        final ProfileModel profile = this.profileDB.getFromCid(ctx.getCid());
        if (profile == null) throw new ProfileNotFoundException(event);

        final String content = Objects.requireNonNull(event.getValues().get(0));
        switch (ctx.specifiers.get(1)) {
            default -> throw new IllegalArgumentException();
            case "branch" -> {
                final var branch = CwuBranch.valueOf(content);

                event.editMessageEmbeds(ProfileBuilderUtils.updateMessage(branch, profile.getRank()))
                        .setComponents(ProfileBuilderUtils.updateComponent(profile.getCid(), branch, profile.getRank()))
                        .queue();
            }
            case "rank" -> {
                final var rank = CwuRank.valueOf(content);

                event.editMessageEmbeds(ProfileBuilderUtils.updateMessage(profile.getBranch(), rank))
                        .setComponents(ProfileBuilderUtils.updateComponent(profile.getCid(), profile.getBranch(), rank))
                        .queue();
            }
        }
    }

    @Override
    public void handleButtonInteraction(final ButtonInteractionEvent event, final CommandContext ctx) throws CwuException {
        switch ((ProfileButtonType) ctx.getEnumType()) {
            case STATS -> {
                final ProfileModel profile = this.profileDB.getFromCid(ctx.getCid());
                if (profile == null) throw new ProfileNotFoundException(event);

                event.editMessageEmbeds(ProfileBuilderUtils.displayMessage(profile))
                        .setComponents(ProfileBuilderUtils.displayComponent(profile.getCid(), profile.getBranch(), profile.getRank()))
                        .queue();
            }
            case UPDATE_REQUEST -> {
                final ProfileModel profile = this.profileDB.getFromCid(ctx.getCid());
                if (profile == null) throw new ProfileNotFoundException(event);

                final var branch = CwuBranch.valueOf(ctx.getAsString("branch"));
                final var rank = CwuRank.valueOf(ctx.getAsString("rank"));

                event.editMessageEmbeds(ProfileBuilderUtils.updateMessage(branch, rank))
                        .setComponents(ProfileBuilderUtils.updateComponent(profile.getCid(), branch, rank))
                        .queue();
            }
            case UPDATE_CONFIRM -> {
                final ProfileModel profile = this.profileDB.getFromCid(ctx.getCid());
                if (profile == null) throw new ProfileNotFoundException(event);

                final var rank = CwuRank.valueOf(ctx.getAsString("rank"));
                final var branch = CwuBranch.valueOf(ctx.getAsString("branch"));

                profile.setBranch(branch);
                profile.setRank(rank);
                this.profileDB.save();

                event.reply("Mise à jour de la fiche employé avec succès.")
                        .setEphemeral(true).queue();
                event.getMessage().delete().queue();
            }
            case UPDATE_CANCEL -> {
                final ProfileModel profile = this.profileDB.getFromCid(ctx.getCid());
                if (profile == null) throw new ProfileNotFoundException(event);

                event.reply("Vous avez annuler la modification de la fiche employé.")
                        .setEphemeral(true).queue();
                event.getMessage().delete().queue();
            }
            case DELETE_REQUEST -> {
                final ProfileModel profile = this.profileDB.getFromCid(ctx.getCid());
                if (profile == null) throw new ProfileNotFoundException(event);

                event.editMessageEmbeds(ProfileBuilderUtils.deleteMessage(profile.getIdentity()))
                        .setComponents(ProfileBuilderUtils.deleteComponent(profile.getCid())).queue();
            }
            case DELETE_CONFIRM -> {
                this.profileDB.removeOne(ctx.getCid());
                this.profileDB.save();

                event.reply("Suppression de la fiche employé avec succès.")
                        .setEphemeral(true).queue();
                event.getMessage().delete().queue();
            }
            case DELETE_CANCEL -> {
                event.reply("Vous avez annulé la suppression de l'employé.")
                        .setEphemeral(true).queue();
                event.getMessage().delete().queue();
            }

            case CREATE -> {
                if (this.profileCache.exist(ctx.getCid())) {
                    final ProfileModel profile = this.profileCache.find(ctx.getCid());
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
                event.reply("Vous avez annuler la création d'une fiche employé.")
                        .setEphemeral(true).queue();
                event.getMessage().delete().queue();
            }
            case SUBMIT -> {
                final ProfileModel profile = this.profileCache.find(ctx.getCid());
                if (profile == null) throw new ProfileNotFoundException(event);
                if (!profile.verify()) throw new ProfileValidationException(event);

                this.profileCache.delete(ctx.getCid());
                this.profileDB.addOne(profile);
                this.profileDB.save();

                event.reply("Création de la fiche de l'employé avec succès.")
                        .setEphemeral(true).queue();
                event.getMessage().delete().queue();
            }
            case RESUME, EDIT -> {
                final ProfileModel profile = this.profileCache.find(ctx.getCid());
                if (profile == null) throw new ProfileNotFoundException(event);

                profile.goFirstPage();
                this.goToPage(event, profile, ctx.getCid());
            }
            case OVERWRITE, START -> {
                final String cid = ctx.getCid();
                final ProfileModel profile = new ProfileModel();
                this.profileCache.add(cid, profile);

                profile.start();
                profile.goFirstPage();
                this.goToPage(event, profile, ctx.getCid());
            }
            case CLEAR -> {
                final ProfileModel profile = this.profileCache.find(ctx.getCid());
                if (profile == null) throw new ProfileNotFoundException(event);

                this.handleClearButton(event, profile, ctx.getCid());
            }
            case FILL -> {
                final ProfileModel profile = this.profileCache.find(ctx.getCid());
                if (profile == null) throw new ProfileNotFoundException(event);

                this.handleFillButton(event, profile, ctx.getCid());
            }
            case PREVIEW -> {
                final ProfileModel profile = this.profileCache.find(ctx.getCid());
                if (profile == null) throw new ProfileNotFoundException(event);

                profile.goPreviewPage();
                this.goToPage(event, profile, ctx.getCid());
            }
            case PAGE -> {
                final ProfileModel profile = this.profileCache.find(ctx.getCid());
                if (profile == null) throw new ProfileNotFoundException(event);

                if (ctx.getAsString("type").equals("prev")) { profile.goPrevPage(); }
                else { profile.goNextPage(); }
                this.goToPage(event, profile, ctx.getCid());
            }
        }
    }

    /* Button handlers */
    private void handleClearButton(final ButtonInteractionEvent event, final ProfileModel profile, final String managerCid) {
        switch (profile.getCurrentPage()) {
            case IDENTITY -> profile.setIdentity(null);
            case ID -> profile.setId(null);
            case BRANCH -> profile.setBranch(null);
            case RANK -> profile.setRank(null);
            case JOINED_AT -> profile.setJoinedAt(null);
        }

        this.goToPage(event, profile, managerCid);
    }

    private void handleFillButton(final ButtonInteractionEvent event, final ProfileModel profile, final String managerCid) {
        final var currentPage = profile.getCurrentPage();
        final TextInput.Builder inputBuilder = TextInput.create("cwu_manage.fill/data", currentPage.getDescription(), TextInputStyle.SHORT)
                .setRequired(true);

        final var modalType = switch (currentPage) {
            default -> throw new IllegalArgumentException();
            case IDENTITY -> {
                inputBuilder.setPlaceholder("ex: Nom Prénom, #12345");
                yield ProfileModalType.FILL_IDENTITY;
            }
            case ID -> {
                inputBuilder.setPlaceholder("ex: 1262848159386828811");
                yield ProfileModalType.FILL_ID;
            }
            case JOINED_AT -> {
                inputBuilder.setPlaceholder("ex: 01/01/1999");
                yield ProfileModalType.FILL_JOINED_AT;
            }
        };

        final Modal modal = Modal.create(modalType.build(managerCid), "Ajout des informations")
                .addComponents(ActionRow.of(inputBuilder.build()))
                .build();

        event.replyModal(modal).queue();
    }

    /* Utils */
    private void goToPage(final IMessageEditCallback event, final ProfileModel profile, final String managerCid) {
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
}
