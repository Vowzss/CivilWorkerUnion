package com.oneliferp.cwu.commands.session.misc.ids;

import com.oneliferp.cwu.misc.IButtonType;

public enum SessionModalType implements IButtonType {
    FILL_PARTICIPANTS("mdl#cwu_session", "fill/participants"),
    FILL_INFO("mdl#cwu_session", "fill/info"),
    FILL_EARNINGS("mdl#cwu_session", "fill/earnings");

    public final String root;
    public final String action;

    SessionModalType(final String root, final String action) {
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
