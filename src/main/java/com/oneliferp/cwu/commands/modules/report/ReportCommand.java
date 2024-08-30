package com.oneliferp.cwu.commands.modules.report;

import com.oneliferp.cwu.cache.ReportCache;
import com.oneliferp.cwu.commands.CwuCommand;
import com.oneliferp.cwu.commands.modules.report.models.*;
import com.oneliferp.cwu.database.ProfileDatabase;
import com.oneliferp.cwu.database.ReportDatabase;
import com.oneliferp.cwu.exceptions.CwuException;
import com.oneliferp.cwu.commands.utils.CommandContext;
import com.oneliferp.cwu.commands.modules.report.misc.StockType;
import com.oneliferp.cwu.commands.modules.profile.models.ProfileModel;
import com.oneliferp.cwu.commands.modules.report.misc.actions.*;
import com.oneliferp.cwu.commands.modules.profile.exceptions.ProfileNotFoundException;
import com.oneliferp.cwu.commands.modules.report.exceptions.ReportNotFoundException;
import com.oneliferp.cwu.commands.modules.report.exceptions.ReportValidationException;
import com.oneliferp.cwu.commands.modules.report.misc.*;
import com.oneliferp.cwu.commands.modules.report.utils.ReportBuilderUtils;
import com.oneliferp.cwu.commands.modules.session.exceptions.SessionNotFoundException;
import com.oneliferp.cwu.exceptions.IdentityException;
import com.oneliferp.cwu.exceptions.IdentityMalformedException;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.models.IdentityModel;
import com.oneliferp.cwu.utils.RegexUtils;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.ArrayList;

public class ReportCommand extends CwuCommand {
    private final ReportDatabase reportDatabase;
    private final ReportCache reportCache;

    private final ProfileDatabase profileDatabase;

    public ReportCommand() {
        super("report", "rapport", "Vous permet de rédiger des rapports.");
        this.reportDatabase = ReportDatabase.get();
        this.reportCache = ReportCache.get();

        this.profileDatabase = ProfileDatabase.get();
    }

    /* Handlers */
    @Override
    public void handleCommandInteraction(final SlashCommandInteractionEvent event) throws CwuException {
        final ProfileModel cwu = this.profileDatabase.getFromId(event.getUser().getIdLong());
        if (cwu == null) throw new ProfileNotFoundException(event);

        final ReportModel ongoingReport = this.reportCache.find(cwu.getCid());
        if (ongoingReport != null && ongoingReport.getType() != ReportType.UNKNOWN) {
            event.replyEmbeds(ReportBuilderUtils.alreadyExistMessage(ongoingReport.getType()))
                    .setComponents(ReportBuilderUtils.resumeOrOverwriteRow(cwu.getCid()))
                    .setEphemeral(true).queue();
            return;
        }

        final ReportModel report = switch (cwu.getBranch()) {
            case CWU -> new CwuReportModel(cwu);
            case DTL -> new DtlReportModel(cwu);
            case DMS -> new DmsReportModel(cwu);
            case DRT -> new DrtReportModel(cwu);
        };
        this.reportCache.add(cwu.getCid(), report);

        event.replyEmbeds(ReportBuilderUtils.initMessage(cwu.getBranch(), ReportType.UNKNOWN))
                .setComponents(ReportBuilderUtils.initRow(cwu.getBranch(), cwu.getCid()))
                .setEphemeral(true).queue();
    }

