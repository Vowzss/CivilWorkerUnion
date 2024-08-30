package com.oneliferp.cwu.commands.modules.profile.misc.actions;

import com.oneliferp.cwu.misc.IActionType;

public enum ProfileModalType implements IActionType {
    FILL_IDENTITY("mdl#cwu_manage", "fill/identity"),
    FILL_ID("mdl#cwu_manage", "fill/id"),
    FILL_JOINED_AT("mdl#cwu_manage", "fill/joined_at");

    public final String root;
    public final String action;

    ProfileModalType(final String root, final String action) {
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
