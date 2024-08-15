package com.oneliferp.cwu.modules.report.misc.ids;

import com.oneliferp.cwu.misc.ICommandType;

public enum ReportCommandType implements ICommandType {
    BASE("rapport", "Vous permet de créer un rapport.");

    private final String name;
    private final String description;

    ReportCommandType(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
