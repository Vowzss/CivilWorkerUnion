package com.oneliferp.cwu.commands.modules.session.misc.actions;

import com.oneliferp.cwu.misc.IActionType;

public enum SessionMenuType implements IActionType {
    SELECT_TYPE("mnu#cwu_session", "select/type"),
    SELECT_ZONE("mnu#cwu_session", "select/zone");

    public final String root;
    public final String action;

    SessionMenuType(final String root, final String action) {
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
