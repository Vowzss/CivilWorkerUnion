package com.oneliferp.cwu.commands.session.misc;

import com.oneliferp.cwu.commands.session.misc.ids.SessionPageType;
import net.fellbaum.jemoji.EmojiManager;

import java.util.List;

public enum SessionType {
    UNKNOWN("Inconnu", null),
    RATION("Ration", "\uD83E\uDD61"),
    DISTILLERY("Distillerie", "\uD83E\uDD64"),
    LAUNDRY("Laverie", "\uD83E\uDDFC"),
    RECYCLING("Déchèterie", EmojiManager.getByAlias(":recycle:").get().getEmoji()),
    PRINTING("Imprimerie", "\uD83D\uDDA8"),
    CLEANING("Nettoyage", "\uD83E\uDDF9"),
    RENOVATION("Rénovation", "\uD83E\uDDF1"),
    GARDENING("Jardinage", "\uD83C\uDF3F"),
    OTHER("Autre", "\uD83E\uDDF0");

    private final String label;
    private final String emoji;

    SessionType(final String label, final String emoji) {
        this.label = label;
        this.emoji = emoji;
    }

    /* Getters */
    public String getLabel() {
        return this.label;
    }

    public String getEmoji() {
        return this.emoji;
    }

    /* Utils */
    public ZoneType getZone() {
        return switch (this) {
            default -> ZoneType.UNKNOWN;
            case RATION -> ZoneType.RATION_FACTORY;
            case RECYCLING -> ZoneType.RECYCLING_CENTER;
            case DISTILLERY -> ZoneType.DISTILLERY;
            case LAUNDRY -> ZoneType.HANGAR_PLAZA;
            case PRINTING -> ZoneType.PRINTING_HOUSE;
        };
    }

    public static List<SessionPageType> getDefaultPages() {
        return List.of(SessionPageType.LOYALISTS, SessionPageType.CITIZENS, SessionPageType.VORTIGAUNTS, SessionPageType.ANTI_CITIZENS, SessionPageType.INFO);
    }
}
