package com.oneliferp.cwu.listeners;

import com.oneliferp.cwu.CivilWorkerUnion;
import com.oneliferp.cwu.commands.CwuCommand;
import com.oneliferp.cwu.exceptions.CwuException;
import com.oneliferp.cwu.commands.CommandContext;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SlashCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull final SlashCommandInteractionEvent event) {
        try {
            final CwuCommand command = CivilWorkerUnion.get().getCommandFromName(event.getName());
            command.handleCommandInteraction(event);
        } catch (CwuException ex) {
            event.reply("\uD83D\uDCA5 " + ex.getMessage())
                    .queue();

            ex.printStackTrace();
        }
    }

    @Override
    public void onStringSelectInteraction(final StringSelectInteractionEvent event) {
        final CommandContext eventType = new CommandContext(event.getComponentId());

        try {
            final var command = CivilWorkerUnion.get().getCommandFromId(eventType.getCommand());
            command.handleSelectionInteraction(event, eventType);
        } catch (CwuException ex) {
            event.reply(ex.getMessage()).queue();
            ex.printStackTrace();
        }
    }

    @Override
    public void onButtonInteraction(final ButtonInteractionEvent event) {
        final String ID = event.getButton().getId();
        if (ID == null) throw new IllegalArgumentException();
        final CommandContext eventType = new CommandContext(ID);

        try {
            final var command = CivilWorkerUnion.get().getCommandFromId(eventType.getCommand());
            command.handleButtonInteraction(event, eventType);
        } catch (CwuException ex) {
            event.reply(ex.getMessage())
                    .queue();

            ex.printStackTrace();
        }
    }

    @Override
    public void onModalInteraction(final ModalInteractionEvent event) {
        final CommandContext eventType = new CommandContext(event.getModalId());

        try {
            final var command = CivilWorkerUnion.get().getCommandFromId(eventType.getCommand());
            System.out.println(eventType.getCommand());
            command.handleModalInteraction(event, eventType);
        } catch (CwuException ex) {
            event.reply("\uD83D\uDCA5 " + ex.getMessage())
                    .queue();

            ex.printStackTrace();
        }
    }
}
