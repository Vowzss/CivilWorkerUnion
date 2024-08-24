package com.oneliferp.cwu.misc;

public enum CwuRank {
    SUPERVISOR("Superviseur", 0.65f, 0.35f),
    ASSISTANT("Adjoint", 0.60f, 0.30f),
    EXECUTIVE("Chef", 0.55f, 0.25f),
    MANAGER("Sous-chef", 0.50f, 0.20f),
    CONFIRMED("Confirmé", 0.45f, 0.15f),
    INTERMEDIATE("Intermédiaire", 0.40f, 0.10f),
    APPRENTICE("Apprenti", 0.35f, 0.5f),
    RECRUIT("Recrue", 30, 0.0f);


    private final String label;
    private final float sessionRoyalty;
    private final float branchRoyalty;

    CwuRank(final String label, final float sessionRoyalty, final float branchRoyalty) {
        this.label = label;
        this.sessionRoyalty = sessionRoyalty;
        this.branchRoyalty = branchRoyalty;
    }

    public String getLabel() {
        return this.label;
    }

    public float getSessionRoyalty() {
        return this.sessionRoyalty;
    }

    public float getBranchRoyalty() {
        return this.branchRoyalty;
    }
}
