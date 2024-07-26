package com.oneliferp.cwu.modules.profile.exceptions;

import net.dv8tion.jda.api.entities.User;

public class ProfileAlreadyLinkedException extends ProfileException {
    final User user;

    public ProfileAlreadyLinkedException(final User user) {
        this.user = user;
    }

    @Override
    public String getMessage() {
        return String.format("Ce profil est déja associé à l'utilisateur : %s.", user.getAsMention());
    }
}
