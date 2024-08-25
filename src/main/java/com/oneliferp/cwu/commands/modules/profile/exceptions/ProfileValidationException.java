package com.oneliferp.cwu.commands.modules.profile.exceptions;

import com.oneliferp.cwu.exceptions.CwuException;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public class ProfileValidationException extends CwuException {
    public ProfileValidationException(final IReplyCallback callback) {
        super(callback);
    }

    @Override
    public String getMessage() {
        return "Cet employé n'existe pas dans notre registre.";
    }
}
