package com.oneliferp.cwu.commands.manage.models;

import com.oneliferp.cwu.CivilWorkerUnion;
import com.oneliferp.cwu.commands.manage.misc.EmployeePageType;
import com.oneliferp.cwu.commands.profile.models.ProfileModel;
import com.oneliferp.cwu.commands.report.misc.actions.ReportPageType;
import com.oneliferp.cwu.commands.session.misc.SessionType;
import com.oneliferp.cwu.commands.session.misc.ZoneType;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.misc.CwuRank;
import com.oneliferp.cwu.misc.pagination.PaginationContext;
import com.oneliferp.cwu.misc.pagination.PaginationRegistry;
import com.oneliferp.cwu.models.IdentityModel;
import com.oneliferp.cwu.utils.SimpleDate;

public class EmployeeModel {
    private final String manager;

    private Long id;

    private IdentityModel identity;

    private CwuBranch branch;

    private CwuRank rank;

    private SimpleDate joinedAt;

    private PaginationContext<EmployeePageType> pagination;

    public EmployeeModel(final String manager) {
        this.manager = manager;
        this.joinedAt = SimpleDate.now();
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public void setIdentity(final IdentityModel identity) {
        this.identity = identity;
    }

    public IdentityModel getIdentity() {
        return this.identity;
    }

    public void setBranch(final CwuBranch branch) {
        this.branch = branch;
    }

    public CwuBranch getBranch() {
        return this.branch;
    }

    public void setRank(final CwuRank rank) {
        this.rank = rank;
    }

    public CwuRank getRank() {
        return this.rank;
    }

    public void setJoinedAt(final SimpleDate joinedAt) {
        this.joinedAt = joinedAt;
    }

    public SimpleDate getJoinedAt() {
        return this.joinedAt;
    }

    /* Methods */
    public String getManagerCid() {
        return this.manager;
    }

    public void begin() {
        this.pagination = new PaginationContext<>(PaginationRegistry.getEmployeePages(), EmployeePageType.PREVIEW);
    }

    public boolean isValid() {
        return this.id != null && this.identity != null && this.branch != null && this.joinedAt != null;
    }

    /* Utils */
    public ProfileModel toProfile() {
        return new ProfileModel(this.id, this.identity, this.branch, this.rank, this.joinedAt);
    }

    /* Page Metadata */
    public EmployeePageType getCurrentPage() {
        return this.pagination.getCurrent();
    }

    public void goNextPage() {
        this.pagination.setNext();
    }
    public void goPrevPage() {
        this.pagination.setPrev();
    }
    public void goPreviewPage() {
        this.pagination.setPreview();
    }
    public void goFirstPage() {
        this.pagination.setFirst();
    }

    public boolean isFirstPage() {
        return this.pagination.isFirst();
    }
    public boolean isLastPage() {
        return this.pagination.isLast();
    }

    public int getCurrentStep() {
        return this.pagination.getCurrentStep();
    }
    public int getMaxSteps() {
        return this.pagination.getMaxSteps();
    }

    public boolean hasPage(final EmployeePageType type) {
        return this.pagination.contains(type);
    }
}
