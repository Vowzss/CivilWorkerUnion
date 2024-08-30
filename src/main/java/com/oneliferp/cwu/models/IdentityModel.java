package com.oneliferp.cwu.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.exceptions.IdentityMalformedException;
import com.oneliferp.cwu.utils.RegexUtils;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;

public abstract class IdentityModel {
    @JsonProperty("firstname")
    protected String firstName;

    @JsonProperty("lastname")
    protected String lastName;

    @JsonProperty("cid")
    protected String cid;

    protected IdentityModel() {}

    public IdentityModel(final String firstName, final String lastName, final String cid) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.cid = cid;
    }

    /* Getters & Setters */

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getCid() {
        return this.cid;
    }


    /* Object */
    @Override
    public String toString() {
        if (this.lastName == null || this.lastName.isEmpty()) return String.format("%s, #%s", firstName, cid);
        return String.format("%s %s, #%s", this.firstName, this.lastName, this.cid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CwuIdentityModel that = (CwuIdentityModel) o;
        return Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(cid, that.cid);
    }

    @Override
    public int hashCode() {
        if (this.lastName == null) return Objects.hash(firstName, cid);
        return Objects.hash(firstName, lastName, cid);
    }

    /* Helpers */
    protected static Triplet<String, String, String> parseIdentity(final String str) throws IdentityMalformedException {
        final Matcher matcher = RegexUtils.APPLY_PATTERN.matcher(str);
        if (!matcher.find()) throw new IdentityMalformedException();

        final String[] names = matcher.group(1).split(" ");
        return Triplet.with(names[0], names[1], matcher.group(2));
    }

    protected static List<Triplet<String, String, String>> parseIdentities(final String str) throws IdentityMalformedException {
        final List<Triplet<String, String, String>> list = new ArrayList<>();
        final Matcher matcher = RegexUtils.APPLY_PATTERN.matcher(str);

        matcher.results().forEach(rs -> {
            final String[] names = matcher.group(1).split(" ");

            // This parameter can be optional depending on the case
            final String lastName = names.length > 1 ? (names[1] != null ? names[1] : "") : "";
            list.add(Triplet.with(names[0], lastName, matcher.group(2)));
        });

        if (list.isEmpty()) throw new IdentityMalformedException();
        return list;
    }

}
