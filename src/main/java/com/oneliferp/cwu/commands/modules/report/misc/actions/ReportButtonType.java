package com.oneliferp.cwu.commands.modules.report.misc.actions;

import com.oneliferp.cwu.misc.IActionType;

public enum ReportButtonType implements IActionType {
    OVERVIEW("btn#cwu_manage", "report/overview"),
    HISTORY("btn#cwu_manage", "report/history"),
    MANAGE("btn#cwu_manage", "report/manage"),
    PAGE("btn#cwu_manage", "report/page"),

    DELETE("btn#cwu_report", "delete"),
    DELETE_CONFIRM("btn#cwu_report", "delete/confirm"),
    DELETE_CANCEL("btn#cwu_report", "delete/cancel"),

    BEGIN("btn#cwu_report", "begin"),
    ABORT("btn#cwu_report", "abort"),
    RESUME("btn#cwu_report", "resume"),
    OVERWRITE("btn#cwu_report", "overwrite"),
    EDIT("btn#cwu_report", "edit"),
    SUBMIT("btn#cwu_report", "submit"),
    CLEAR("btn#cwu_report", "clear"),
    FILL("btn#cwu_report", "fill"),
    PREVIEW("btn#cwu_report", "preview"),
    PAGE_NEXT("btn#cwu_report", "page/next"),
    PAGE_PREV("btn#cwu_report", "page/prev");

    private final String root;
    private final String action;

    ReportButtonType(final String root, final String action) {
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
