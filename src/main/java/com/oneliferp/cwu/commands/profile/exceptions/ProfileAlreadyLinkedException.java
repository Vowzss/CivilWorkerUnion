package com.oneliferp.cwu.commands.profile.exceptions;

import com.oneliferp.cwu.exceptions.CwuException;
import net.dv8tion.jda.api.entities.User;

public class ProfileAlreadyLinkedException extends CwuException {
    final User user;

    public ProfileAlreadyLinkedException(final User user) {
        this.user = user;
    }

    @Override
    public String getMessage() {
        return String.format("Ce profil est déja associé à l'utilisateur : %s.", user.getAsMention());
    }
}
