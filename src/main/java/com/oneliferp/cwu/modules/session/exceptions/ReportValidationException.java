package com.oneliferp.cwu.modules.session.exceptions;

public class ReportValidationException extends Exception {
    @Override
    public String getMessage() {
        return "La génération du rapport a échoué car des données sont manquantes.";
    }
}
