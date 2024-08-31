package com.oneliferp.cwu.commands.exceptions;

public class CommandNotFoundException extends Exception {
    @Override
    public String getMessage() {
        return "Cette command n'existe pas/plus!";
    }
}