    @Override
    public void handleButtonInteraction(final ButtonInteractionEvent event, final CommandContext ctx) throws CwuException {
        final ProfileModel profile = this.profileDatabase.getFromCid(ctx.getCid());
        if (profile == null) throw new ProfileNotFoundException(event);

        final ReportButtonType type = ctx.getEnumType();
        if (type == ReportButtonType.DELETE) {
            final ReportModel report = this.reportDatabase.get(ctx.getAsString("report"));
            if (report == null) throw new ReportNotFoundException(event);

            event.editMessageEmbeds(ReportBuilderUtils.deleteMessage(report))
                    .setComponents(ReportBuilderUtils.deleteComponent(profile.getCid(), report.getId())).queue();
            return;
        } else if (type == ReportButtonType.DELETE_CONFIRM) {
            this.reportDatabase.removeOne(ctx.getAsString("report"));
            this.reportDatabase.save();

            event.reply("Suppression du rapport avec succès.")
                    .setEphemeral(true).queue();
            event.getMessage().delete().queue();
            return;
        } else if (type == ReportButtonType.DELETE_CANCEL) {
            event.reply("Vous avez annulé la suppression du rapport.")
                    .setEphemeral(true).queue();
            event.getMessage().delete().queue();
            return;
        }

        final ReportModel report = this.reportCache.find(ctx.getCid());
        if (report == null) throw new ReportNotFoundException(event);

        switch (type) {
            default -> throw new IllegalArgumentException();
            case BEGIN -> this.handleBeginButton(event, report);
            case FILL -> this.handleFillButton(event, report);
            case EDIT -> this.handleEditButton(event, report);
            case OVERWRITE -> this.handleOverwriteButton(event, profile.getBranch(), report);
            case PREVIEW -> this.handlePreviewButton(event, report);
            case ABORT -> this.handleCancelButton(event, report);
            case CLEAR -> this.handleClearButton(event, report);
            case RESUME -> this.handleResumeButton(event, report);
            case SUBMIT -> this.handleSubmitButton(event, report);
            case PAGE_NEXT -> this.handlePageButton(event, report, true);
            case PAGE_PREV -> this.handlePageButton(event, report, false);
        }
    }

    @Override
    public void handleSelectionInteraction(final StringSelectInteractionEvent event, final CommandContext ctx) throws CwuException {
        final ProfileModel profile = this.profileDatabase.getFromCid(ctx.getCid());
        if (profile == null) throw new ProfileNotFoundException(event);

        ReportModel report = this.reportCache.find(ctx.getCid());
        if (report == null) throw new SessionNotFoundException(event);

        report.reset();

        switch ((ReportMenuType) ctx.getEnumType()) {
            case SELECT_TYPE -> {
                report.setType(ReportType.valueOf(event.getValues().get(0)));

                if (report instanceof CwuReportModel cwuReport && cwuReport.isRecastAvailable()) {
                    switch (report.getType().getMainBranch()) {
                        case DTL -> report = cwuReport.convertToDtlReport();
                        case DMS -> report = cwuReport.convertToDmsReport();
                        case DRT -> report = cwuReport.convertToDrtReport();
                    }
                }

                event.editMessageEmbeds(ReportBuilderUtils.initMessage(report.getBranch(), report.getType()))
                        .setComponents(ReportBuilderUtils.beginRow(profile.getBranch(), report)).queue();
            }
            case SELECT_STOCK -> {
                report.setType(ReportType.STOCK);
                report.setStock(StockType.valueOf(event.getValues().get(0)));

                event.editMessageEmbeds(ReportBuilderUtils.stockMessage(report))
                        .setComponents(ReportBuilderUtils.stockRow(report)).queue();
            }
        }
    }

