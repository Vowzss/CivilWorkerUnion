package com.oneliferp.cwu.commands.modules.report.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.commands.modules.profile.models.ProfileModel;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.models.IdentityModel;

public class DmsReportModel extends ReportModel {
    @JsonProperty("tax")
    private Integer tax;

    @JsonProperty("patient")
    private IdentityModel patient;

    @JsonProperty("medical")
    private String medical;

    private DmsReportModel() {
        super();

        this.branch = CwuBranch.DMS;
    }

    public DmsReportModel(final ProfileModel employee) {
        super(employee);
    }

    /* Getter & Setter */
    @Override
    public void setIdentity(IdentityModel identity) {
        this.patient = identity;
    }

    @Override
    public IdentityModel getIdentity() {
        return this.patient;
    }

    @Override
    public void setTax(final Integer tax) {
        this.tax = tax;
    }

    @Override
    public Integer getTax() {
        return this.tax;
    }

    @Override
    public void setMedical(final String medical) {
        this.medical = medical;
    }

    @Override
    public String getMedical() {
        return this.medical;
    }

    /* Methods */
    @Override
    public void reset() {
        super.reset();

        this.patient = null;
        this.tax = null;
    }
}
