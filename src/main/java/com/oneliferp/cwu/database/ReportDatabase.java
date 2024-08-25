package com.oneliferp.cwu.database;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oneliferp.cwu.commands.modules.report.models.ReportModel;
import com.oneliferp.cwu.commands.modules.report.misc.ReportType;

import java.util.Collection;
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
    public List<ReportModel> resolveFromWeek(final int limit) {
        return this.map.values().stream().filter(ReportModel::isWithinWeek)
                .limit(limit)
                .toList();
    }

    public List<ReportModel> resolveFromWeek() {
        return this.map.values().stream().filter(ReportModel::isWithinWeek)
                .toList();
    }

    public List<ReportModel> resolveByType(final ReportType type) {
        return this.map.values().stream()
                .filter(o -> o.getType() == type)
                .toList();
    }

    public List<ReportModel> resolveByEmployee(final String cid) {
        return this.map.values().stream()
                .filter(o -> o.getEmployeeCid().equals(cid))
                .toList();
    }

    public ReportModel resolveLatest() {
        if (this.map.values().isEmpty()) return null;

        return this.map.values().stream()
                .sorted(Comparator.comparing(ReportModel::getCreatedAt, Comparator.reverseOrder()))
                .findFirst().get();
    }

    /* Helpers */
    public static int resolveEarnings(final Collection<ReportModel> collection) {
        if (collection.isEmpty()) return 0;
        return collection.stream()
                .filter(c -> c.getType() == ReportType.BUSINESS_ORDER || c.getType() == ReportType.MEDICAL_ORDER)
                .mapToInt(r ->  (int) Math.floor(r.getTax() * r.getEmployee().rank.getBranchRoyalty()))
                .sum();
    }

    public static int resolvePoints(final Collection<ReportModel> collection) {
        if (collection == null) return 0;
        return collection.size() / 2;
    }

}
