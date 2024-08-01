package com.oneliferp.cwu.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final IdentityModel that = (IdentityModel) o;
        return Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(cid, that.cid);
    }

    @Override
    public int hashCode() {
        if (this.lastName == null) return Objects.hash(firstName, cid);
        return Objects.hash(firstName, lastName, cid);
    }
}
