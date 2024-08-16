package com.oneliferp.cwu.commands.report.exceptions;

import com.oneliferp.cwu.exceptions.CwuException;

public class ReportNotFoundException extends CwuException {
    @Override
    public String getMessage() {
        return "Vous n'avez aucun rapport en cours d'écriture.";
    }
}
