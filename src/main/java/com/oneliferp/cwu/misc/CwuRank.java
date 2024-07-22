package com.oneliferp.cwu.misc;

public enum CwuRank {
    RECRUIT("Recrue"),
    APPRENTICE("Apprenti"),
    INTERMEDIATE("Intermédiaire"),
    CONFIRMED("Confirmé"),
    MANAGER("Sous-chef"),
    EXECUTIVE("Chef");

    private final String label;

    CwuRank(final String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }


}
