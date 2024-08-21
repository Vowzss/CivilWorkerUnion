package com.oneliferp.cwu.commands.manage.misc.actions;

import com.oneliferp.cwu.misc.IActionType;

public enum ReportChoiceType implements IActionType {
    VIEW("", "report/view"),
    DELETE("", "report/delete");

    public final String root;
    public final String action;

    ReportChoiceType(final String root, final String action) {
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