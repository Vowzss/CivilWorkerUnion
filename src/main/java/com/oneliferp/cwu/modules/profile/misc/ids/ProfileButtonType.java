package com.oneliferp.cwu.modules.profile.misc.ids;

import com.oneliferp.cwu.misc.IButtonType;

public enum ProfileButtonType implements IButtonType {
    STATS("btn#cwu_profile", "stats"),
    RETURN("btn#cwu_profile", "return"),
    DELETE("btn#cwu_profile", "delete"),
    DELETE_CONFIRM("btn#cwu_profile", "delete/confirm"),
    DELETE_CANCEL("btn#cwu_profile", "delete/cancel");

    public final String root;
    public final String action;

    ProfileButtonType(final String root, final String action) {
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
