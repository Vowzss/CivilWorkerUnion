package com.oneliferp.cwu.exceptions;

public class IdentityMalformedException extends CwuException {
    @Override
    public String getMessage() {
        return "Le format de l'identité est érroné, veuillez respecter le format suivant: Nom Prénom, #12345.";
    }
}
