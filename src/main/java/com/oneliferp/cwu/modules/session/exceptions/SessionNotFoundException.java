package com.oneliferp.cwu.modules.session.exceptions;

public class SessionNotFoundException extends Exception {
    @Override
    public String getMessage() {
        return "Vous n'avez aucune session de travail en cours.";
    }
}
