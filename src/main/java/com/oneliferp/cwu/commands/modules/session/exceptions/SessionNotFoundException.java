package com.oneliferp.cwu.commands.modules.session.exceptions;

import com.oneliferp.cwu.exceptions.CwuException;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public class SessionNotFoundException extends CwuException {
    public SessionNotFoundException(final IReplyCallback callback) {
        super(callback);
    }

    @Override
    public String getMessage() {
        return "Vous n'avez aucune session de travail en cours.";
    }
}
