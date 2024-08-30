package com.oneliferp.cwu.commands.modules.manage.misc.actions;

import com.oneliferp.cwu.misc.IActionType;

public enum WorkforceButtonType implements IActionType {
    STATS("btn#cwu_manage", "workforce/stats"),
    OVERVIEW("btn#cwu_manage", "workforce/overview"),
    MANAGE("btn#cwu_manage", "workforce/manage");

    public final String root;
    public final String action;

    WorkforceButtonType(final String root, final String action) {
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
