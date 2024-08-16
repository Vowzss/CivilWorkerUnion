package com.oneliferp.cwu.commands.session.misc.ids;

public enum SessionPageType {
    ZONE("zone", "Emplacement de la session :"),
    LOYALISTS("loyalists", "Loyaliste(s) présent(s) :"),
    CITIZENS("citizens", "Citoyen(s) présent(s) :"),
    VORTIGAUNTS("vortigaunts", "Vortigaunt(s) présent(s) :"),
    ANTI_CITIZENS("anticitizens", "Anti Citoyens(s) présent(s) :"),
    INFO("info", "Informations supplémentaires :"),
    TOKENS("tokens", "Gain de la session :"),
    PREVIEW("preview", "Apperçu de la session");

    private final String id;
    private final String description;

    SessionPageType(final String id, final String description) {
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
