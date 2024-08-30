package com.oneliferp.cwu.commands.modules.report.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.oneliferp.cwu.commands.modules.profile.models.ProfileModel;
import com.oneliferp.cwu.commands.modules.report.misc.ReportType;
import com.oneliferp.cwu.commands.modules.report.misc.StockType;
import com.oneliferp.cwu.commands.modules.report.misc.actions.ReportPageType;
import com.oneliferp.cwu.database.ProfileDatabase;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.misc.IdFactory;
import com.oneliferp.cwu.misc.pagination.Pageable;
import com.oneliferp.cwu.misc.pagination.PaginationContext;
import com.oneliferp.cwu.misc.pagination.PaginationRegistry;
import com.oneliferp.cwu.models.IdentityModel;
import com.oneliferp.cwu.utils.SimpleDate;
import com.oneliferp.cwu.utils.json.StockTypeFilter;
import org.jetbrains.annotations.Nullable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "branch"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CwuReportModel.class, name = "CWU"),
        @JsonSubTypes.Type(value = DtlReportModel.class, name = "DTL"),
        @JsonSubTypes.Type(value = DmsReportModel.class, name = "DMS"),
        @JsonSubTypes.Type(value = DmsReportModel.class, name = "DRT"),
})
public abstract class ReportModel extends Pageable<ReportPageType>  {
    @JsonProperty("id")
    protected String id;

    @JsonProperty("employee")
    protected IdentityModel employee;

    @JsonProperty("branch")
    protected CwuBranch branch;

    @JsonProperty("createdAt")
    protected SimpleDate createdAt;

    @JsonProperty("type")
    protected ReportType type;

    @JsonProperty("stock")
    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = StockTypeFilter.class)
    protected StockType stock;

    @JsonProperty("information")
    protected String info;

    protected ReportModel(final CwuBranch branch) {
        this.branch = branch;
    }

    protected ReportModel(final ProfileModel profile) {
        this.id = IdFactory.get().generateID();

        this.employee = profile.getIdentity();
        this.branch = profile.getBranch();
        this.createdAt = SimpleDate.now();

        this.stock = StockType.UNKNOWN;
        this.type = ReportType.UNKNOWN;
        this.info = null;
    }

    /* Getters & Setters */
    public String getId() {
        return this.id;
    }

    public IdentityModel getEmployee() {
        return this.employee;
    }

    public CwuBranch getBranch() {
        return this.branch;
    }

    public void setType(final ReportType type) {
        this.type = type;

        // update branch if mismatch to avoid data corruption
        if (!this.type.hasMainBranch()) {
            this.branch = CwuBranch.CWU;
            return;
        }
        this.branch = this.type.getMainBranch();
    }
    public ReportType getType() {
        return this.type;
    }

    public void setStock(final StockType type) {
        this.stock = type;
    }
    public StockType getStock() {
        return this.stock;
    }

    public void setInfo(@Nullable final String info) {
        this.info = (info == null || info.isBlank()) ? null : info;
    }
    public String getInfo() {
        return this.info;
    }

    public SimpleDate getCreatedAt() {
        return this.createdAt;
    }

    public abstract void setIdentity(final IdentityModel identity);
    public abstract IdentityModel getIdentity();

    public void setTax(final Integer tax) {
        throw new UnsupportedOperationException("Tax is not available for this type of report.");
    }
    public Integer getTax() {
        throw new UnsupportedOperationException("Tax is not available for this type of report.");
    }

    public void setHealthiness(final String healthiness) {
        throw new UnsupportedOperationException("Healthiness is not available for this type of report.");
    }
    public String getHealthiness() {
        throw new UnsupportedOperationException("Healthiness is not available for this type of report.");
    }

    public void setMedical(final String medical) {
        throw new UnsupportedOperationException("Medical is not available for this type of report.");
    }
    public String getMedical() {
        throw new UnsupportedOperationException("Medical is not available for this type of report.");
    }

    public void setRent(final Integer rent) {
        throw new UnsupportedOperationException("Rent is not available for this type of report.");
    }
    public Integer getRent() {
        throw new UnsupportedOperationException("Rent is not available for this type of report.");
    }

    public void setCost(final Integer cost) {
        throw new UnsupportedOperationException("Cost is not available for this type of report.");
    }
    public Integer getCost() {
        throw new UnsupportedOperationException("Cost is not available for this type of report.");
    }

    /* Utils */
    public String getEmployeeCid() {
        return this.employee.cid;
    }

    public ProfileModel resolveEmployee() {
        return ProfileDatabase.get().getFromCid(this.employee.cid);
    }

    public boolean isWithinWeek() {
        return this.createdAt.isWithinWeek();
    }

    /* Helpers */
    public String getDescriptionFormat(final boolean withIdentity) {
        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s  %s - %s **(ID: %s)**", this.branch.getEmoji(), this.branch.name(), this.type.getLabel(), this.id)).append("\n");
        if (withIdentity) sb.append(String.format("Employé : %s ", this.employee)).append("\n");
        sb.append(this.createdAt);
        return sb.toString();
    }

    public String getDescriptionFormat() {
        return this.getDescriptionFormat(true);
    }

    public String getDisplayFormat() {
        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s **Rapport :** %s", this.type.getBranchEmoji(), this.type.getLabel())).append("\n");
        sb.append(String.format("**Employé :** %s", this.employee)).append("\n");
        sb.append(String.format("**Date :** %s", this.getCreatedAt())).append("\n\n");
        sb.append("**Informations supplémentaires :**").append("\n").append(this.info == null ? "Rien à signaler" : info).append("\n\n");
        return sb.toString();
    }

    public String getStepTitle() {
        return String.format("%s  Rapport %s - %s | %d/%d", this.type.getBranchEmoji(), this.branch, this.type.getLabel(), this.getCurrentStep(), this.getMaxSteps());
    }

    public String getPreviewTitle() {
        return String.format("%s  Rapport %s - %s | Apperçu", this.type.getBranchEmoji(), this.branch, this.type.getLabel());
    }

    public String getEndingTitle() {
        return String.format("%s  Rapport %s - %s | Finalisation", this.type.getBranchEmoji(), this.branch, this.type.getLabel());
    }

    /* Pageable implementation */
    @Override
    public void start() {
        this.pagination = new PaginationContext<>(PaginationRegistry.getReportPages(this.type), ReportPageType.PREVIEW);
    }

    @Override
    public void end() {

    }

    @Override
    public void reset() {
        this.type = ReportType.UNKNOWN;
        this.stock = StockType.UNKNOWN;
        this.info = null;
    }

    @Override
    public boolean verify() {
        return this.employee != null && this.type != null;
    }
}
