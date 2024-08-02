package com.oneliferp.cwu.modules.report.misc;

public enum ReportPageType {
    TYPE("type", "Type de rapport:"),
    STOCK("stock", "Réapprovisionnement en:"),
    TOKENS("tokens", null),
    IDENTITY("identity", "Personne concernée:"),
    INFO("info", "Informations supplémentaires:"),
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

    public ReportPageType getNext() {
        final ReportPageType[] types = ReportPageType.values();
        return types[(this.ordinal() + 1) % types.length];
    }

    public ReportPageType getPrevious() {
        final ReportPageType[] types = ReportPageType.values();
        return types[(this.ordinal() - 1) % types.length];
    }
}
