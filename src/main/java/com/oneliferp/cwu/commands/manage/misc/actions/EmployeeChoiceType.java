package com.oneliferp.cwu.commands.manage.misc.actions;

import com.oneliferp.cwu.misc.IActionType;

public enum EmployeeChoiceType implements IActionType {
    VIEW("", "profile/view"),
    DELETE("", "profile/delete"),
    CREATE("", "profile/create"),
    START("", "profile/start"),
    ABORT("", "profile/abort"),
    PAGE_NEXT("", "profile/next"),
    PAGE_PREV("", "profile/prev"),
    FILL("", "profile/fill"),
    UPDATE("", "profile/update"),
    EDIT("", "profile/edit"),
    CLEAR("", "profile/clear"),
    PREVIEW("", "profile/preview"),
    SUBMIT("", "profile/submit"),
    OVERWRITE("", "profile/overwrite"),
    RESUME("", "profile/resume");

    public final String root;
    public final String action;

    EmployeeChoiceType(final String root, final String action) {
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
