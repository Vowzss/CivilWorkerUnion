package com.oneliferp.cwu.Commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;
import java.util.List;

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
    public void handleCommandEvent(final SlashCommandInteractionEvent event) {
        event.reply("Command not yet implemented.").setEphemeral(true).queue();
    }

    public void handleButtonEvent(final ButtonInteractionEvent event, final String buttonID) {
        event.reply("Button not yet implemented.").setEphemeral(true).queue();
    }

    public void handleReplyEvent(final Message message, final String buttonID) {
    }

    /*
    Getters
    */
    public final String getName() { return this.name; }
    public final String getDescription() { return this.description; }

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
