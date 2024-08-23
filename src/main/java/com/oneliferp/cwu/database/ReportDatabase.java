package com.oneliferp.cwu.database;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oneliferp.cwu.commands.modules.report.models.ReportModel;
import com.oneliferp.cwu.commands.modules.report.misc.ReportType;
import com.oneliferp.cwu.commands.modules.session.models.SessionModel;

import java.util.Comparator;
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
    public void addOne(final ReportModel report) {
        System.out.printf("TYPE: '%s', IDENTITY: '%s'%n", report.getType().name(), report.getEmployee());
        this.map.put(report.getId(), report);
    }

    /* Utils */
    public List<ReportModel> getReportsByType(final ReportType type) {
        return this.map.values().stream()
                .filter(o -> o.getType() == type)
                .toList();
    }

    public List<ReportModel> getReportsByCwu(final String cid) {
        return this.map.values().stream()
                .filter(o -> o.getEmployeeCid().equals(cid))
                .toList();
    }

    public List<ReportModel> getLatests(final int count) {
        return this.map.values().stream()
                .sorted(Comparator.comparing(ReportModel::getCreatedAt, Comparator.naturalOrder()))
                .limit(count)
                .toList();
    }
}
