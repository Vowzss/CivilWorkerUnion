package com.oneliferp.cwu.commands.modules.manage.misc.actions;

import com.oneliferp.cwu.misc.IActionType;

public enum SessionChoiceType implements IActionType {
    SUMMARY("", "session/summary"),
    STATS("", "session/stats"),
    MANAGE("", "session/manage");

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