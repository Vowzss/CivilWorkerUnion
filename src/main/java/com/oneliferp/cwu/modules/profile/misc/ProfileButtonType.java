package com.oneliferp.cwu.modules.profile.misc;

import java.util.HashMap;
import java.util.Map;

public enum ProfileButtonType {
    STATS("cwu_profile_stats"),
    RETURN("cwu_profile_return"),
    LINK("cwu_profile_link"),
    UNLINK("cwu_profile_unlink"),
    DELETE("cwu_profile_delete"),

    CONFIRM_LINK("cwu_profile_confirm.link"),
    CANCEL_LINK("cwu_profile_cancel.link"),

    CONFIRM_UNLINK("cwu_profile_confirm.unlink"),
    CANCEL_UNLINK("cwu_profile_cancel.unlink"),

    CONFIRM_DELETE("cwu_profile_confirm.delete"),
    CANCEL_DELETE("cwu_profile_cancel.delete");

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
