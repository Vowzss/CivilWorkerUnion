package com.oneliferp.cwu.misc;

public enum CwuRank {
    RECRUIT("Recrue", 6),
    APPRENTICE("Apprenti", 5),
    INTERMEDIATE("Intermédiaire", 4),
    CONFIRMED("Confirmé", 3),
    MANAGER("Sous-chef", 2),
    EXECUTIVE("Chef", 1),
    SUPERVISOR("Superviseur", 0);

    private final String label;
    private final int order;

    CwuRank(final String label, final int order) {
        this.label = label;
        this.order = order;
    }

    public String getLabel() {
        return this.label;
    }

    public int getOrder() {
        return this.order;
    }
}
