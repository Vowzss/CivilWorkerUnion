package com.oneliferp.cwu.commands.modules.session.misc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.commands.modules.session.misc.actions.SessionPageType;

public enum CitizenType {
    @JsonProperty("loyalists")
    LOYALIST(60, "Loyaliste"),

    @JsonProperty("civilians")
    CIVILIAN(60, "Civil"),

    @JsonProperty("vortigaunts")
    VORTIGAUNT(60, "Vortigaunt"),

    @JsonProperty("antiCitizens")
    ANTI_CITIZEN(40, "Anti-citoyen");

    private final int wage;
    private final String label;

    CitizenType(final int wage, final String label) {
        this.wage = wage;
        this.label = label;
    }

    public int getWage() {
        return this.wage;
    }

    public String getLabel() {
        return this.label;
    }

    /* Utils */
    public static CitizenType fromPage(final SessionPageType type) {
        return switch (type) {
            default -> throw new IllegalArgumentException();
            case LOYALISTS -> CitizenType.LOYALIST;
            case CIVILIANS -> CitizenType.CIVILIAN;
            case VORTIGAUNTS -> CitizenType.VORTIGAUNT;
            case ANTI_CITIZENS -> CitizenType.ANTI_CITIZEN;
        };
    }
}
