package com.oneliferp.cwu.modules.profile.commands;

import com.oneliferp.cwu.Commands.CwuCommand;
import com.oneliferp.cwu.Database.CwuDatabase;
import com.oneliferp.cwu.Models.CwuModel;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.misc.CwuRank;
import com.oneliferp.cwu.modules.profile.utils.OptionUtils;
import com.oneliferp.cwu.modules.profile.utils.ProfileBuilderUtils;
import com.oneliferp.cwu.modules.profile.misc.ProfileButtonType;
import com.oneliferp.cwu.modules.profile.misc.ProfileCommandType;
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
                new OptionData(OptionType.STRING, "firstname", "Prénom", true),
                new OptionData(OptionType.STRING, "lastname", "Nom de famille", true),

                new OptionData(OptionType.STRING, "cid", "Le CID (ex: #12345)", true),

                new OptionData(OptionType.STRING, "branch", "La branche rattachée", true)
                        .addChoices(Arrays.stream(CwuBranch.values())
                                .map(val -> new Command.Choice(String.format("%s (%s)", val.name(), val.getMeaning()), val.name()))
                                .toArray(Command.Choice[]::new)),
                new OptionData(OptionType.STRING, "rank", "Le rang", true)
                        .addChoices(Arrays.stream(CwuRank.values())
                                .map(val -> new Command.Choice(val.getLabel(), val.name()))
                                .toArray(Command.Choice[]::new)),

                new OptionData(OptionType.STRING, "id", "Identifiant discord", false)
        );

        final SubcommandData viewSubCommand = new SubcommandData(ProfileCommandType.VIEW.getId(), ProfileCommandType.VIEW.getDescription());
        viewSubCommand.addOptions(new OptionData(OptionType.STRING, "cid", "CID associé au CWU.", false));

        slashCommand.addSubcommands(createSubCommand, viewSubCommand);
        return slashCommand;
    }

    @Override
    public void handleCommandEvent(final SlashCommandInteractionEvent event) {
        switch (ProfileCommandType.fromId(event.getSubcommandName())) {
            default: throw new IllegalArgumentException();
            case CREATE: {
                // Check if cwu does not already exist
                final OptionMapping optionID = event.getOption("id");
                final Long userID = optionID == null ? event.getUser().getIdLong() : optionID.getAsLong();
                if (this.cwuDatabase.contains(userID)) {
                    event.reply("Un profil similaire existe déjà.").queue();
                    return;
                }

                // Create cwu from event options
                final CwuModel cwu = OptionUtils.createCwu(event, userID);

                // Save cwu to database
                this.cwuDatabase.saveOne(cwu);
                this.cwuDatabase.writeToCache();

                // Reply newly created cwu profile feedback
                event.replyEmbeds(ProfileBuilderUtils.profileMessage(cwu))
                        .setActionRow(ProfileBuilderUtils.statsAndDeleteRow(cwu.getCid()))
                        .queue();
                break;
            }
            case VIEW: {
                // Resolve cwu from cid (if provided) or user id
                final OptionMapping cidOption = event.getOption("cid");
                final CwuModel cwu = cidOption == null ? this.cwuDatabase.getByUserId(event.getUser().getIdLong())
                        : this.cwuDatabase.getByCid(cidOption.getAsString());

                // No cwu found, reply not found feedback
                if (cwu == null) {
                    event.reply("Aucun profil ne correspond à la recherche.").queue();
                    return;
                }

                // Reply cwu profile feedback
                event.replyEmbeds(ProfileBuilderUtils.profileMessage(cwu))
                        .setActionRow(ProfileBuilderUtils.statsAndDeleteRow(cwu.getCid()))
                        .queue();
                break;
            }
        }
    }

    @Override
    public void handleButtonEvent(final ButtonInteractionEvent event, final String buttonID) {
        final List<String> parts = Arrays.asList(buttonID.split(":"));

        final CwuModel userCwu = this.cwuDatabase.getByUserId(event.getUser().getIdLong());
        final CwuModel requestedCwu = this.cwuDatabase.getByCid(parts.get(1));

        switch (ProfileButtonType.fromId(parts.get(0))) {
            default: throw new IllegalArgumentException();
            case STATS: {
                event.replyEmbeds(ProfileBuilderUtils.statsMessage(requestedCwu))
                        .setActionRow(ProfileBuilderUtils.returnButton(userCwu.getCid()))
                        .queue();
                break;
            }
            case DELETE: {
                if (userCwu != requestedCwu) {
                    event.reply("Vous ne pouvez pas supprimer ce profil.").setEphemeral(true).queue();
                    return;
                }

                this.cwuDatabase.remove(requestedCwu.getUserID());
                event.reply("Le profil vient d'être supprimé avec succès.").setEphemeral(true).queue();
                break;
            }
            case RETURN: {
                event.replyEmbeds(ProfileBuilderUtils.profileMessage(requestedCwu))
                        .setActionRow(ProfileBuilderUtils.statsAndDeleteRow(requestedCwu.getCid()))
                        .queue();
                break;
            }
        }

        event.getMessage().delete().queue();
    }
}
