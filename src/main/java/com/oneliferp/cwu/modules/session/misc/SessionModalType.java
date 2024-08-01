package com.oneliferp.cwu.modules.session.misc;

import com.oneliferp.cwu.misc.EventTypeData;

public enum SessionModalType {
    FILL_PARTICIPANTS("mdl#cwu_session", "fill/participants"),
    FILL_INFO("mdl#cwu_session", "fill/info"),
    FILL_EARNINGS("mdl#cwu_session", "fill/earnings");

    public final String root;
    public final String action;

    SessionModalType(final String root, final String action) {
        this.root = root;
        this.action = action;
    }

    public String build(final String cid) {
        return EventTypeData.buildPattern(this.root, this.action, cid);
    }
}
