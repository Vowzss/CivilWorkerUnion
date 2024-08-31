package com.oneliferp.cwu.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.exceptions.IdentityMalformedException;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.misc.CwuRank;

import java.util.List;

public class CwuIdentityModel extends IdentityModel {
    @JsonProperty("rank")
    private CwuRank rank;

    @JsonProperty("branch")
    private CwuBranch branch;

    private CwuIdentityModel() {
        super();
    }

    public CwuIdentityModel(final String firstName, final String lastName, final String cid) {
        super(firstName, lastName, cid);
    }

    /* Helpers */
    public static CwuIdentityModel parseCwuIdentity(final String str) throws IdentityMalformedException {
        final var parts = IdentityModel.parseIdentity(str);
        return new CwuIdentityModel(parts.getValue0(), parts.getValue1(), parts.getValue2());
    }

    public static List<CwuIdentityModel> parseCwuIdentities(final String str) throws IdentityMalformedException {
        final var parts = IdentityModel.parseIdentities(str);
        return parts.stream().map(p -> new CwuIdentityModel(p.getValue0(), p.getValue1(), p.getValue2())).toList();
    }

    public CwuRank getRank() {
        return this.rank;
    }

    public void setRank(final CwuRank rank) {
        this.rank = rank;
    }

    public CwuBranch getBranch() {
        return this.branch;
    }

    public void setBranch(final CwuBranch branch) {
        this.branch = branch;
    }
}
