package com.oneliferp.cwu.modules.session.misc;

import com.oneliferp.cwu.misc.EventTypeData;

public enum SessionMenuType {
    FILL_ZONE("mnu#cwu_session", "fill/zone");

    public final String root;
    public final String action;

    SessionMenuType(final String root, final String action) {
        this.root = root;
        this.action = action;
    }

    public String build(final String cid) {
        return EventTypeData.buildPattern(this.root, this.action, cid);
    }
}
