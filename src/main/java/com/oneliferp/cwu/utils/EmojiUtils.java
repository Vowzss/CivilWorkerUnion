package com.oneliferp.cwu.utils;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.fellbaum.jemoji.EmojiManager;

public class EmojiUtils {
    public static String getGreenCircle() {
        return EmojiManager.getByAlias(":green_circle:").get().getUnicode();
    }

    public static String getYellowCircle() {
        return EmojiManager.getByAlias(":yellow_circle:").get().getUnicode();
    }

    public static String getRedCircle() {
        return EmojiManager.getByAlias(":red_circle:").get().getUnicode();
    }

    public static String getGlasses() {
        return EmojiManager.getByAlias(":eyeglasses:").get().getUnicode();
    }

    public static String getRightArrow() {
        return EmojiManager.getByAlias(":arrow_right:").get().getUnicode();
    }

    public static String getLeftArrow() {
        return EmojiManager.getByAlias(":arrow_left:").get().getUnicode();
    }

    public static Emoji asDiscordEmoji(final String unicode) {
        return Emoji.fromFormatted(unicode);
    }
}
