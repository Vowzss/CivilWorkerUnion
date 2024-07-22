package com.oneliferp.cwu.Listeners;

import com.oneliferp.cwu.CivilWorkerUnion;
import com.oneliferp.cwu.Commands.CwuCommand;
import com.oneliferp.cwu.modules.profile.commands.ProfileCommand;
import com.oneliferp.cwu.modules.profile.misc.ProfileCommandType;
import com.oneliferp.cwu.modules.session.commands.SessionCommand;
import com.oneliferp.cwu.modules.session.misc.SessionCommandType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.List;

public class SlashCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(final SlashCommandInteractionEvent event) {
        final CwuCommand command = CivilWorkerUnion.get().getCommand(event.getName());
        if (command == null) {
            event.getHook().editOriginal("Cette command n'existe pas/plus!").queue();
            return;
        }
        command.handleCommandEvent(event);
    }

    @Override
    public void onMessageReceived(final MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        // Only look for message reply
        if (event.getMessage().getType() != MessageType.INLINE_REPLY) return;

        // Check if message truly is a message reply
        final MessageReference reference = event.getMessage().getMessageReference();
        if (reference == null) return;

        // Check for button components
        final Message message = reference.getMessage();
        if (message == null || message.getComponents().isEmpty()) return;

        final List<Button> buttons = message.getComponents().get(0).getButtons();
        if (buttons.isEmpty()) return;

        // Get first button id
        final String buttonID = buttons.get(0).getId();
        if (buttonID == null) return;

        // Resolve correct handler from button id
        if (buttonID.contains("report")) {
            final SessionCommand command = CivilWorkerUnion.get().getCommand(SessionCommandType.BASE.getId());
            command.handleReplyEvent(event.getMessage(), buttonID);
        } else if (buttonID.contains("profile")) {
            final ProfileCommand command = CivilWorkerUnion.get().getCommand(ProfileCommandType.BASE.getId());
            command.handleReplyEvent(event.getMessage(), buttonID);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void onButtonInteraction(final ButtonInteractionEvent event) {
        final String buttonID = event.getButton().getId();
        if (buttonID == null) throw new IllegalArgumentException();

        try {
            if (buttonID.contains("report")) {
                final SessionCommand command = CivilWorkerUnion.get().getCommand(SessionCommandType.BASE.getId());
                command.handleButtonEvent(event, buttonID);
            } else if (buttonID.contains("profile")) {
                final ProfileCommand command = CivilWorkerUnion.get().getCommand(ProfileCommandType.BASE.getId());
                command.handleButtonEvent(event, buttonID);
            } else throw new IllegalArgumentException();
        } catch (Exception ex) {
            event.reply("Aucune action n'est associé au bouton...").queue();
            ex.printStackTrace();
        }
    }
}
