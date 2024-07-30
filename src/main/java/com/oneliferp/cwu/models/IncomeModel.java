package com.oneliferp.cwu.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IncomeModel {
    @JsonProperty("wages")
    private int wages = 0;

    @JsonProperty("deposit")
    private int deposit = 0;

    @JsonProperty("earnings")
    private Integer earnings = null;

    /*
    Setters
    */
    public void setEarnings(final Integer earnings) {
        this.earnings = earnings == 0 ? null : earnings;
    }

    public void setWages(final int wages) {
        this.wages = wages;
    }

    public void setDeposit(final int deposit) {
        this.deposit = deposit;
    }

    /*
    Getters
    */
    public Integer getEarnings() {
        return this.earnings;
    }

    public int getWages() {
        return this.wages;
    }

    public int getDeposit() {
        return this.deposit;
    }
}
