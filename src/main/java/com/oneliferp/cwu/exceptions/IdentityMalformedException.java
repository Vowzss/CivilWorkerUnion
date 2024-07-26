package com.oneliferp.cwu.exceptions;

public class IdentityMalformedException extends Exception {
    @Override
    public String getMessage() {
        return "Le format de l'identité est érroné.";
    }
}
