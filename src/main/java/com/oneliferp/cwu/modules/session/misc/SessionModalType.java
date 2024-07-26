package com.oneliferp.cwu.modules.session.misc;

import java.util.HashMap;
import java.util.Map;

public enum SessionModalType {
    PARTICIPANTS("cwu_session_fill_participants"),
    INFO("cwu_session_fill_info"),
    EARNINGS("cwu_session_fill_earnings");

    /*
    Perform easy lookup
    */
    private static final Map<String, SessionModalType> IDS = new HashMap<>();
    static {
        for (final var type : values()) {
            IDS.put(type.getId(), type);
        }
    }

    public static SessionModalType fromId(final String id) {
        final var type = IDS.get(id);
        if (type == null) throw new IllegalArgumentException("No enum constant with id " + id);
        return type;
    }

    private final String id;

    SessionModalType(final String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}
