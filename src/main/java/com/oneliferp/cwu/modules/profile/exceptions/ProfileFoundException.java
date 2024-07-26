package com.oneliferp.cwu.modules.profile.exceptions;

public class ProfileFoundException extends ProfileException {
    @Override
    public String getMessage() {
        return "Un profil associé à ce cid existe déjà.";
    }
}
