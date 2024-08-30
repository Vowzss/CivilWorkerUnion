package com.oneliferp.cwu.commands.modules.profile.misc.actions;

import com.oneliferp.cwu.misc.IActionType;

public enum ProfileButtonType implements IActionType {
    STATS("btn#cwu_profile", "stats"),
    UPDATE_REQUEST("btn#cwu_profile", "update"),
    UPDATE_CONFIRM("btn#cwu_profile", "update/confirm"),
    UPDATE_CANCEL("btn#cwu_profile", "update/cancel"),

    DELETE_REQUEST("btn#cwu_profile", "delete"),
    DELETE_CONFIRM("btn#cwu_profile", "delete/confirm"),
    DELETE_CANCEL("btn#cwu_profile", "delete/cancel"),

    EDIT("btn#cwu_profile", "edit"),
    CREATE("btn#cwu_profile", "create"),
    OVERWRITE("btn#cwu_profile", "overwrite"),
    START("btn#cwu_profile", "profile/start"),
    ABORT("btn#cwu_profile", "profile/abort"),
    PAGE("btn#cwu_profile", "page"),
    FILL("btn#cwu_profile", "fill"),
    CLEAR("btn#cwu_profile", "clear"),
    PREVIEW("btn#cwu_profile", "preview"),
    SUBMIT("btn#cwu_profile", "submit"),

    RESUME("btn#cwu_profile", "resume");


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
