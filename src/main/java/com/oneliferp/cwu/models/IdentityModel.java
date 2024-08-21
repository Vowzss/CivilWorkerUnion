package com.oneliferp.cwu.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.exceptions.IdentityMalformedException;
import com.oneliferp.cwu.utils.RegexUtils;

import java.util.Objects;
import java.util.regex.Matcher;

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

    public static IdentityModel parseIdentity(final String str) throws IdentityMalformedException {
        final Matcher matcher = RegexUtils.APPLY_PATTERN.matcher(str);
        if (!matcher.find()) throw new IdentityMalformedException();

        final String[] names = matcher.group(1).split(" ");
        return new IdentityModel(names[0], names[1], matcher.group(2));
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
