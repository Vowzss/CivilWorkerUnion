package com.oneliferp.cwu.modules.session.exceptions;

import com.oneliferp.cwu.exceptions.CwuException;

public class SessionValidationException extends CwuException {
    @Override
    public String getMessage() {
        return "La génération du rapport a échoué car des données sont manquantes.";
    }
}
