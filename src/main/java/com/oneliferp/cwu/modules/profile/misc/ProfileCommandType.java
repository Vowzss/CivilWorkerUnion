package com.oneliferp.cwu.modules.profile.misc;

import java.util.HashMap;
import java.util.Map;

public enum ProfileCommandType {
    BASE("profil",  null),
    CREATE("create", "Permet de créer un profil CWU."),
    DELETE("delete", "Permet de supprimer un profil CWU."),
    VIEW("view", "Permet de visualiser un profil CWU");

    /*
    Perform easy lookup
    */
    private static final Map<String, ProfileCommandType> IDS = new HashMap<>();
    static {
        for (final var type : values()) {
            IDS.put(type.getId(), type);
        }
    }

    public static ProfileCommandType fromId(final String id) {
        final var type = IDS.get(id);
        if (type == null) throw new IllegalArgumentException("No enum constant with id " + id);
        return type;
    }

    private final String id;
    private final String description;

    ProfileCommandType(final String id, final String description) {
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
