package com.oneliferp.cwu.misc;

public enum PageType {
    LOYALISTS("loyalists", "Loyaliste(s) présent(s):"),
    CITIZENS("citizens", "Citoyen(s) présent(s):"),
    VORTIGAUNTS("vortigaunts", "Vortigaunt(s) présent(s):"),
    ANTI_CITIZENS("anticitizens", "Anti Citoyens(s) présent(s):"),
    INFO("info", "Informations supplémentaires:"),
    EARNINGS("earnings", "Gain de la session:"),
    PREVIEW("preview", "Apperçu du rapport");

    private final String id;
    private final String description;

    PageType(final String id, final String description) {
        this.id = id;
        this.description = description;
    }

    public String getID() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }

    public PageType getNext() {
        if (this == EARNINGS) throw new IllegalStateException();
        final PageType[] types = PageType.values();
        return types[(this.ordinal() + 1) % types.length];
    }

    public PageType getPrevious() {
        if (this == LOYALISTS) throw new IllegalStateException();
        final PageType[] types = PageType.values();
        return types[(this.ordinal() - 1) % types.length];
    }
}
