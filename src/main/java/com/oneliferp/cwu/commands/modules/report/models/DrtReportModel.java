package com.oneliferp.cwu.commands.modules.report.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.commands.modules.profile.models.ProfileModel;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.models.IdentityModel;

public class DrtReportModel extends ReportModel {
    @JsonProperty("merchant")
    protected IdentityModel merchant;

    @JsonProperty("tax")
    private Integer tax;

    @JsonProperty("rent")
    private Integer rent;

    @JsonProperty("cost")
    private Integer cost;

    @JsonProperty("healthiness")
    private String healthiness;

    DrtReportModel() {
        super(CwuBranch.DRT);
    }

    public DrtReportModel(final ProfileModel profile) {
        super(profile);
    }

    /* Getter & Setter */
    @Override
    public void setIdentity(final IdentityModel identity) {
        this.merchant = identity;
    }
    @Override
    public IdentityModel getIdentity() {
        return this.merchant;
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
    public void setRent(final Integer rent) {
        this.rent = rent;
    }
    @Override
    public Integer getRent() {
        return this.rent;
    }

    @Override
    public void setCost(final Integer cost) {
        this.cost = cost;
    }
    @Override
    public Integer getCost() {
        return this.cost;
    }

    @Override
    public void setHealthiness(final String healthiness) {
        this.healthiness = healthiness;
    }
    @Override
    public String getHealthiness() {
        return this.healthiness;
    }

    /* Pageable implementation */
    @Override
    public void reset() {
        super.reset();

        this.merchant = null;
        this.tax = null;
        this.rent = null;
        this.cost = null;
        this.healthiness = null;
    }

    @Override
    public boolean verify() {
        return switch (this.type) {
            default -> throw new IllegalStateException("Unexpected value: " + this.type);
            case BUSINESS_ATTRIBUTION, BUSINESS_PAYMENT -> super.verify() && this.merchant != null && this.rent != null;
            case BUSINESS_ORDER -> super.verify() && this.merchant != null && this.tax != null && this.cost != null;
            case BUSINESS_CLEANING -> super.verify() && this.healthiness != null;
        };
    }
}
