package com.oneliferp.cwu.modules.report.misc;

import com.oneliferp.cwu.misc.CwuBranch;

import java.util.Arrays;
import java.util.List;

public enum ReportType {
    UNKNOWN("...", null),
    STOCK("Rachat de stock", CwuBranch.values()),
    HOUSING_ATTRIBUTION("Attribution d'un logement", CwuBranch.DTL),
    HOUSING_PAYMENT("Paiement d'un loyer", CwuBranch.DTL),
    HOUSING_EVICTION("Expulsion d'un locataire", CwuBranch.DTL),
    HOUSING_VERIFICATION("Vérification de propreté", CwuBranch.DTL),
    MEDICAL_CHECK("Bilan de santé", CwuBranch.DMS),
    OTHER("Autre", CwuBranch.values());

    private final String label;
    private final List<CwuBranch> branches;

    ReportType(final String label, final CwuBranch... branches) {
        this.label = label;
        this.branches = branches == null ? null : Arrays.stream(branches).toList();
    }

    /* Getters */
    public String getLabel() {
        return this.label;
    }

    /* Methods */
    public String getBranchEmoji() {
        return this.branches.size() == 1 ? branches.get(0).getEmoji() : "\uD83D\uDCDD";
    }

    /* Utils */
    public static List<ReportType> getAvailableReportTypes(final CwuBranch branch) {
        return Arrays.stream(ReportType.values()).skip(1)
                .filter(type -> type.branches.contains(branch))
                .toList();
    }
}
