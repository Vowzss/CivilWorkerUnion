package com.oneliferp.cwu.commands.manage.misc.actions;

import com.oneliferp.cwu.misc.IActionType;

public enum ManageButtonType implements IActionType {
    PROFILE_VIEW("btn#cwu_manage", "profile/view"),
    PROFILE_DELETE("btn#cwu_manage", "profile/delete"),
    PROFILE_CREATE("btn#cwu_manage", "profile/create"),
    PROFILE_START("btn#cwu_manage", "profile/start"),
    PROFILE_ABORT("btn#cwu_manage", "profile/abort"),
    PROFILE_NEXT("btn#cwu_manage", "profile/next"),
    PROFILE_PREV("btn#cwu_manage", "profile/prev"),
    PROFILE_FILL("btn#cwu_manage", "profile/fill"),
    PROFILE_EDIT("btn#cwu_manage", "profile/edit"),
    PROFILE_UPDATE("btn#cwu_manage", "profile/update"),
    PROFILE_CLEAR("btn#cwu_manage", "profile/clear"),
    PROFILE_PREVIEW("btn#cwu_manage", "profile/preview"),
    PROFILE_SUBMIT("btn#cwu_manage", "profile/submit"),
    PROFILE_OVERWRITE("btn#cwu_manage", "profile/overwrite"),
    PROFILE_RESUME("btn#cwu_manage", "profile/resume"),

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
