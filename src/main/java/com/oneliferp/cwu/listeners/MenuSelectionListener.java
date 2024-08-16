package com.oneliferp.cwu.listeners;

import com.oneliferp.cwu.CivilWorkerUnion;
import com.oneliferp.cwu.commands.CommandContext;
import com.oneliferp.cwu.exceptions.CwuException;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MenuSelectionListener extends ListenerAdapter {
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
}
