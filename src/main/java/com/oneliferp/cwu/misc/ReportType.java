package com.oneliferp.cwu.misc;

import java.util.Arrays;
import java.util.List;

public enum ReportType {
    STOCK("Rachat de stock", CwuBranch.values()),
    HOUSING_ATTRIBUTION("Attribution d'un logement", CwuBranch.DTL),
    HOUSING_PAYMENT("Paiement d'un logement", CwuBranch.DTL),
    HOUSING_EVICTION("Expulsion d'un logement", CwuBranch.DTL),
    OTHER("Autre", CwuBranch.values());

    private final String label;
    private final List<CwuBranch> branches;

    ReportType(final String label, final CwuBranch... branches) {
        this.label = label;
        this.branches = Arrays.stream(branches).toList();
    }

    public String getLabel() {
        return this.label;
    }

    public List<CwuBranch> getBranches() {
        return this.branches;
    }

    /*
    Utils
    */
    public List<ReportType> getAvailableReportTypes(final CwuBranch branch) {
        return Arrays.stream(ReportType.values()).filter(type -> type.branches.contains(branch)).toList();
    }

    public String getBranchEmoji() {
        return this.branches.size() == 1 ? branches.get(0).getEmoji() : "\uD83D\uDCDD";
    }

    public String getBranchLabel() {
        return this.branches.size() == 1 ? "CWU" : this.branches.get(0).name();
    }
}
