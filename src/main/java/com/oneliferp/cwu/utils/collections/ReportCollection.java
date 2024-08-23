package com.oneliferp.cwu.utils.collections;

import com.oneliferp.cwu.commands.modules.report.models.ReportModel;

import java.util.List;

public class ReportCollection {
    public static int resolveTotalEarnings(final List<ReportModel> reports) {
        return reports.stream()
                .mapToInt(s -> s.getTokens())
                .sum();
    }
}
