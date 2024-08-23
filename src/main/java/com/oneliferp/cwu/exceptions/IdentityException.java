package com.oneliferp.cwu.exceptions;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public class IdentityException extends CwuException {
    public IdentityException(final IReplyCallback callback) {
        super(callback);
    }

    @Override
    public String getMessage() {
        return "Le format de l'identité est érroné, veuillez respecter le format suivant: Nom Prénom, #12345.";
    }
}
