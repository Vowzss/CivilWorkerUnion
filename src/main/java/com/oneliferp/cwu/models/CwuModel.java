package com.oneliferp.cwu.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.database.ReportDatabase;
import com.oneliferp.cwu.database.SessionDatabase;
import com.oneliferp.cwu.exceptions.IdentityMalformedException;
import com.oneliferp.cwu.commands.report.models.ReportModel;
import com.oneliferp.cwu.commands.session.models.SessionModel;
import com.oneliferp.cwu.utils.RegexUtils;
import com.oneliferp.cwu.utils.SimpleDate;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.misc.CwuRank;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Comparator;

public class CwuModel {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("identity")
    private IdentityModel identity;

    @JsonProperty("branch")
    private CwuBranch branch;

    @JsonProperty("rank")
    private CwuRank rank;

    @JsonProperty("joinedAt")
    private SimpleDate joinedAt;

    private CwuModel() {}

    public CwuModel(final long id, final IdentityModel identity, final CwuBranch branch, final CwuRank rank) {
        this.id = id;
        this.identity = identity;
        this.branch = branch;
        this.rank = rank;
        this.joinedAt = SimpleDate.now();
    }

    public static CwuModel fromInput(final SlashCommandInteractionEvent event) throws IdentityMalformedException {
        final IdentityModel identity = RegexUtils.parseIdentity(event.getOption("identity").getAsString());

        final String branch = event.getOption("branch").getAsString();
        final String rank = event.getOption("rank").getAsString();

        return new CwuModel(event.getUser().getIdLong(), identity, CwuBranch.valueOf(branch), CwuRank.valueOf(rank));
    }

    /* Getters */
    public Long getId() {
        return this.id;
    }

    public String getCid() {
        return this.identity.cid;
    }

    public IdentityModel getIdentity() {
        return this.identity;
    }

    public SimpleDate getJoinedAt() {
        return this.joinedAt;
    }

    public CwuBranch getBranch() {
        return this.branch;
    }

    public CwuRank getRank() {
        return this.rank;
    }

    /* Utils */
    public SessionModel getLatestSession() {
        return SessionDatabase.get().getSessionsByCwu(this.getCid()).stream()
                .filter(session -> session.getPeriod().getStartedAt() != null)
                .max(Comparator.comparing(session -> session.getPeriod().getStartedAt()))
                .orElse(null);
    }

    public int getSessionCount() {
        return SessionDatabase.get().getSessionsByCwu(this.getCid()).size();
    }

    public int getWeeklySessionCount() {
        return SessionDatabase.get().getSessionsByCwu(this.getCid())
                .stream().filter(SessionModel::isWithinWeek)
                .toList().size();
    }

    public ReportModel getLatestReport() {
        return ReportDatabase.get().getReportsByCwu(this.getCid()).stream()
                .max(Comparator.comparing(ReportModel::getCreatedAt))
                .orElse(null);
    }

    public int getReportCount() {
        return ReportDatabase.get().getReportsByCwu(this.getCid()).size();
    }

    public int getWeeklyReportCount() {
        return ReportDatabase.get().getReportsByCwu(this.getCid())
                .stream().filter(ReportModel::isWithinWeek)
                .toList().size();
    }
}

