package com.oneliferp.cwu.modules.report.misc;

import java.util.HashMap;
import java.util.Map;

public enum ReportCommandType {
    BASE("rapport", "Vous permet de créer un rapport.");

    /*
    Perform easy lookup
    */
    private static final Map<String, ReportCommandType> IDS = new HashMap<>();
    static {
        for (final var type : values()) {
            IDS.put(type.getId(), type);
        }
    }

    public static ReportCommandType fromId(final String id) {
        final var type = IDS.get(id);
        if (type == null) throw new IllegalArgumentException("No enum constant with id " + id);
        return type;
    }

    private final String id;
    private final String description;

    ReportCommandType(final String id, final String description) {
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
