package com.oneliferp.cwu.exceptions;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public abstract class CwuException extends Exception {
    final IReplyCallback callback;

    protected CwuException(final IReplyCallback callback) {
        this.callback = callback;
    }

    public void reply() {
        this.callback.reply(this.getMessage()).setEphemeral(true).queue();
    }
}
