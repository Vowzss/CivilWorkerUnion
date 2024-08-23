package com.oneliferp.cwu.commands.modules.manage.misc.actions;

import com.oneliferp.cwu.misc.IActionType;

public enum ManageMenuType implements IActionType {
    SELECT_BRANCH("mnu#cwu_manage", "select/branch"),
    SELECT_RANK("mnu#cwu_manage", "select/rank");

    public final String root;
    public final String action;

    ManageMenuType(final String root, final String action) {
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
