package com.oneliferp.cwu.modules.session.misc;

import com.oneliferp.cwu.misc.IButtonType;

public enum SessionButtonType implements IButtonType {
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
