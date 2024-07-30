package com.oneliferp.cwu.modules.profile.exceptions;

import com.oneliferp.cwu.exceptions.CwuException;

public class ProfileNotLinkedException extends CwuException {
    @Override
    public String getMessage() {
        return "Ce profil n'est associé à aucun utilisateur.";
    }
}
