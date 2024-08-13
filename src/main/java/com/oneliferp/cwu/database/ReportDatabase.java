package com.oneliferp.cwu.database;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oneliferp.cwu.models.CwuModel;
import com.oneliferp.cwu.models.ReportModel;
import com.oneliferp.cwu.modules.report.misc.ReportType;

import java.util.List;

public class ReportDatabase extends JsonDatabase<String, ReportModel> {
    /* Singleton */
    private static ReportDatabase instance;
    public static ReportDatabase get() {
        if (instance == null) instance = new ReportDatabase();
        return instance;
    }

    protected ReportDatabase() {
        super(new TypeReference<>() {
        }, "report_db.json");
    }

    @Override
    public void addOne(final ReportModel value) {
        this.map.put(value.getManagerCid(), value);
    }

    /* Utils */
    public List<ReportModel> getSessionsByType(final ReportType type) {
        return this.map.values().stream()
                .filter(o -> o.getType() == type)
                .toList();
    }

    public List<ReportModel> getSessionsByCwu(final CwuModel cwu) {
        return this.map.values().stream()
                .filter(o -> o.getManagerCid().equals(cwu.getCid()))
                .toList();
    }
}
