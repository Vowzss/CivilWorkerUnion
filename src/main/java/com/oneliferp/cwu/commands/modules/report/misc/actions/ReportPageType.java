package com.oneliferp.cwu.commands.modules.report.misc.actions;

public enum ReportPageType {
    TYPE("type", "Type de rapport :"),
    STOCK("stock", "Réapprovisionnement en :"),
    TENANT("tenant", "Identité du locataire :"),
    MERCHANT("merchant", "Identité du commerçant :"),
    PATIENT("patient", "Identité du patient :"),
    HEALTHINESS("healthiness", "Salubrité :"),
    MEDICAL("medical", "Détails de l'intervention :"),
    TAX("tax", "Montant de la TVA :"),
    RENT("rent", "Montant du loyé :"),
    COST("cost", "Montant de la commande :"),
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
