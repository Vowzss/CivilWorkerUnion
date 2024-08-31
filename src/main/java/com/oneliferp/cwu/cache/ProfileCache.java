package com.oneliferp.cwu.cache;

import com.oneliferp.cwu.commands.modules.profile.models.ProfileModel;

public class ProfileCache extends RuntimeCache<String, ProfileModel> {
    private static ProfileCache instance;

    public static ProfileCache get() {
        if (instance == null) instance = new ProfileCache();
        return instance;
    }
}
