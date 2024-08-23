package com.oneliferp.cwu.commands.modules.report.exceptions;

import com.oneliferp.cwu.exceptions.CwuException;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public class ReportNotFoundException extends CwuException {
    public ReportNotFoundException(final IReplyCallback callback) {
        super(callback);
    }

    @Override
    public String getMessage() {
        return "Vous n'avez aucun rapport en cours d'écriture.";
    }
}
