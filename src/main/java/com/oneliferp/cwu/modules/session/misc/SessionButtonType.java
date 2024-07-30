package com.oneliferp.cwu.modules.session.misc;

public enum SessionButtonType {
    START("btn$cwu_session", "start"),
    CANCEL("btn$cwu_session", "cancel"),
    RESUME("btn$cwu_session", "resume"),
    EDIT("btn$cwu_session", "edit"),
    SUBMIT("btn$cwu_session", "submit"),
    CLEAR("btn$cwu_session", "clear"),
    FILL("btn$cwu_session", "fill"),
    PREVIEW("btn$cwu_session", "preview"),
    PAGE_NEXT("btn$cwu_session", "page/next"),
    PAGE_PREV("btn$cwu_session", "page/prev");

    public final String root;
    public final String action;

    SessionButtonType(final String root, final String action) {
        this.root = root;
        this.action = action;
    }

    public String build(final String id) {
        return String.format("%s.%s:%s", this.root, this.action, id);
    }
}
