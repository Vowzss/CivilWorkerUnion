package com.oneliferp.cwu.exceptions;

public class CommandNotFoundException extends CwuException {
    @Override
    public String getMessage() {
        return "Cette command n'existe pas/plus!";
    }
}
