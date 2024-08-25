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

    public static String getIdCard() {
        return EmojiManager.getByAlias(":identification_card:").get().getUnicode();
    }

    public static String getPencilMemo() {
        return EmojiManager.getByAlias(":pencil:").get().getUnicode();
    }

    public static String getPencil() {
        return EmojiManager.getByAlias(":pencil2:").get().getUnicode();
    }

    public static String getPenBall() {
        return EmojiManager.getByAlias(":pen_ballpoint:").get().getUnicode();
    }
    public static String getPenFountain() {
        return EmojiManager.getByAlias(":pen_fountain:").get().getUnicode();
    }

    public static String getScales() {
        return EmojiManager.getByAlias(":scales:").get().getUnicode();
    }

    public static String getRightArrow() {
        return EmojiManager.getByAlias(":arrow_right:").get().getUnicode();
    }

    public static String getLeftArrow() {
        return EmojiManager.getByAlias(":arrow_left:").get().getUnicode();
    }

    public static String getHamburger() {
        return EmojiManager.getByAlias(":hamburger:").get().getUnicode();
    }

    public static String getHospital() {
        return EmojiManager.getByAlias(":hospital:").get().getUnicode();
    }

    public static String getKey() {
        return EmojiManager.getByAlias(":key:").get().getUnicode();
    }

    /* Helpers */

    public static Emoji asDiscordEmoji(final String unicode) {
        return Emoji.fromFormatted(unicode);
    }

    public static String getGreenOrRedCircle(final boolean state) {
        return state ? EmojiUtils.getGreenCircle() : EmojiUtils.getRedCircle();
    }

    public static String getGreenOrYellowCircle(final boolean state) {
        return state ? EmojiUtils.getGreenCircle() : EmojiUtils.getYellowCircle();
    }

    public static String getAnnotationGuide() {
        final StringBuilder sb = new StringBuilder();
        sb.append("**Détails des annotations:**").append("\n");
        sb.append(String.format("%s  le champ annoté doit être spécifié.", EmojiUtils.getRedCircle())).append("\n");
        sb.append(String.format("%s  un des champs annoté doit être défini.", EmojiUtils.getYellowCircle())).append("\n");
        sb.append(String.format("%s  le champ est optionnel ou déjà rempli.", EmojiUtils.getGreenCircle())).append("\n");
        return sb.toString();
    }
}
