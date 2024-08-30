package com.oneliferp.cwu.commands.modules.report.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oneliferp.cwu.commands.modules.profile.models.ProfileModel;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.models.CitizenIdentityModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CwuReportModel extends ReportModel {
    public CwuReportModel(final ProfileModel profile) {
        super(profile);
    }

    @Override
    public void setIdentity(final CitizenIdentityModel identity) {
    }
    @Override
    public CitizenIdentityModel getIdentity() {
        return null;
    }

    /* Utils */
    public boolean isRecastAvailable() {
        return this.type.hasMainBranch();
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

        report.id = this.id;
        report.employee = this.employee;
        report.createdAt = this.createdAt;
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
}
