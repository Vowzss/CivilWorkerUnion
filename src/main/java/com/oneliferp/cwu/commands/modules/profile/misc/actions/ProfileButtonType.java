package com.oneliferp.cwu.commands.modules.profile.misc.actions;

import com.oneliferp.cwu.misc.IActionType;

public enum ProfileButtonType implements IActionType {
    STATS("btn#cwu_profile", "stats"),
    UPDATE("btn#cwu_profile", "update"),
    UPDATE_CONFIRM("btn#cwu_profile", "update/confirm"),
    UPDATE_CANCEL("btn#cwu_profile", "update/cancel"),

    DELETE("btn#cwu_profile", "delete"),
    DELETE_CONFIRM("btn#cwu_profile", "delete/confirm"),
    DELETE_CANCEL("btn#cwu_profile", "delete/cancel");


    private final String root;
    private final String action;

    ProfileButtonType(final String root, final String action) {
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
