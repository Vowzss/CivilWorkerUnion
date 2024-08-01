package com.oneliferp.cwu.modules.profile.commands;

import com.oneliferp.cwu.commands.CwuCommand;
import com.oneliferp.cwu.database.CwuDatabase;
import com.oneliferp.cwu.models.CwuModel;
import com.oneliferp.cwu.exceptions.CwuException;
import com.oneliferp.cwu.misc.EventTypeData;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.misc.CwuRank;
import com.oneliferp.cwu.modules.profile.exceptions.*;
import com.oneliferp.cwu.modules.profile.misc.ProfileButtonType;
import com.oneliferp.cwu.modules.profile.misc.ProfileCommandType;
import com.oneliferp.cwu.modules.profile.utils.OptionUtils;
import com.oneliferp.cwu.modules.profile.utils.ProfileBuilderUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Arrays;
import java.util.List;

public class ProfileCommand extends CwuCommand {
    private final CwuDatabase cwuDatabase;

    public ProfileCommand() {
        super(ProfileCommandType.BASE.getId(), "Vous permet de gérer les profiles CWU.");
        this.cwuDatabase = CwuDatabase.getInstance();
    }

    @Override
    public SlashCommandData configure(final SlashCommandData slashCommand) {
        final SubcommandData createSubCommand = new SubcommandData(ProfileCommandType.CREATE.getId(), ProfileCommandType.CREATE.getDescription());
        createSubCommand.addOptions(
                new OptionData(OptionType.STRING, "identity", "Identité (Nom Prénom, #12345)", true),
                new OptionData(OptionType.STRING, "branch", "Branche", true)
                        .addChoices(Arrays.stream(CwuBranch.values())
                                .map(val -> new Command.Choice(String.format("%s (%s)", val.name(), val.getMeaning()), val.name()))
                                .toArray(Command.Choice[]::new)),
                new OptionData(OptionType.STRING, "rank", "Rang", true)
                        .addChoices(Arrays.stream(CwuRank.values())
                                .map(val -> new Command.Choice(val.getLabel(), val.name()))
                                .toArray(Command.Choice[]::new))
        );

        final SubcommandData viewSubCommand = new SubcommandData(ProfileCommandType.VIEW.getId(), ProfileCommandType.VIEW.getDescription());
        viewSubCommand.addOptions(new OptionData(OptionType.STRING, "cid", "CID associé au CWU.", false));

        slashCommand.addSubcommands(createSubCommand, viewSubCommand);
        return slashCommand;
    }

    @Override
    public void handleCommandInteraction(final SlashCommandInteractionEvent event) throws Exception {
        switch (ProfileCommandType.fromId(event.getSubcommandName())) {
            default: {
                throw new IllegalArgumentException();
            }
            case CREATE: {
                final CwuModel cwu = OptionUtils.createCwu(event);
                if (this.cwuDatabase.contains(cwu.getCid())) throw new ProfileFoundException();

                // Update & save cwu database state
                this.cwuDatabase.addOne(cwu);
                this.cwuDatabase.writeToCache();

                event.replyEmbeds(ProfileBuilderUtils.profileMessage(cwu))
                        .setActionRow(ProfileBuilderUtils.statsAndDeleteRow(cwu.getCid()))
                        .setEphemeral(true).queue();
                break;
            }
            case VIEW: {
                // Obtain cid from command option or user id
                final OptionMapping cidOption = event.getOption("cid");
                final CwuModel cwu = cidOption == null ?
                        this.cwuDatabase.getFromId(event.getUser().getIdLong()) :
                        this.cwuDatabase.getFromCid(cidOption.getAsString());
                if (cwu == null) throw new ProfileNotFoundException();

                event.replyEmbeds(ProfileBuilderUtils.profileMessage(cwu))
                        .setActionRow(ProfileBuilderUtils.statsAndDeleteRow(cwu.getCid()))
                        .setEphemeral(true).queue();
                break;
            }
        }
    }

    @Override
    public void handleButtonInteraction(final ButtonInteractionEvent event, final String eventID) throws CwuException {
        final EventTypeData buttonType = new EventTypeData(eventID);

        final CwuModel cwu = this.cwuDatabase.getFromCid(buttonType.getCid());
        if (cwu == null) throw new ProfileNotFoundException();

        switch ((ProfileButtonType) buttonType.enumType) {
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
                        .setActionRow(ProfileBuilderUtils.confirmAndCancelRow(List.of(ProfileButtonType.DELETE_CONFIRM, ProfileButtonType.DELETE_CANCEL), cwu.getCid()))
                        .queue();
                break;
            }
            case DELETE_CONFIRM: {
                // Update & save cwu database state
                this.cwuDatabase.removeOne(cwu.getCid());
                this.cwuDatabase.writeToCache();

                event.editMessage(String.format("Le profil **(%s)** vient d'être supprimé avec succès.", cwu.getIdentity()))
                        .queue();
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
