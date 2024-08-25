package com.oneliferp.cwu.commands.modules.manage.misc.actions;

import com.oneliferp.cwu.misc.IActionType;

public enum WorkforceButtonType implements IActionType {
    STATS("btn#cwu_manage", "workforce/stats"),
    OVERVIEW("btn#cwu_manage", "workforce/overview"),
    MANAGE("btn#cwu_manage", "workforce/manage"),

    EDIT("btn#cwu_manage", "workforce/profile/edit"),

    CREATE("btn#cwu_manage", "workforce/employee/create"),
    OVERWRITE("btn#cwu_manage", "workforce/profile/overwrite"),

    START("", "profile/start"),
    ABORT("", "profile/abort"),
    PAGE_NEXT("", "profile/next"),
    PAGE_PREV("", "profile/prev"),
    FILL("", "profile/fill"),
    CLEAR("", "profile/clear"),
    PREVIEW("", "profile/preview"),
    SUBMIT("", "profile/submit"),

    RESUME("", "profile/resume");

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
