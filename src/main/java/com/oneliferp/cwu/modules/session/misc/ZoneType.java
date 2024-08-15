package com.oneliferp.cwu.modules.session.misc;

public enum ZoneType {
    UNKNOWN("Inconnu", null),
    RATION_FACTORY("Usine à Ration", "\uD83D\uF3ED"),
    RECYCLING_CENTER("Déchèterie", "\uD83D\uF3ED"),
    DISTILLERY("Distillerie", "\uD83D\uF3ED"),
    PRINTING_HOUSE("Imprimerie", "\uD83D\uF3ED"),
    HANGAR_PLAZA("Hangar Plaza", "\uD83D\uF3ED"),
    PLAZA("Plaza", "\uD83C\uDFE5"),
    DISTRICT_1("Appartements CCH1", "\uD83D\uF3E1"),
    DISTRICT_2("Appartements CCH2", "\uD83D\uF3EC"),
    DISTRICT_3("Appartements CCH3", "\uD83D\uF3E8"),
    DISTRICT_4("Appartements CCH4", "\uD83D\uF3E2"),
    LOWER_TOWN("Basse Ville", "\uD83D\uF303"),
    QG_CWU("QG CWU", "\uD83D\uDDC3"),
    HOSPITAL("Hopital", "\uD83C\uDFE5");

    private final String label;
    private final String emoji;

    ZoneType(final String label, final String emoji) {
        this.label = label;
        this.emoji = emoji;
    }

    public String getLabel() {
        return this.label;
    }

    public String getEmoji() {
        return this.emoji;
    }
}
