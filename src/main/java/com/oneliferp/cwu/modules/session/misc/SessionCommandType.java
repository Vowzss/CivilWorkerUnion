package com.oneliferp.cwu.modules.session.misc;

import java.util.HashMap;
import java.util.Map;

public enum SessionCommandType {
    BASE("session", "Vous permet de créer des raports de session."),
    RATION("ration", "Permet de créer une session à l'usine à ration."),
    DISTILLERY("distillerie", "Permet de créer une session à la distillerie."),
    LAUNDRY("laverie", "Permet de créer une session à la laverie."),
    RECYCLING("decheterie", "Permet de créer une session à la déchèterie."),
    PRINTING("imprimerie", "Permet de créer une session d'impression d'affiche."),
    CLEANING("nettoyage", "Permet de créer une session de nettoyage."),
    RENOVATION("renovation", "Permet de créer une session de rénovation."),
    GARDENING("jardinage", "Permet de créer une session de jardinage."),
    OTHER("autre", "Permet de créer une session personalisé.");

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
