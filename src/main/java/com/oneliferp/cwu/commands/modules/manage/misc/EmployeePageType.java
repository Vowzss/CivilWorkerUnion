package com.oneliferp.cwu.commands.modules.manage.misc;

public enum EmployeePageType {
    IDENTITY("identity", "Identité de l'employé :"),
    ID("id", "Identifiant discord :"),
    BRANCH("branch", "Branche de l'employé :"),
    RANK("rank", "Rang de l'employé :"),
    JOINED_AT("joined_at", "Date du recrutement :"),
    PREVIEW("preview", "Apperçu de la fiche employé");

    private final String id;
    private final String description;

    EmployeePageType(final String id, final String description) {
        this.id = id;
        this.description = description;
    }

    public String getID() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }
}
