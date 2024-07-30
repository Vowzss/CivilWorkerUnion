package com.oneliferp.cwu.modules.session.exceptions;

import com.oneliferp.cwu.exceptions.CwuException;

public class SessionNotFoundException extends CwuException {
    @Override
    public String getMessage() {
        return "Vous n'avez aucune session de travail en cours.";
    }
}
