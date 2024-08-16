package com.oneliferp.cwu.commands;

import com.oneliferp.cwu.exceptions.CwuException;
import com.oneliferp.cwu.misc.ICommandType;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public abstract class CwuCommand {
    final String id;
    private final String name;
    private final String description;

    protected CwuCommand(final String id, final String name, final String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    /* Handlers */
    public void handleCommandInteraction(final SlashCommandInteractionEvent event) throws CwuException {
        event.reply("\uD83D\uDCA5 " + "Command not yet implemented.").queue();
    }

    public void handleButtonInteraction(final ButtonInteractionEvent event, final CommandContext eventType) throws CwuException {
        event.reply("\uD83D\uDCA5 " + "Button not yet implemented.").queue();
    }

    public void handleModalInteraction(final ModalInteractionEvent event, final CommandContext eventType) throws CwuException {
        event.reply("\uD83D\uDCA5 " + "Interface not yet implemented.").queue();
    }

    public void handleSelectionInteraction(final StringSelectInteractionEvent event, final CommandContext eventType) throws CwuException {
        event.reply("\uD83D\uDCA5 " + "Selection not yet implemented.").queue();
    }

    /* Getters */
    public final String getID() { return this.id; }

    public String getName() {
        return this.name;
    }

    /* Utils */
    public final SlashCommandData toSlashCommand() {
        System.out.printf(this.name + " " + this.description + "\n");
        return Commands.slash(this.name, this.description);
    }

    public SlashCommandData configure(final SlashCommandData slashCommand) {
        return slashCommand;
    }
}
