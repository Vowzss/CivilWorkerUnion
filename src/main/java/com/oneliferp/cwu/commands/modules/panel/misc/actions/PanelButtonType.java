package com.oneliferp.cwu.commands.modules.panel.misc.actions;

import com.oneliferp.cwu.misc.IActionType;

public enum PanelButtonType implements IActionType {
    RESET("btn#cwu_panel", "reset");

    private final String root;
    private final String action;

    PanelButtonType(final String root, final String action) {
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
