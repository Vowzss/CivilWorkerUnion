package com.oneliferp.cwu.commands.modules.report.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.commands.modules.profile.models.ProfileModel;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.models.IdentityModel;

public class CwuReportModel extends ReportModel {
    @JsonProperty("identity")
    protected IdentityModel identity;

    private CwuReportModel() {
        super(CwuBranch.CWU);
    }

    public CwuReportModel(final ProfileModel profile) {
        super(profile);
    }

    @Override
    public void setIdentity(final IdentityModel identity) {
        this.identity = identity;
    }

    @Override
    public IdentityModel getIdentity() {
        return this.identity;
    }

    /* Utils */
    public boolean isRecastAvailable() {
        return this.getType().hasMainBranch();
    }

    /* Helpers */
    private ReportModel convert(final CwuBranch branch) {
        final ReportModel report;

        switch (branch) {
            default -> throw new IllegalArgumentException("Unsupported branch");
            case DTL -> report = new DtlReportModel();
            case DMS -> report = new DmsReportModel();
            case DRT -> report = new DrtReportModel();
        }

        report.employee = this.employee;
        report.branch = branch;
        report.type = this.type;

        return report;
    }

    public DmsReportModel convertToDmsReport() {
        return (DmsReportModel) this.convert(CwuBranch.DMS);
    }

    public DrtReportModel convertToDrtReport() {
        return (DrtReportModel) this.convert(CwuBranch.DRT);
    }

    public DtlReportModel convertToDtlReport() {
        return (DtlReportModel) this.convert(CwuBranch.DTL);
    }

    /* Pageable implementation */
}
