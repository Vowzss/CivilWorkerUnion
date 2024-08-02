package com.oneliferp.cwu.database;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oneliferp.cwu.models.ReportModel;

public class ReportDatabase extends JsonDatabase<String, ReportModel> {
    /* Singleton */
    private static ReportDatabase instance;
    public static ReportDatabase get() {
        if (instance == null) instance = new ReportDatabase();
        return instance;
    }

    protected ReportDatabase() {
        super(new TypeReference<>() {
        }, "cwu_report.json");
    }

    @Override
    public void addOne(final ReportModel value) {
        this.map.put(value.getManagerCid(), value);
    }
}
