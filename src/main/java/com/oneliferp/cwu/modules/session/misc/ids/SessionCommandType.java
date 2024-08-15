package com.oneliferp.cwu.modules.session.misc.ids;

import com.oneliferp.cwu.misc.ICommandType;

public enum SessionCommandType implements ICommandType {
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

    private final String name;
    private final String description;

    SessionCommandType(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
