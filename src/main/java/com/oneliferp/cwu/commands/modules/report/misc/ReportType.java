package com.oneliferp.cwu.commands.modules.report.misc;

import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.utils.EmojiUtils;

import java.util.Arrays;
import java.util.List;

public enum ReportType {
    UNKNOWN("...", null),
    STOCK("Rachat de stock", CwuBranch.values()),
    BONUS("Jeton CWU", CwuBranch.values()),

    HOUSING_ATTRIBUTION("Attribution d'un logement", CwuBranch.DTL, CwuBranch.CWU),
    HOUSING_PAYMENT("Paiement d'un logement", CwuBranch.DTL, CwuBranch.CWU),
    HOUSING_EVICTION("Expulsion d'un locataire", CwuBranch.DTL, CwuBranch.CWU),
    HOUSING_CLEANING("Salubrité d'un logement", CwuBranch.DTL, CwuBranch.CWU),

    BUSINESS_ATTRIBUTION("Attribution d'un commerce", CwuBranch.DRT, CwuBranch.CWU),
    BUSINESS_PAYMENT("Paiement d'un commerce", CwuBranch.DRT, CwuBranch.CWU),
    BUSINESS_ORDER("Commande d'un commerce", CwuBranch.DRT, CwuBranch.CWU),
    BUSINESS_CLEANING("Salubrité d'un commerce", CwuBranch.DRT, CwuBranch.CWU),

    MEDICAL_INTERVENTION("Intervention médicale", CwuBranch.DMS, CwuBranch.CWU),
    MEDICAL_ORDER("Commande médicale", CwuBranch.DMS, CwuBranch.CWU),

    OTHER("Autre", CwuBranch.values());

    private final String label;
    private final List<CwuBranch> branches;

    ReportType(final String label, final CwuBranch... branches) {
        this.label = label;
        this.branches = branches == null ? null : Arrays.stream(branches).toList();
    }

    public static List<ReportType> getAvailableReportTypes(final CwuBranch branch) {
        return Arrays.stream(ReportType.values()).skip(1)
                .filter(type -> type.branches.contains(branch))
                .toList();
    }

    /* Getters */
    public String getLabel() {
        return this.label;
    }

    /* Methods */
    public boolean hasMainBranch() {
        return this.branches.size() != CwuBranch.values().length;
    }

    public CwuBranch getMainBranch() {
        return this.branches.get(0);
    }

    /* Utils */
    public String getBranchEmoji() {
        if (!hasMainBranch()) return EmojiUtils.getPencilMemo();
        return this.getMainBranch().getEmoji();
    }
}
