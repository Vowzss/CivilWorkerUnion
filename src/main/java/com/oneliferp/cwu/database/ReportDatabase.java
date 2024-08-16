package com.oneliferp.cwu.database;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oneliferp.cwu.models.CwuModel;
import com.oneliferp.cwu.commands.report.models.ReportModel;
import com.oneliferp.cwu.commands.report.misc.ReportType;

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
        this.readFromCache().forEach(report -> map.put(report.getManagerCid(), report));
    }

    @Override
    public void addOne(final ReportModel value) {
        this.map.put(value.getManagerCid(), value);
    }

    /* Utils */
    public List<ReportModel> getReportsByType(final ReportType type) {
        return this.map.values().stream()
                .filter(o -> o.getType() == type)
                .toList();
    }

    public List<ReportModel> getReportsByCwu(final String cid) {
        return this.map.values().stream()
                .filter(o -> o.getManagerCid().equals(cid))
                .toList();
    }
}
