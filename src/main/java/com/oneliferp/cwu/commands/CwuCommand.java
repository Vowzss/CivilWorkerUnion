package com.oneliferp.cwu.commands;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public abstract class CwuCommand {
    private final String name;
    private final String description;

    protected CwuCommand(final String name, final String description) {
        this.name = name;
        this.description = description;
    }
    /*
    Handlers
    */
    public void handleCommandEvent(final SlashCommandInteractionEvent event) throws Exception {
        event.reply("\uD83D\uDCA5 " + "Command not yet implemented.").setEphemeral(true).queue();
    }

    public void handleButtonEvent(final ButtonInteractionEvent event, final String buttonID) throws Exception {
        event.reply("\uD83D\uDCA5 " + "Button not yet implemented.").setEphemeral(true).queue();
    }

    public void handleModalEvent(final ModalInteractionEvent event, final String modalID) throws Exception {
        event.reply("\uD83D\uDCA5 " + "Interface not yet implemented.").setEphemeral(true).queue();
    }

    /*
    Getters
    */
    public final String getName() { return this.name; }

    /*
    Utils
    */
    public final SlashCommandData toSlashCommand() {
        return Commands.slash(this.name, this.description);
    }

    public SlashCommandData configure(final SlashCommandData slashCommand) {
        return slashCommand;
    }
}
