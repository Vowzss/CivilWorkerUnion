package com.oneliferp.cwu.modules.report.misc;

import com.oneliferp.cwu.misc.IButtonType;

public enum ReportMenuType implements IButtonType {
    SELECT_TYPE("mnu#cwu_report", "select/type"),
    SELECT_STOCK("mnu#cwu_report", "select/stock");

    public final String root;
    public final String action;

    ReportMenuType(final String root, final String action) {
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
