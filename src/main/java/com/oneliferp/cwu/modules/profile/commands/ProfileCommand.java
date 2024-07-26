package com.oneliferp.cwu.modules.profile.commands;

import com.oneliferp.cwu.Cache.CidCache;
import com.oneliferp.cwu.Commands.CwuCommand;
import com.oneliferp.cwu.Database.CwuDatabase;
import com.oneliferp.cwu.Models.CwuModel;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.misc.CwuRank;
import com.oneliferp.cwu.modules.profile.exceptions.*;
import com.oneliferp.cwu.modules.profile.misc.ProfileButtonType;
import com.oneliferp.cwu.modules.profile.misc.ProfileCommandType;
import com.oneliferp.cwu.modules.profile.utils.OptionUtils;
import com.oneliferp.cwu.modules.profile.utils.ProfileBuilderUtils;
import net.dv8tion.jda.api.entities.User;
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
    private final CidCache cidCache;

    public ProfileCommand() {
        super(ProfileCommandType.BASE.getId(), "Vous permet de gérer les profiles CWU.");

        this.cwuDatabase = CwuDatabase.get();
        this.cidCache = CidCache.get();
    }

    @Override
    public SlashCommandData configure(final SlashCommandData slashCommand) {
        final SubcommandData createSubCommand = new SubcommandData(ProfileCommandType.CREATE.getId(), ProfileCommandType.CREATE.getDescription());
        createSubCommand.addOptions(
                new OptionData(OptionType.STRING, "identity", "Identité (Nom Prénom, #00000)", true),
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
    public void handleCommandEvent(final SlashCommandInteractionEvent event) throws Exception {
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
                        .setActionRow(ProfileBuilderUtils.actionsRow(cwu.getCid(), cwu.isLinked()))
                        .setEphemeral(true).queue();
                break;
            }
            case VIEW: {
                // Obtain cid from command option or user id
                final OptionMapping cidOption = event.getOption("cid");
                final String cid = cidOption == null ? this.cidCache.get(event.getUser().getIdLong()) : cidOption.getAsString();

                final CwuModel cwu = this.cwuDatabase.get(cid);
                if (cwu == null) throw new ProfileNotFoundException();

                event.replyEmbeds(ProfileBuilderUtils.profileMessage(cwu))
                        .setActionRow(ProfileBuilderUtils.actionsRow(cwu.getCid(), cwu.isLinked()))
                        .setEphemeral(true).queue();
                break;
            }
        }
    }

    @Override
    public void handleButtonEvent(final ButtonInteractionEvent event, final String buttonID) throws ProfileException {
        final List<String> parts = Arrays.asList(buttonID.split(":"));

        final CwuModel cwu = this.cwuDatabase.get(parts.get(1));
        if (cwu == null) throw new ProfileNotFoundException();

        switch (ProfileButtonType.fromId(parts.get(0))) {
            default: {
                throw new IllegalArgumentException();
            }
            case STATS: {
                event.replyEmbeds(ProfileBuilderUtils.statsMessage(cwu))
                        .setActionRow(ProfileBuilderUtils.returnButton(cwu.getCid()))
                        .setEphemeral(true).queue();
                break;
            }
            case DELETE: {
                event.replyEmbeds(ProfileBuilderUtils.suppressMessage(cwu))
                        .setActionRow(ProfileBuilderUtils.confirmAndCancelRow(List.of(ProfileButtonType.CONFIRM_DELETE, ProfileButtonType.CANCEL_DELETE), cwu.getCid()))
                        .setEphemeral(true).queue();
                break;
            }
            case LINK: {
                final User user = event.getUser();
                if (cwu.isLinked()) throw new ProfileAlreadyLinkedException(user);

                event.replyEmbeds(ProfileBuilderUtils.linkMessage(event.getUser().getIdLong(), cwu))
                        .setActionRow(ProfileBuilderUtils.confirmAndCancelRow(List.of(ProfileButtonType.CONFIRM_LINK, ProfileButtonType.CANCEL_LINK), cwu.getCid()))
                        .setEphemeral(true).queue();
                break;
            }
            case UNLINK: {
                if (!cwu.isLinked()) throw new ProfileNotLinkedException();

                event.replyEmbeds(ProfileBuilderUtils.unlinkMessage(cwu))
                        .setActionRow(ProfileBuilderUtils.confirmAndCancelRow(List.of(ProfileButtonType.CONFIRM_UNLINK, ProfileButtonType.CANCEL_UNLINK), cwu.getCid()))
                        .setEphemeral(true).queue();
                break;
            }
            case CONFIRM_LINK: {
                // Update & save cwu database state
                cwu.setId(event.getUser().getIdLong());
                this.cwuDatabase.writeToCache();

                // Update runtime cache
                this.cidCache.add(cwu.getId(), cwu.getCid());

                event.reply(String.format("Le profil **(%s)** vient d'être associé avec succès.", cwu.getIdentity()))
                        .setEphemeral(true).queue();
                break;
            }
            case CONFIRM_UNLINK: {
                // Update runtime cache
                this.cidCache.remove(cwu.getId());

                // Update & save cwu database state
                cwu.setId(null);
                this.cwuDatabase.writeToCache();

                event.reply(String.format("Le profil **(%s)** vient d'être dissocié avec succès.", cwu.getIdentity()))
                        .setEphemeral(true).queue();
                break;
            }
            case CONFIRM_DELETE: {
                // Update & save cwu database state
                this.cwuDatabase.removeOne(cwu.getCid());
                this.cwuDatabase.writeToCache();

                event.reply(String.format("Le profil **(%s)** vient d'être supprimé avec succès.", cwu.getIdentity()))
                        .setEphemeral(true).queue();
                break;
            }
            case RETURN:
            case CANCEL_LINK:
            case CANCEL_DELETE:
            case CANCEL_UNLINK: {
                event.replyEmbeds(ProfileBuilderUtils.profileMessage(cwu))
                        .setActionRow(ProfileBuilderUtils.actionsRow(cwu.getCid(), cwu.isLinked()))
                        .setEphemeral(true).queue();
                break;
            }
        }

        event.getMessage().delete().queue();
    }
}
