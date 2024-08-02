package com.oneliferp.cwu.modules.report.exceptions;

public class ReportNotFoundException extends Exception {
    @Override
    public String getMessage() {
        return "Vous n'avez aucun rapport en cours d'écriture.";
    }
}
