package com.oneliferp.cwu.modules.profile.misc;

public enum ProfileButtonType {
    STATS("btn$cwu_profile", "stats"),
    RETURN("btn$cwu_profile", "return"),
    DELETE("btn$cwu_profile", "delete"),
    DELETE_CONFIRM("btn$cwu_profile", "delete/confirm"),
    DELETE_CANCEL("btn$cwu_profile", "delete/cancel");

    public final String root;
    public final String action;

    ProfileButtonType(final String root, final String action) {
        this.root = root;
        this.action = action;
    }

    public String build(final String id) {
        return String.format("%s.%s:%s", this.root, this.action, id);
    }
}
