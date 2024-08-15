package com.oneliferp.cwu.modules.report.misc.ids;

import com.oneliferp.cwu.misc.IButtonType;

public enum ReportModalType implements IButtonType {
    FILL_INFO("mdl#cwu_report", "fill/info"),
    FILL_IDENTITY("mdl#cwu_report", "fill/identity"),
    FILL_TOKENS("mdl#cwu_report", "fill/tokens");

    public final String root;
    public final String action;

    ReportModalType(final String root, final String action) {
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
