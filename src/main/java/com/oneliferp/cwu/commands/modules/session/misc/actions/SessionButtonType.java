package com.oneliferp.cwu.commands.modules.session.misc.actions;

import com.oneliferp.cwu.misc.IActionType;

public enum SessionButtonType implements IActionType {
    OVERVIEW("btn#cwu_manage", "session/overview"),
    HISTORY("btn#cwu_manage", "session/history"),
    MANAGE("btn#cwu_manage", "session/manage"),
    PAGE("btn#cwu_manage", "session/page"),

    DELETE("btn#cwu_session", "delete"),
    DELETE_CONFIRM("btn#cwu_session", "delete/confirm"),
    DELETE_CANCEL("btn#cwu_session", "delete/cancel"),

    BEGIN("btn#cwu_session", "begin"),
    ABORT("btn#cwu_session", "abort"),
    RESUME("btn#cwu_session", "resume"),
    OVERWRITE("btn#cwu_session", "overwrite"),
    EDIT("btn#cwu_session", "edit"),
    SUBMIT("btn#cwu_session", "submit"),
    CLEAR("btn#cwu_session", "clear"),
    FILL("btn#cwu_session", "fill"),
    PREVIEW("btn#cwu_session", "preview"),
    PAGE_NEXT("btn#cwu_session", "page/next"),
    PAGE_PREV("btn#cwu_session", "page/prev");

    private final String root;
    private final String action;

    SessionButtonType(final String root, final String action) {
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
