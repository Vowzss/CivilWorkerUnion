package com.oneliferp.cwu.modules.session.misc;

import java.util.HashMap;
import java.util.Map;

public enum SessionCommandType {
    BASE("session", null),
    RATION("ration", "Permet de créer un rapport de session ration."),
    LAUNDRY("laverie", "Permet de créer un rapport de session laverie.");

    /*
    Perform easy lookup
    */
    private static final Map<String, SessionCommandType> IDS = new HashMap<>();
    static {
        for (final var type : values()) {
            IDS.put(type.getId(), type);
        }
    }

    public static SessionCommandType fromId(final String id) {
        final var type = IDS.get(id);
        if (type == null) throw new IllegalArgumentException("No enum constant with id " + id);
        return type;
    }

    private final String id;
    private final String description;

    SessionCommandType(final String id, final String description) {
        this.id = id;
        this.description = description;
    }

    public String getId() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }
}
