package com.oneliferp.cwu.misc;

import com.oneliferp.cwu.modules.session.misc.SessionCommandType;

public enum SessionType {
    RATION("Ration", true, "\uD83E\uDD61"),
    DISTILLERY("Distillerie", true, "\uD83E\uDD64"),
    LAUNDRY("Laverie", true, "\uD83E\uDDFC"),
    RECYCLING("Déchèterie", true, "\uD83D\uDCE6"),
    PRINTING("Imprimerie", false, "\uD83D\uDDA8"),
    CLEANING("Nettoyage", false, "\uD83E\uDDF9"),
    RENOVATION("Rénovation", false, "\uD83E\uDDF1"),
    GARDENING("Jardinage", false, "\uD83C\uDF3F"),
    OTHER("Autre", false, "\uD83E\uDDF3");

    private final String label;
    private final String emoji;
    private final boolean isProfitable;

    SessionType(final String label, final boolean isProfitable, final String emoji) {
        this.label = label;
        this.isProfitable = isProfitable;
        this.emoji = emoji;
    }

    /*
    Getters
    */
    public String getLabel() {
        return this.label;
    }

    public boolean isProfitable() {
        return this.isProfitable;
    }

    public String getEmoji() {
        return this.emoji;
    }


    /*
    Utils
    */
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

    public static SessionType fromCommandType(final SessionCommandType type) {
        return switch (type) {
            default -> throw new IllegalArgumentException();
            case RATION -> RATION;
            case DISTILLERY -> DISTILLERY;
            case LAUNDRY -> LAUNDRY;
            case RECYCLING -> RECYCLING;
            case PRINTING -> PRINTING;
            case CLEANING -> CLEANING;
            case RENOVATION -> RENOVATION;
            case GARDENING -> GARDENING;
            case OTHER -> OTHER;
        };
    }
}
