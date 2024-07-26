package com.oneliferp.cwu.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oneliferp.cwu.Utils.RegexUtils;
import com.oneliferp.cwu.exceptions.IdentityMalformedException;

public class IdentityModel {
    @Expose
    @SerializedName("firstname")
    public String firstName;

    @Expose
    @SerializedName("lastname")
    public String lastName;

    @Expose
    @SerializedName("cid")
    public String cid;

    public IdentityModel() {

    }

    public IdentityModel(final String identity) throws IdentityMalformedException {
        final String[] parts = RegexUtils.parseIdentity(identity);

        this.firstName = parts[0];
        this.lastName = parts[1];
        this.cid = parts[2];
    }

    @Override
    public String toString() {
        return String.format("%s %s, #%s", this.firstName, this.lastName, this.cid);
    }
}
