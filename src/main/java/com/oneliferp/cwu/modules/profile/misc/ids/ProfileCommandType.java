package com.oneliferp.cwu.modules.profile.misc.ids;

import com.oneliferp.cwu.misc.ICommandType;

public enum ProfileCommandType implements ICommandType {
    BASE("profil",  "Vous permet de gérer les profiles CWU."),
    CREATE("ajouter", "Permet d'ajouter un profil CWU."),
    DELETE("supprimer", "Permet de supprimer un profil CWU."),
    VIEW("visualiser", "Permet de visualiser un profil CWU.");

    private final String name;
    private final String description;

    ProfileCommandType(final String name, final String description) {
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
