package com.oneliferp.cwu.modules.report.misc;

import java.util.HashMap;
import java.util.Map;

public enum ReportButtonType {
    START("cwu_start_report"),
    CANCEL("cwu_cancel_report"),
    SUBMIT("cwu_submit_report"),
    EDIT("cwu_edit_report"),
    PREVIEW("cwu_preview_report"),
    CLEAR("cwu_clear_report"),
    PAGE("cwu_page_report");

    /*
    Perform easy lookup
    */
    private static final Map<String, ReportButtonType> IDS = new HashMap<>();
    static {
        for (final var type : values()) {
            IDS.put(type.getId(), type);
        }
    }
    public static ReportButtonType fromId(final String id) {
        final var type = IDS.get(id);
        if (type == null) throw new IllegalArgumentException("No enum constant with id " + id);
        return type;
    }

    private final String id;

    ReportButtonType(final String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}
