package com.oneliferp.cwu.misc;

public enum ParticipantType {
    LOYALIST,
    CITIZEN,
    VORTIGAUNT,
    ANTI_CITIZEN,;

    public static ParticipantType getFromPage(final PageType type) {
        return switch (type) {
            case LOYALISTS -> ParticipantType.LOYALIST;
            case CITIZENS -> ParticipantType.CITIZEN;
            case VORTIGAUNTS -> ParticipantType.VORTIGAUNT;
            case ANTI_CITIZENS -> ParticipantType.ANTI_CITIZEN;
            default -> throw new IllegalArgumentException();
        };
    }
}
