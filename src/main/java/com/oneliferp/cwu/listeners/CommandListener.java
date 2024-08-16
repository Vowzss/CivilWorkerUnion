package com.oneliferp.cwu.listeners;

import com.oneliferp.cwu.CivilWorkerUnion;
import com.oneliferp.cwu.commands.CwuCommand;
import com.oneliferp.cwu.exceptions.CwuException;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class CommandListener extends ListenerAdapter {
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
}
