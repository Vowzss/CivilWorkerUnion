package com.oneliferp.cwu.listeners;

import com.oneliferp.cwu.CivilWorkerUnion;
import com.oneliferp.cwu.commands.exceptions.CommandNotFoundException;
import com.oneliferp.cwu.commands.utils.CommandContext;
import com.oneliferp.cwu.exceptions.CwuException;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ModalListener extends ListenerAdapter {
    @Override
    public void onModalInteraction(final ModalInteractionEvent event) {
        final CommandContext eventType = new CommandContext(event.getModalId());

        try {
            final var command = CivilWorkerUnion.get().getCommandFromId(eventType.getCommand());
            System.out.println(eventType.getCommand());
            command.handleModalInteraction(event, eventType);
        } catch (CwuException ex) {
            ex.reply();
        } catch (CommandNotFoundException ex) {
            event.reply(ex.getMessage()).setEphemeral(true).queue();
        }
    }
}
