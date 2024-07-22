package com.oneliferp.cwu.misc.form;

import io.github.stepio.jgforms.question.MetaData;

public enum ReportForm implements MetaData {
    CWU(992161960L),
    LOYALISTS(955404284L),
    CITIZENS(1313625207L),
    VORTIGAUNTS(134173125L),
    CITIZEN_COUNT(444381484L),
    ANTI_CITIZENS(1010394718L),
    ANTI_CITIZEN_COUNT(1838931787L),
    SESSION_TYPE(1422120392L),
    ZONE_TYPE(1167872433L),
    ADDITIONAL_INFO(453038439L),
    EARNINGS(119315580L);

    private final long id;

    ReportForm(final long id) {
        this.id = id;
    }

    @Override
    public long getId() {
        return this.id;
    }
}
