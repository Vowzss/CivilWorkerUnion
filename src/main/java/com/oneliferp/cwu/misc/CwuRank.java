package com.oneliferp.cwu.misc;

public enum CwuRank {
    SUPERVISOR("Superviseur", 65, 35),
    ASSISTANT("Adjoint", 60, 30),
    EXECUTIVE("Chef", 55, 25),
    MANAGER("Sous-chef", 50, 20),
    CONFIRMED("Confirmé", 45, 15),
    INTERMEDIATE("Intermédiaire", 40, 10),
    APPRENTICE("Apprenti", 35, 5),
    RECRUIT("Recrue", 30, 0);


    private final String label;
    private final int sessionRoyalty;
    private final int branchRoyalty;

    CwuRank(final String label, final int sessionRoyalty, final int branchRoyalty) {
        this.label = label;
        this.sessionRoyalty = sessionRoyalty;
        this.branchRoyalty = branchRoyalty;
    }

    public String getLabel() {
        return this.label;
    }

    public int getSessionRoyalty() {
        return this.sessionRoyalty;
    }

    public int getBranchRoyalty() {
        return this.branchRoyalty;
    }
}
