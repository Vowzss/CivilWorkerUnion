package com.oneliferp.cwu.commands.modules.profile.exceptions;

import com.oneliferp.cwu.exceptions.CwuException;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public class ProfileNotFoundException extends CwuException {
    public ProfileNotFoundException(final IReplyCallback callback) {
        super(callback);
    }

    @Override
    public String getMessage() {
        return "Ce profil n'existe pas dans notre registre.";
    }
}
