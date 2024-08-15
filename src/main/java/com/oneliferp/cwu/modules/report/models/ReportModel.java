package com.oneliferp.cwu.modules.report.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.database.CwuDatabase;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.modules.report.misc.StockType;
import com.oneliferp.cwu.models.CwuModel;
import com.oneliferp.cwu.models.IdentityModel;
import com.oneliferp.cwu.modules.report.misc.ReportType;
import com.oneliferp.cwu.modules.report.misc.ids.ReportPageType;
import com.oneliferp.cwu.misc.pagination.PaginationContext;
import com.oneliferp.cwu.misc.pagination.PaginationRegistry;
import org.jetbrains.annotations.Nullable;

public class ReportModel {
    @JsonProperty("manager")
    private IdentityModel manager;

    @JsonProperty("branch")
    private CwuBranch branch;

    @JsonProperty("type")
    private ReportType type;

    @JsonProperty("identity")
    private IdentityModel identity;

    @JsonProperty("stock")
    private StockType stock;

    @JsonProperty("tokens")
    private Integer tokens;

    @JsonProperty("information")
    private String info;

    @JsonIgnore
    private PaginationContext<ReportPageType> pagination;

    public ReportModel(final CwuModel cwu) {
        this.manager = cwu.getIdentity();
        this.branch = cwu.getBranch();
        this.type = ReportType.UNKNOWN;
    }

    /* Getters & Setters */
    public CwuBranch getBranch() {
        return this.branch;
    }

    public void setType(final ReportType type) {
        this.type = type;
    }
    public ReportType getType() {
        return this.type;
    }

    public void setIdentity(final IdentityModel identity) {
        this.identity = identity;
    }
    public IdentityModel getIdentity() {
        return this.identity;
    }

    public void setStock(final StockType type) {
        this.stock = type;
    }
    public StockType getStock() {
        return this.stock;
    }

    public void setTokens(final Integer tokens) {
        this.tokens = tokens;
    }
    public Integer getTokens() {
        return this.tokens;
    }

    public void setInfo(@Nullable final String info) {
        this.info = (info == null || info.isBlank()) ? null : info;
    }
    public String getInfo() {
        return this.info;
    }

    /* Methods */
    public void begin() {
        this.pagination = new PaginationContext<>(PaginationRegistry.getReportPages(this.type), ReportPageType.PREVIEW);
    }

    public void end() {

    }

    public boolean isValid() {
        // FIX TO CHECK BASED OF SPECIFIC TYPE
        return this.manager != null && this.type != null;
    }

    /* Page Metadata */
    public ReportPageType getCurrentPage() {
        return this.pagination.getCurrent();
    }

    public boolean hasSingleStep() {
        return this.pagination.getMaxSteps() <= 1;
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

    public boolean hasPage(final ReportPageType type) {
        return this.pagination.contains(type);
    }

    /* Utils */
    public String getManagerCid() {
        return this.manager.cid;
    }

    public int computeStockItemCount() {
        return this.tokens / this.stock.getPrice();
    }

    public int resolveStockSpareTokens() {
        return this.tokens % this.stock.getPrice();
    }

    public CwuModel resolveCwu() {
        return CwuDatabase.get().getFromCid(this.manager.cid);
    }
}
