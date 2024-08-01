package com.oneliferp.cwu.listeners;

import com.oneliferp.cwu.CivilWorkerUnion;
import com.oneliferp.cwu.commands.CwuCommand;
import com.oneliferp.cwu.exceptions.CommandNotFoundException;
import com.oneliferp.cwu.modules.profile.commands.ProfileCommand;
import com.oneliferp.cwu.modules.profile.misc.ProfileCommandType;
import com.oneliferp.cwu.modules.session.commands.SessionCommand;
import com.oneliferp.cwu.modules.session.misc.SessionCommandType;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(final SlashCommandInteractionEvent event) {
        final String commandName = event.getName();

        try {
            final CwuCommand command = CivilWorkerUnion.get().getCommand(commandName);
            if (command == null) throw new CommandNotFoundException();
            command.handleCommandInteraction(event);
        } catch (Exception ex) {
            event.reply("\uD83D\uDCA5 " + ex.getMessage())
                    .setEphemeral(true).queue();

            ex.printStackTrace();
        }
    }

    @Override
    public void onStringSelectInteraction(final StringSelectInteractionEvent event) {
        final String ID = event.getComponentId();

        try {
            if (ID.contains(SessionCommandType.BASE.getId())) {
                final SessionCommand command = CivilWorkerUnion.get().getCommand(SessionCommandType.BASE.getId());
                command.handleSelectionInteraction(event, ID);
            } else if (ID.contains(ProfileCommandType.BASE.getId())) {
                final ProfileCommand command = CivilWorkerUnion.get().getCommand(ProfileCommandType.BASE.getId());
                command.handleSelectionInteraction(event, ID);
            } else throw new IllegalArgumentException();
        } catch (Exception ex) {
            event.reply(ex.getMessage())
                    .setEphemeral(true).queue();

            ex.printStackTrace();
        }
    }

    @Override
    public void onButtonInteraction(final ButtonInteractionEvent event) {
        final String ID = event.getButton().getId();
        if (ID == null) throw new IllegalArgumentException();

        try {
            if (ID.contains(SessionCommandType.BASE.getId())) {
                final SessionCommand command = CivilWorkerUnion.get().getCommand(SessionCommandType.BASE.getId());
                command.handleButtonInteraction(event, ID);
            } else if (ID.contains(ProfileCommandType.BASE.getId())) {
                final ProfileCommand command = CivilWorkerUnion.get().getCommand(ProfileCommandType.BASE.getId());
                command.handleButtonInteraction(event, ID);
            } else throw new IllegalArgumentException();
        } catch (Exception ex) {
            event.reply(ex.getMessage())
                    .setEphemeral(true).queue();

            ex.printStackTrace();
        }
    }

    @Override
    public void onModalInteraction(final ModalInteractionEvent event) {
        final String ID = event.getModalId();

        try {
            if (ID.contains(SessionCommandType.BASE.getId())) {
                final SessionCommand command = CivilWorkerUnion.get().getCommand(SessionCommandType.BASE.getId());
                command.handleModalInteraction(event, ID);
            } else if (ID.contains(ProfileCommandType.BASE.getId())) {
                final ProfileCommand command = CivilWorkerUnion.get().getCommand(ProfileCommandType.BASE.getId());
                command.handleModalInteraction(event, ID);
            } else throw new IllegalArgumentException();
        } catch (Exception ex) {
            event.reply("\uD83D\uDCA5 " + ex.getMessage())
                    .setEphemeral(true).queue();

            ex.printStackTrace();
        }
    }
}
