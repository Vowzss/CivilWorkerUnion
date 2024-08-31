package com.oneliferp.cwu.commands.modules.panel.misc.actions;

import com.oneliferp.cwu.misc.IActionType;

public enum PanelModalType implements IActionType {
    PERIOD("mdl#cwu_panel", "period");

    private final String root;
    private final String action;

    PanelModalType(final String root, final String action) {
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
