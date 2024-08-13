package com.oneliferp.cwu.modules.profile.misc;

import com.oneliferp.cwu.misc.ICommandType;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum ProfileCommandType implements ICommandType {
    BASE("profil",  "Vous permet de gérer les profiles CWU."),
    CREATE("ajouter", "Permet d'ajouter un profil CWU."),
    DELETE("supprimer", "Permet de supprimer un profil CWU."),
    VIEW("visualiser", "Permet de visualiser un profil CWU.");

    /*
    Perform easy lookup
    */
    private static final Map<String, ProfileCommandType> IDS = Arrays.stream(ProfileCommandType.values())
            .skip(1).collect(Collectors.toMap(ProfileCommandType::getName, e -> e));

    public static ProfileCommandType resolveType(final String id) {
        final var type = IDS.get(id);
        if (type == null) throw new IllegalArgumentException("No enum constant with id " + id);
        return type;
    }

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
