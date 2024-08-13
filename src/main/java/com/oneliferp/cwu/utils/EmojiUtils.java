package com.oneliferp.cwu.utils;

import net.fellbaum.jemoji.EmojiManager;

public class EmojiUtils {
    public static String getGreenCircle() {
        return EmojiManager.getByAlias(":green_circle:").get().getEmoji();
    }

    public static String getYellowCircle() {
        return EmojiManager.getByAlias(":yellow_circle:").get().getEmoji();
    }

    public static String getRedCircle() {
        return EmojiManager.getByAlias(":red_circle:").get().getEmoji();
    }
}
