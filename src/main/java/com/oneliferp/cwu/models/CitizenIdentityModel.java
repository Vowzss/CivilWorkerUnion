package com.oneliferp.cwu.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.commands.modules.session.misc.CitizenType;
import com.oneliferp.cwu.exceptions.IdentityMalformedException;

import java.util.List;

public class CitizenIdentityModel extends IdentityModel {
    @JsonProperty("type")
    private CitizenType type;

    private CitizenIdentityModel() {
        super();
    }

    public CitizenIdentityModel(final String firstName, final String lastName, final String cid) {
        super(firstName, lastName, cid);
    }

    /* Helpers */
    public static CitizenIdentityModel parseCitizenIdentity(final String str) throws IdentityMalformedException {
        final var parts = IdentityModel.parseIdentity(str);
        return new CitizenIdentityModel(parts.getValue0(), parts.getValue1(), parts.getValue2());
    }

    public static List<CitizenIdentityModel> parseCitizenIdentities(final String str) throws IdentityMalformedException {
        final var parts = IdentityModel.parseIdentities(str);
        return parts.stream().map(p -> new CitizenIdentityModel(p.getValue0(), p.getValue1(), p.getValue2())).toList();
    }

    public CitizenType getType() {
        return this.type;
    }

    public void setType(final CitizenType type) {
        this.type = type;
    }
}
