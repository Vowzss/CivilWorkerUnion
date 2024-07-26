package com.oneliferp.cwu.modules.profile.exceptions;

public class ProfileNotFoundException extends ProfileException {
    @Override
    public String getMessage() {
        return "Ce profil n'existe pas dans notre registre.";
    }
}
