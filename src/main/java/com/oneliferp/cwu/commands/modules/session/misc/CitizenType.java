package com.oneliferp.cwu.commands.modules.session.misc;

import com.oneliferp.cwu.commands.modules.session.misc.actions.SessionPageType;

public enum CitizenType {
    LOYALIST(60, "Loyaliste"),

    CIVILIAN(60, "Civil"),

    VORTIGAUNT(60, "Vortigaunt"),

    ANTI_CITIZEN(40, "Anti-citoyen");

    private final int wage;
    private final String label;

    CitizenType(final int wage, final String label) {
        this.wage = wage;
        this.label = label;
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

    public int getWage() {
        return this.wage;
    }

    public String getLabel() {
        return this.label;
    }
}
