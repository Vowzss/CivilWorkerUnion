package com.oneliferp.cwu.modules.profile.exceptions;

public class ProfileNotLinkedException extends ProfileException {
    @Override
    public String getMessage() {
        return "Ce profil n'est associé à aucun utilisateur.";
    }
}
