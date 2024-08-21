package com.oneliferp.cwu.commands.manage.misc.actions;

import com.oneliferp.cwu.misc.IActionType;

public enum ManageModalType implements IActionType {
    SESSION_ID("mdl#cwu_gestion", "session/id"),
    REPORT_ID("mdl#cwu_gestion", "report/id"),
    PROFILE_CID("mdl#cwu_gestion", "profile/cid");

    private final String root;
    private final String action;

    ManageModalType(final String root, final String action) {
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
