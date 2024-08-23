package com.oneliferp.cwu.listeners;

import com.oneliferp.cwu.CivilWorkerUnion;
import com.oneliferp.cwu.commands.exceptions.CommandNotFoundException;
import com.oneliferp.cwu.commands.utils.CommandContext;
import com.oneliferp.cwu.exceptions.CwuException;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ButtonListener extends ListenerAdapter {
    @Override
    public void onButtonInteraction(final ButtonInteractionEvent event) {
        final String ID = event.getButton().getId();
        if (ID == null) throw new IllegalArgumentException();
        final CommandContext eventType = new CommandContext(ID);

        try {
            final var command = CivilWorkerUnion.get().getCommandFromId(eventType.getCommand());
            command.handleButtonInteraction(event, eventType);
        } catch (CwuException ex) {
            ex.reply();
        } catch (CommandNotFoundException ex) {
            event.reply(ex.getMessage()).setEphemeral(true).queue();
        }
    }
}
