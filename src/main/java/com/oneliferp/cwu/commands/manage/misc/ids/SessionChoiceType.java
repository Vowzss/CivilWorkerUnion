package com.oneliferp.cwu.commands.manage.misc.ids;

import com.oneliferp.cwu.misc.IActionType;

public enum SessionChoiceType implements IActionType {
    VIEW("", "session/view"),
    DELETE("", "session/delete"),
    EDIT("", "session/edit");

    public final String root;
    public final String action;

    SessionChoiceType(final String root, final String action) {
        this.root = root;
        this.action = action;
    }

    @Override
    public String getRoot() {
        return this.root;
    }

    @Override
    public String getAction() {
        return this.action;
    }
}