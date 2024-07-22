package com.oneliferp.cwu.misc;

import net.dv8tion.jda.api.entities.emoji.Emoji;

public enum SessionType {
    RATION("Ration", "\uD83E\uDD61"),
    CARDBOARD("Carton", "\uD83D\uDCE6"),
    CANS ("Canettes", "\uD83E\uDD64"),
    LAUNDRY("Laverie", "\uD83E\uDDFC"),
    PRINTING("Imprimerie", "\uD83D\uDDA8"),
    CLEANING("Nettoyage", "\uD83E\uDDF9"),
    RENOVATION("Rénovation", "\uD83E\uDDF1"),
    GARDENING("Jardinage", "\uD83C\uDF3F"),
    OTHER("Autre", "\uD83E\uDDF3");

    private final String label;
    private final String emoji;

    SessionType(final String label, final String emoji) {
        this.label = label;
        this.emoji = emoji;
    }

    public String getLabel() {
        return label;
    }

    public String getEmoji() {
        return emoji;
    }

    public ZoneType getZone() {
        return switch (this) {
            default -> null;
            case RATION -> ZoneType.RATION_FACTORY;
            case CARDBOARD -> ZoneType.RECYCLING_CENTER;
            case CANS -> ZoneType.DISTILLERY;
            case LAUNDRY -> ZoneType.HANGAR_PLAZA;
            case PRINTING -> ZoneType.PRINTING_HOUSE;
        };
    }
}
