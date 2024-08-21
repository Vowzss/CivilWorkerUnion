package com.oneliferp.cwu.commands.profile;

import com.oneliferp.cwu.commands.CwuCommand;
import com.oneliferp.cwu.database.ProfileDatabase;
import com.oneliferp.cwu.commands.profile.models.ProfileModel;
import com.oneliferp.cwu.exceptions.CwuException;
import com.oneliferp.cwu.commands.CommandContext;
import com.oneliferp.cwu.commands.profile.exceptions.*;
import com.oneliferp.cwu.commands.profile.misc.actions.ProfileButtonType;
import com.oneliferp.cwu.commands.profile.utils.ProfileBuilderUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class ProfileCommand extends CwuCommand {
    private final ProfileDatabase profileDatabase;

    public ProfileCommand() {
        super("profile", "profil", "Vous permet d'accéder à votre profil d'employé.");
        this.profileDatabase = ProfileDatabase.get();
    }

    @Override
    public void handleCommandInteraction(final SlashCommandInteractionEvent event) throws CwuException {
        // Obtain cid from command option or user id
        final OptionMapping cidOption = event.getOption("cid");
        final ProfileModel cwu = cidOption == null ?
                this.profileDatabase.getFromId(event.getUser().getIdLong()) :
                this.profileDatabase.getFromCid(cidOption.getAsString());
        if (cwu == null) throw new ProfileNotFoundException();

        event.replyEmbeds(ProfileBuilderUtils.profileMessage(cwu))
                .setActionRow(ProfileBuilderUtils.statsAndDeleteRow(cwu.getCid()))
                .queue();
    }

    @Override
    public void handleButtonInteraction(final ButtonInteractionEvent event, final CommandContext ctx) throws CwuException {
        final ProfileModel cwu = this.profileDatabase.getFromCid(ctx.getCid());
        if (cwu == null) throw new ProfileNotFoundException();

        switch ((ProfileButtonType) ctx.getEnumType()) {
            default: {
                throw new IllegalArgumentException();
            }
            case STATS: {
                event.editMessageEmbeds(ProfileBuilderUtils.statsMessage(cwu))
                        .setActionRow(ProfileBuilderUtils.returnButton(cwu.getCid()))
                        .queue();
                break;
            }
            case DELETE: {
                event.editMessageEmbeds(ProfileBuilderUtils.suppressMessage(cwu))
                        .setActionRow(ProfileBuilderUtils.confirmAndCancelRow(ProfileButtonType.DELETE_CONFIRM, ProfileButtonType.DELETE_CANCEL, cwu.getCid()))
                        .queue();
                break;
            }
            case DELETE_CONFIRM: {
                // Update & save cwu database state
                this.profileDatabase.removeOne(cwu.getCid());
                this.profileDatabase.save();

                event.reply(String.format("Le profil **(%s)** vient d'être supprimé avec succès.", cwu.getIdentity()))
                        .setEphemeral(true)
                        .queue();

                event.getMessage().delete().queue();
                break;
            }
            case DELETE_CANCEL:
            case RETURN: {
                event.editMessageEmbeds(ProfileBuilderUtils.profileMessage(cwu))
                        .setActionRow(ProfileBuilderUtils.statsAndDeleteRow(cwu.getCid()))
                        .queue();
                break;
            }
        }
    }
}
