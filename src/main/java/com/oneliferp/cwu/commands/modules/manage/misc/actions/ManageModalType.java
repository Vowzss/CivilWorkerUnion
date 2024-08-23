package com.oneliferp.cwu.commands.modules.manage.misc.actions;

import com.oneliferp.cwu.misc.IActionType;

public enum ManageModalType implements IActionType {
    FILL_IDENTITY("mdl#cwu_manage", "fill/identity"),
    FILL_ID("mdl#cwu_manage", "fill/id"),
    FILL_JOINED_AT("mdl#cwu_manage", "fill/joined_at"),

    EMPLOYEE_SEARCH("mdl#cwu_manage", "employee/search"),
    SESSION_SEARCH("mdl#cwu_manage", "session/search"),
    REPORT_SEARCH("mdl#cwu_manage", "report/search");

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
