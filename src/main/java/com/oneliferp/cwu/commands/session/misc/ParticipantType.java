package com.oneliferp.cwu.commands.session.misc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.commands.session.misc.actions.SessionPageType;

public enum ParticipantType {
    @JsonProperty("loyalists")
    LOYALIST(60),

    @JsonProperty("citizens")
    CITIZEN(60),

    @JsonProperty("vortigaunts")
    VORTIGAUNT(60),

    @JsonProperty("antiCitizens")
    ANTI_CITIZEN(40);

    private final int wage;

    ParticipantType(final int wage) {
        this.wage = wage;
    }

    public int getWage() {
        return this.wage;
    }

    /*
        Utils
        */
    public static ParticipantType fromPage(final SessionPageType type) {
        return switch (type) {
            default -> throw new IllegalArgumentException();
            case LOYALISTS -> ParticipantType.LOYALIST;
            case CITIZENS -> ParticipantType.CITIZEN;
            case VORTIGAUNTS -> ParticipantType.VORTIGAUNT;
            case ANTI_CITIZENS -> ParticipantType.ANTI_CITIZEN;
        };
    }
}