    @Override
    public void handleModalInteraction(final ModalInteractionEvent event, final CommandContext ctx) throws CwuException {
        final ReportModel report = this.reportCache.find(ctx.getCid());
        if (report == null) throw new ReportNotFoundException(event);

        final String content = event.getValue("cwu_report.fill/data").getAsString();

        switch ((ReportModalType) ctx.getEnumType()) {
            default -> throw new IllegalArgumentException();
            case FILL_TENANT -> {
                try {
                    report.setIdentity(IdentityModel.parseIdentity(content));
                } catch (IdentityMalformedException e) {
                    throw new IdentityException(event);
                }

                event.editMessageEmbeds(ReportBuilderUtils.tenantMessage(report))
                        .setComponents(ReportBuilderUtils.pageRow(report))
                        .queue();
            }
            case FILL_PATIENT -> {
                try {
                    report.setIdentity(IdentityModel.parseIdentity(content));
                } catch (IdentityMalformedException e) {
                    throw new IdentityException(event);
                }

                event.editMessageEmbeds(ReportBuilderUtils.patientMessage(report))
                        .setComponents(ReportBuilderUtils.pageRow(report))
                        .queue();
            }
            case FILL_TAX -> {
                if (report instanceof DrtReportModel drtReport) {
                    drtReport.setTax(Integer.getInteger(content));
                    event.editMessageEmbeds(ReportBuilderUtils.taxMessage(drtReport))
                            .setComponents(ReportBuilderUtils.pageRow(drtReport))
                            .queue();
                } else if (report instanceof DmsReportModel dmsReport) {
                    dmsReport.setTax(Integer.getInteger(content));
                    event.editMessageEmbeds(ReportBuilderUtils.taxMessage(dmsReport))
                            .setComponents(ReportBuilderUtils.pageRow(dmsReport))
                            .queue();
                }
            }
            case FILL_TOKENS -> {
                report.setTokens(RegexUtils.parseTokens(content));
                event.editMessageEmbeds(ReportBuilderUtils.tokenMessage(report))
                        .setComponents(ReportBuilderUtils.pageRow(report))
                        .queue();
            }
            case FILL_HEALTHINESS -> {
                report.setHealthiness(content);
                event.editMessageEmbeds(ReportBuilderUtils.healthinessMessage(report))
                        .setComponents(ReportBuilderUtils.pageRow(report))
                        .queue();
            }
            case FILL_INFO -> {
                report.setInfo(content);
                event.editMessageEmbeds(ReportBuilderUtils.infoMessage(report))
                        .setComponents(ReportBuilderUtils.pageRow(report))
                        .queue();
            }
        }
    }

    /* Button handlers */
    protected void handleBeginButton(final ButtonInteractionEvent event, final ReportModel report) {
        if (report.getType() == ReportType.UNKNOWN) {
            event.reply("⛔ Vous devez sélectionner un type de rapport.").queue();
            return;
        }

        report.start();
        this.goToPage(event, report);
    }

    private void handleEditButton(final ButtonInteractionEvent event, final ReportModel report) {
        report.goFirstPage();
        this.goToPage(event, report);
    }

    private void handleCancelButton(final ButtonInteractionEvent event, final ReportModel report) {
        this.reportCache.delete(report.getEmployeeCid());

        event.reply("❌ Vous avez annuler votre rapport.")
                .queue();
        event.getMessage().delete().queue();
    }

    private void handleSubmitButton(final ButtonInteractionEvent event, final ReportModel report) throws ReportValidationException {
        if (!report.verify()) throw new ReportValidationException(event);

        report.end();
        this.reportCache.delete(report.getEmployeeCid());

        // Save session within database
        this.reportDatabase.addOne(report);
        this.reportDatabase.save();

        event.editMessageEmbeds(ReportBuilderUtils.submitMessage(report))
                .setComponents(new ArrayList<>())
                .queue();
    }

    private void handleOverwriteButton(final ButtonInteractionEvent event, final CwuBranch branch, final ReportModel report) {
        report.reset();

        event.replyEmbeds(ReportBuilderUtils.initMessage(branch, report.getType()))
                .setComponents(ReportBuilderUtils.beginRow(branch, report))
                .queue();
    }

    private void handleClearButton(final ButtonInteractionEvent event, final ReportModel report) {
        final ReportPageType currentPage = report.getCurrentPage();

        switch (currentPage) {
            case TYPE -> report.setType(ReportType.UNKNOWN);
            case STOCK -> report.setStock(null);
            case TENANT, PATIENT -> report.setIdentity(null);
            case HEALTHINESS -> report.setHealthiness(null);
            case TAX -> report.setTax(null);
            case TOKENS -> report.setTokens(null);
            case INFO -> report.setInfo(null);
        }

        this.goToPage(event, report);
    }

    private void handlePreviewButton(final ButtonInteractionEvent event, final ReportModel report) {
        report.goPreviewPage();
        this.goToPage(event, report);
    }

    private void handleResumeButton(final ButtonInteractionEvent event, final ReportModel report) {
        report.goPreviewPage();
        this.goToPage(event, report);
    }

