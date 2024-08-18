package com.oneliferp.cwu.commands.profile;

import com.oneliferp.cwu.commands.CwuCommand;
import com.oneliferp.cwu.database.EmployeeDatabase;
import com.oneliferp.cwu.models.EmployeeModel;
import com.oneliferp.cwu.exceptions.CwuException;
import com.oneliferp.cwu.commands.CommandContext;
import com.oneliferp.cwu.commands.profile.exceptions.*;
import com.oneliferp.cwu.commands.profile.misc.ids.ProfileButtonType;
import com.oneliferp.cwu.commands.profile.utils.ProfileBuilderUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.List;

public class ProfileCommand extends CwuCommand {
    private final EmployeeDatabase cwuDatabase;

    public ProfileCommand() {
        super("profile", "profil", "Vous permet d'accéder à votre profil d'employé.");
        this.cwuDatabase = EmployeeDatabase.get();
    }
/*
    @Override
    public SlashCommandData configure(final SlashCommandData slashCommand) {
        final SubcommandData createSubCommand = new SubcommandData(ProfileCommandType.CREATE.getName(), ProfileCommandType.CREATE.getDescription());
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

        final SubcommandData viewSubCommand = new SubcommandData(ProfileCommandType.VIEW.getName(), ProfileCommandType.VIEW.getDescription());
        viewSubCommand.addOptions(new OptionData(OptionType.STRING, "cid", "CID associé au CWU.", false));

        slashCommand.addSubcommands(createSubCommand, viewSubCommand);
        return slashCommand;
    }
*/
    @Override
    public void handleCommandInteraction(final SlashCommandInteractionEvent event) throws CwuException {
        // Obtain cid from command option or user id
        final OptionMapping cidOption = event.getOption("cid");
        final EmployeeModel cwu = cidOption == null ?
                this.cwuDatabase.getFromId(event.getUser().getIdLong()) :
                this.cwuDatabase.getFromCid(cidOption.getAsString());
        if (cwu == null) throw new ProfileNotFoundException();

        event.replyEmbeds(ProfileBuilderUtils.profileMessage(cwu))
                .setActionRow(ProfileBuilderUtils.statsAndDeleteRow(cwu.getCid()))
                .queue();

        /*
        switch (ProfileCommandType.resolveType(event.getSubcommandName())) {
            default: {
                throw new IllegalArgumentException();
            }
            case CREATE: {
                final CwuModel cwu = CwuModel.fromInput(event);
                if (this.cwuDatabase.exist(cwu.getCid())) throw new ProfileFoundException();

                // Update & save cwu database state
                this.cwuDatabase.addOne(cwu);
                this.cwuDatabase.save();

                event.replyEmbeds(ProfileBuilderUtils.profileMessage(cwu))
                        .setActionRow(ProfileBuilderUtils.statsAndDeleteRow(cwu.getCid()))
                        .queue();
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
                        .queue();
                break;
            }
        }*/
    }

    @Override
    public void handleButtonInteraction(final ButtonInteractionEvent event, final CommandContext ctx) throws CwuException {
        final EmployeeModel cwu = this.cwuDatabase.getFromCid(ctx.getCid());
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
                        .setActionRow(ProfileBuilderUtils.confirmAndCancelRow(List.of(ProfileButtonType.DELETE_CONFIRM, ProfileButtonType.DELETE_CANCEL), cwu.getCid()))
                        .queue();
                break;
            }
            case DELETE_CONFIRM: {
                // Update & save cwu database state
                this.cwuDatabase.removeOne(cwu.getCid());
                this.cwuDatabase.save();

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
