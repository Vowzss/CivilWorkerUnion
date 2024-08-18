package com.oneliferp.cwu.commands.manage.misc.ids;

import com.oneliferp.cwu.misc.IActionType;

public enum ProfileChoiceType implements IActionType {
    CREATE("", "profile/create"),
    DELETE("", "profile/delete"),
    EDIT("", "profile/edit"),
    VIEW("", "profile/view");

    public final String root;
    public final String action;

    ProfileChoiceType(final String root, final String action) {
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
