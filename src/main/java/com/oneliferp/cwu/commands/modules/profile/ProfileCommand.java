package com.oneliferp.cwu.commands.modules.profile;

import com.oneliferp.cwu.cache.ProfileCache;
import com.oneliferp.cwu.commands.CwuCommand;
import com.oneliferp.cwu.commands.modules.manage.exceptions.EmployeeNotFoundException;
import com.oneliferp.cwu.commands.modules.manage.models.EmployeeModel;
import com.oneliferp.cwu.commands.modules.profile.exceptions.ProfileNotFoundException;
import com.oneliferp.cwu.commands.modules.profile.misc.actions.ProfileButtonType;
import com.oneliferp.cwu.commands.modules.profile.models.ProfileModel;
import com.oneliferp.cwu.commands.modules.profile.utils.ProfileBuilderUtils;
import com.oneliferp.cwu.commands.utils.CommandContext;
import com.oneliferp.cwu.database.ProfileDatabase;
import com.oneliferp.cwu.exceptions.CwuException;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.misc.CwuRank;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

import java.util.Objects;

public class ProfileCommand extends CwuCommand {
    private final ProfileDatabase profileDatabase;
    private final ProfileCache profileCache;

    public ProfileCommand() {
        super("profile", "profil", "Vous permet d'accéder à votre fiche d'employé.");
        this.profileDatabase = ProfileDatabase.get();
        this.profileCache = ProfileCache.get();
    }

    @Override
    public void handleCommandInteraction(final SlashCommandInteractionEvent event) throws CwuException {
        final ProfileModel profile = this.profileDatabase.getFromId(event.getUser().getIdLong());
        if (profile == null) throw new ProfileNotFoundException(event);

        event.replyEmbeds(ProfileBuilderUtils.infoMessage(profile))
                .setEphemeral(true).queue();
    }

    @Override
    public void handleSelectionInteraction(final StringSelectInteractionEvent event, final CommandContext ctx) throws CwuException {
        final ProfileModel profile = this.profileDatabase.getFromCid(ctx.getCid());
        if (profile == null) throw new ProfileNotFoundException(event);

        final String content = Objects.requireNonNull(event.getValues().get(0));
        switch (ctx.specifiers.get(1)) {
            default -> throw new IllegalArgumentException();
            case "branch" -> profile.setBranch(CwuBranch.valueOf(content.toUpperCase()));
            case "rank" -> profile.setRank(CwuRank.valueOf(content.toUpperCase()));
        }

        event.editMessageEmbeds(ProfileBuilderUtils.updateMessage(profile.getBranch(), profile.getRank()))
                .setComponents(ProfileBuilderUtils.updateComponent(profile.getCid(), profile.getBranch(), profile.getRank()))
                .queue();
    }

    @Override
    public void handleButtonInteraction(final ButtonInteractionEvent event, final CommandContext ctx) throws CwuException {
        final ProfileModel profile = this.profileDatabase.getFromCid(ctx.getCid());
        if (profile == null) throw new ProfileNotFoundException(event);

        switch ((ProfileButtonType) ctx.getEnumType()) {
            case UPDATE -> {
                event.editMessageEmbeds(ProfileBuilderUtils.updateMessage(profile.getBranch(), profile.getRank()))
                        .setComponents(ProfileBuilderUtils.updateComponent(profile.getCid(), profile.getBranch(), profile.getRank()))
                        .queue();
            }
            case UPDATE_CONFIRM -> {
                this.profileDatabase.save();
                event.reply("Mise à jour de l'employé avec succès.")
                        .setEphemeral(true).queue();
                event.getMessage().delete().queue();
            }
            case UPDATE_CANCEL -> {
                System.out.println();
                //TODO: ROLLBACK RANK AND BRANCH
                event.editMessageEmbeds(ProfileBuilderUtils.infoMessage(profile))
                        .setComponents(ProfileBuilderUtils.infoComponent(profile.getCid()))
                        .queue();
            }
            case DELETE -> {
                event.editMessageEmbeds(ProfileBuilderUtils.deleteMessage(profile.getIdentity()))
                        .setComponents(ProfileBuilderUtils.deleteComponent(profile.getCid())).queue();
            }
            case DELETE_CONFIRM -> {
                this.profileDatabase.removeOne(ctx.getCid());
                this.profileDatabase.save();

                event.reply("Suppression de l'employé avec succès.")
                        .setEphemeral(true).queue();
                event.getMessage().delete().queue();
            }
            case DELETE_CANCEL -> {
                event.reply("Vous avez annulé la suppression de l'employé.")
                        .setEphemeral(true).queue();
                event.getMessage().delete().queue();
            }
            case STATS -> {
                event.editMessageEmbeds(ProfileBuilderUtils.infoMessage(profile))
                        .setComponents(ProfileBuilderUtils.infoComponent(profile.getCid()))
                        .queue();
            }
        }
    }
}
