package com.oneliferp.cwu.misc;

public enum ZoneType {
    RATION_FACTORY("Usine à Ration"),
    RECYCLING_CENTER("Déchèterie"),
    DISTILLERY("Distillerie"),
    PRINTING_HOUSE("Imprimerie"),
    HANGAR_PLAZA("Hangar Plaza"),
    PLAZA("Plaza"),
    DISTRICT_1("CCH1"),
    DISTRICT_2("CCH2"),
    DISTRICT_3("CCH3"),
    DISTRICT_4("CCH4"),
    LOWER_TOWN("Basse Ville"),
    QG_CWU("QG CWU"),
    HOSPITAL("Hopital");

    private final String label;

    ZoneType(final String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}
