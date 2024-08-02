package com.oneliferp.cwu.modules.report.misc;

import com.oneliferp.cwu.misc.EventTypeData;

import java.util.Map;

public enum ReportButtonType {
    START("btn#cwu_report", "start"),
    CANCEL("btn#cwu_report", "cancel"),
    RESUME("btn#cwu_report", "resume"),
    EDIT("btn#cwu_report", "edit"),
    SUBMIT("btn#cwu_report", "submit"),
    CLEAR("btn#cwu_report", "clear"),
    FILL("btn#cwu_report", "fill"),
    PREVIEW("btn#cwu_report", "preview"),
    PAGE_NEXT("btn#cwu_report", "page/next"),
    PAGE_PREV("btn#cwu_report", "page/prev"),
    REPLACE("btn#cwu_report", "replace");

    public final String root;
    public final String action;

    ReportButtonType(final String root, final String action) {
        this.root = root;
        this.action = action;
    }

    public String build(final String cid) {
        return EventTypeData.buildPattern(this.root, this.action, cid);
    }

    public String build(final String cid, final Map<String, String> params) {
        return EventTypeData.buildPatternWithArgs(this.root, this.action, cid, params);
    }
}
