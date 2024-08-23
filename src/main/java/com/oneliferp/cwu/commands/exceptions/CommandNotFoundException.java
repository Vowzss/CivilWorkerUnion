package com.oneliferp.cwu.commands.exceptions;

import com.oneliferp.cwu.exceptions.CwuException;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public class CommandNotFoundException extends Exception {
    @Override
    public String getMessage() {
        return "Cette command n'existe pas/plus!";
    }
}
