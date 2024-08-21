package com.oneliferp.cwu.commands.report.misc.actions;

public enum ReportPageType {
    TYPE("type", "Type de rapport :"),
    STOCK("stock", "Réapprovisionnement en :"),
    IDENTITY("identity", "Personne concernée :"),
    TOKENS("tokens", "Somme à déclarer :"),
    INFO("info", "Informations supplémentaires :"),
    PREVIEW("preview", "Apperçu du rapport");

    private final String id;
    private final String description;

    ReportPageType(final String id, final String description) {
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
