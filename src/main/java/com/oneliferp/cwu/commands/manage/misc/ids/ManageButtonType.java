package com.oneliferp.cwu.commands.manage.misc.ids;

import com.oneliferp.cwu.misc.IActionType;

public enum ManageButtonType implements IActionType {
    PROFILE_VIEW("btn#cwu_manage", "profile/view"),
    PROFILE_DELETE("btn#cwu_manage", "profile/delete"),
    PROFILE_EDIT("btn#cwu_manage", "profile/edit"),
    PROFILE_CREATE("btn#cwu_manage", "profile/create"),

    SESSION_VIEW("btn#cwu_manage", "session/view"),
    SESSION_DELETE("btn#cwu_manage", "session/delete"),

    REPORT_VIEW("btn#cwu_manage", "report/view"),
    REPORT_DELETE("btn#cwu_manage", "report/delete");

    public final String root;
    public final String action;

    ManageButtonType(final String root, final String action) {
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
