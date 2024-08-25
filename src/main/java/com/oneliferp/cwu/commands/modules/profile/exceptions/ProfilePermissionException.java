package com.oneliferp.cwu.commands.modules.profile.exceptions;

import com.oneliferp.cwu.exceptions.CwuException;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public class ProfilePermissionException extends CwuException {
    public ProfilePermissionException(IReplyCallback callback) {
        super(callback);
    }

    @Override
    public String getMessage() {
        return "Vous n'êtes pas un membre de l'administration de la CWU.";
    }
}
