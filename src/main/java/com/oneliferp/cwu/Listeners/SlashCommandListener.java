package com.oneliferp.cwu.Listeners;

import com.oneliferp.cwu.CivilWorkerUnion;
import com.oneliferp.cwu.Commands.CwuCommand;
import com.oneliferp.cwu.exceptions.CommandNotFoundException;
import com.oneliferp.cwu.modules.profile.commands.ProfileCommand;
import com.oneliferp.cwu.modules.profile.misc.ProfileCommandType;
import com.oneliferp.cwu.modules.session.commands.SessionCommand;
import com.oneliferp.cwu.modules.session.misc.SessionCommandType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.List;

public class SlashCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(final SlashCommandInteractionEvent event) {
        final String commandName = event.getName();
        System.out.println(commandName);

        try {
            final CwuCommand command = CivilWorkerUnion.get().getCommand(commandName);
            if (command == null) throw new CommandNotFoundException();

            command.handleCommandEvent(event);
        } catch (Exception ex) {
            event.reply(ex.getMessage()).setEphemeral(true).queue();
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void onButtonInteraction(final ButtonInteractionEvent event) {
        final String buttonID = event.getButton().getId();
        if (buttonID == null) throw new IllegalArgumentException();

        try {
            if (buttonID.contains(SessionCommandType.BASE.getId())) {
                final SessionCommand command = CivilWorkerUnion.get().getCommand(SessionCommandType.BASE.getId());
                command.handleButtonEvent(event, buttonID);
            } else if (buttonID.contains(ProfileCommandType.BASE.getId())) {
                final ProfileCommand command = CivilWorkerUnion.get().getCommand(ProfileCommandType.BASE.getId());
                command.handleButtonEvent(event, buttonID);
            } else throw new IllegalArgumentException();
        } catch (Exception ex) {
            event.reply(ex.getMessage()).setEphemeral(true).queue();
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void onModalInteraction(final ModalInteractionEvent event) {
        final String modalID = event.getModalId();

        try {
            if (modalID.contains(SessionCommandType.BASE.getId())) {
                final SessionCommand command = CivilWorkerUnion.get().getCommand(SessionCommandType.BASE.getId());
                command.handleModalEvent(event, modalID);
            } else if (modalID.contains(ProfileCommandType.BASE.getId())) {
                final ProfileCommand command = CivilWorkerUnion.get().getCommand(ProfileCommandType.BASE.getId());
                command.handleModalEvent(event, modalID);
            } else throw new IllegalArgumentException();
        } catch (Exception ex) {
            event.reply(ex.getMessage()).setEphemeral(true).queue();
            System.out.println(ex.getMessage());
        }
    }
}
