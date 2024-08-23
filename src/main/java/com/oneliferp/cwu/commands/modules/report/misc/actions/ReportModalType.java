package com.oneliferp.cwu.commands.modules.report.misc.actions;

import com.oneliferp.cwu.misc.IActionType;

public enum ReportModalType implements IActionType {
    FILL_TENANT("mdl#cwu_report", "fill/tenent"),
    FILL_PATIENT("mdl#cwu_report", "fill/patient"),
    FILL_TAX("mdl#cwu_report", "fill/tax"),
    FILL_TOKENS("mdl#cwu_report", "fill/tokens"),
    FILL_HEALTHINESS("mdl#cwu_report", "fill/healthiness"),
    FILL_INFO("mdl#cwu_report", "fill/info");

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
