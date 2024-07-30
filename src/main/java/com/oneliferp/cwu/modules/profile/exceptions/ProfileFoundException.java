package com.oneliferp.cwu.modules.profile.exceptions;

import com.oneliferp.cwu.exceptions.CwuException;

public class ProfileFoundException extends CwuException {
    @Override
    public String getMessage() {
        return "Un profil associé à ce cid existe déjà.";
    }
}
