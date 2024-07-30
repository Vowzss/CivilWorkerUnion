package com.oneliferp.cwu.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IdentityModel {
    @JsonProperty("firstname")
    public String firstName;

    @JsonProperty("lastname")
    public String lastName;

    @JsonProperty("cid")
    public String cid;

    public IdentityModel() {}

    public IdentityModel(final String firstName, final String lastName, final String cid) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.cid = cid;
    }

    @Override
    public String toString() {
        if (this.lastName == null) return String.format("%s, %s", firstName, cid);
        return String.format("%s %s, #%s", this.firstName, this.lastName, this.cid);
    }
}
