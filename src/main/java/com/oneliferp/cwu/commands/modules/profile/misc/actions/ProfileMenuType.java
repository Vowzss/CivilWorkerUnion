package com.oneliferp.cwu.commands.modules.profile.misc.actions;

import com.oneliferp.cwu.misc.IActionType;

public enum ProfileMenuType implements IActionType {
    SELECT_BRANCH("mnu#cwu_profile", "select/branch"),
    SELECT_RANK("mnu#cwu_profile", "select/rank");

    public final String root;
    public final String action;

    ProfileMenuType(final String root, final String action) {
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
