package com.oneliferp.cwu.Commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public class ClearCommand extends CwuCommand {
    public ClearCommand() {
        super("clear", "Permet d'effacer les 20 derniers messages envoyés.");
    }

    @Override
    public void handleCommandEvent(SlashCommandInteractionEvent event) {
        if (event.isFromGuild()) {
            event.reply("Aucun message ne peux être supprimé.").queue();
            return;
        }

        final PrivateChannel channel = event.getChannel().asPrivateChannel();
        channel.getHistory().retrievePast(20).queue(history -> {
            final int messageCount = deleteBotHistory(channel, history, event.getUser().getIdLong());
            event.reply(String.format("%d message(s) supprimé(s).", messageCount)).setEphemeral(true).queue();
        });
    }

    public static int deleteBotHistory(final MessageChannel channel, final List<Message> history, final long userID) {
        final List<Message> messages = history.stream()
                .filter(message -> message.getAuthor().getIdLong() != userID)
                .toList();

        channel.purgeMessages(messages);

        return messages.size();
    }
}
