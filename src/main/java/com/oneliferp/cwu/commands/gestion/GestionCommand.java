package com.oneliferp.cwu.commands.gestion;

import com.oneliferp.cwu.commands.CwuCommand;
import com.oneliferp.cwu.exceptions.CwuException;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class GestionCommand extends CwuCommand {
    public GestionCommand() {
        super("manage", "gestion", "Vous permet de gérer les différents aspect de la CWU.");
    }

    @Override
    public SlashCommandData configure(SlashCommandData slashCommand) {
        final SubcommandData createProfileCommand = new SubcommandData("profil", "Vous permet de gérer les profiles des CWU.");
        createProfileCommand.addOption(OptionType.STRING, "");
        return super.configure(slashCommand);
    }

    @Override
    public void handleCommandInteraction(SlashCommandInteractionEvent event) throws CwuException {
        super.handleCommandInteraction(event);
    }
}