    private void handleFillButton(final ButtonInteractionEvent event, final ReportModel report) {
        final ReportPageType currentPage = report.getCurrentPage();
        final TextInput.Builder inputBuilder = TextInput.create("cwu_report.fill/data", currentPage.getDescription(), TextInputStyle.PARAGRAPH)
                .setRequired(true);

        // Fill input if data exist at page
        ReportModalType modalType;
        switch (report.getCurrentPage()) {
            default -> throw new IllegalArgumentException();
            case TENANT -> {
                modalType = ReportModalType.FILL_TENANT;
                inputBuilder.setPlaceholder("Nom Prénom, #12345");

                final var identity = report.getIdentity();
                if (identity == null) break;
                inputBuilder.setValue(identity.toString());
            }
            case PATIENT -> {
                modalType = ReportModalType.FILL_PATIENT;
                inputBuilder.setPlaceholder("Nom Prénom, #12345");

                final var identity = report.getIdentity();
                if (identity == null) break;
                inputBuilder.setValue(identity.toString());
            }
            case TOKENS -> {
                modalType = ReportModalType.FILL_TOKENS;
                inputBuilder.setPlaceholder("ex: 1438");

                final var tokens = report.getTokens();
                if (tokens == null) break;
                inputBuilder.setValue(tokens.toString());
            }
            case HEALTHINESS -> {
                modalType = ReportModalType.FILL_HEALTHINESS;
                inputBuilder.setPlaceholder("Rien à signaler");

                final var healthiness = report.getHealthiness();
                if (healthiness == null) break;
                inputBuilder.setValue(healthiness);
            }
            case INFO -> {
                modalType = ReportModalType.FILL_INFO;
                inputBuilder.setPlaceholder("Rien à signaler");

                final var info = report.getInfo();
                if (info == null) break;
                inputBuilder.setValue(info);
            }
        }

        final Modal modal = Modal.create(modalType.build(report.getEmployeeCid()), "Ajout des informations")
                .addComponents(ActionRow.of(inputBuilder.build()))
                .build();

        event.replyModal(modal).queue();
    }

    private void handlePageButton(final ButtonInteractionEvent event, final ReportModel report, final boolean isNext) {
        if (isNext) report.goNextPage();
        else report.goPrevPage();

        this.goToPage(event, report);
    }

    /* Utils */
    protected void goToPage(final ButtonInteractionEvent event, final ReportModel report) {
        final ReportPageType page = report.getCurrentPage();

        final var callback = (switch (page) {
            default -> throw new IllegalArgumentException();
            case STOCK -> event.editMessageEmbeds(ReportBuilderUtils.stockMessage(report))
                    .setComponents(ReportBuilderUtils.stockRow(report));
            case TENANT -> event.editMessageEmbeds(ReportBuilderUtils.tenantMessage(report))
                    .setComponents(ReportBuilderUtils.pageRow(report));
            case PATIENT -> event.editMessageEmbeds(ReportBuilderUtils.patientMessage(report))
                    .setComponents(ReportBuilderUtils.pageRow(report));
            case HEALTHINESS -> event.editMessageEmbeds(ReportBuilderUtils.healthinessMessage(report))
                    .setComponents(ReportBuilderUtils.pageRow(report));
            case MEDICAL -> event.editMessageEmbeds(ReportBuilderUtils.medicalMessage(report))
                    .setComponents(ReportBuilderUtils.pageRow(report));
            case TAX -> event.editMessageEmbeds(ReportBuilderUtils.taxMessage(report))
                    .setComponents(ReportBuilderUtils.pageRow(report));
            case TOKENS -> event.editMessageEmbeds(ReportBuilderUtils.tokenMessage(report))
                    .setComponents(ReportBuilderUtils.pageRow(report));
            case INFO -> event.editMessageEmbeds(ReportBuilderUtils.infoMessage(report))
                    .setComponents(ReportBuilderUtils.pageRow(report));
            case PREVIEW -> event.editMessageEmbeds(ReportBuilderUtils.previewMessage(report))
                .setComponents(ReportBuilderUtils.pageRow(report));
        });

        callback.queue();
    }
}
