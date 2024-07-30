package com.oneliferp.cwu.utils;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.time.Instant;

public class EmbedUtils {
    public static EmbedBuilder createDefault() {
        final EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.cyan);
        embed.setFooter("Civil Worker Union");
        embed.setTimestamp(Instant.now());
        return embed;
    }
}
