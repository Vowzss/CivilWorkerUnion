package com.oneliferp.cwu.modules.report.misc;

import com.oneliferp.cwu.misc.IButtonType;

public enum ReportButtonType implements IButtonType {
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
