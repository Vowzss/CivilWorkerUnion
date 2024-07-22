package com.oneliferp.cwu.modules.profile.misc;

import java.util.HashMap;
import java.util.Map;

public enum ProfileButtonType {
    STATS("cwu_stats_profile"),
    RETURN("cwu_return_profile"),
    DELETE("cwu_delete_profile");

    /*
    Perform easy lookup
    */
    private static final Map<String, ProfileButtonType> IDS = new HashMap<>();
    static {
        for (final var type : values()) {
            IDS.put(type.getId(), type);
        }
    }
    public static ProfileButtonType fromId(final String id) {
        final var type = IDS.get(id);
        if (type == null) throw new IllegalArgumentException("No enum constant with id " + id);
        return type;
    }

    private final String id;

    ProfileButtonType(final String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}
