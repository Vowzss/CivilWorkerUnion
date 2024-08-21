package com.oneliferp.cwu.commands.report;

import com.oneliferp.cwu.cache.ReportCache;
import com.oneliferp.cwu.commands.CwuCommand;
import com.oneliferp.cwu.database.ProfileDatabase;
import com.oneliferp.cwu.database.ReportDatabase;
import com.oneliferp.cwu.exceptions.CwuException;
import com.oneliferp.cwu.commands.CommandContext;
import com.oneliferp.cwu.commands.report.misc.StockType;
import com.oneliferp.cwu.commands.profile.models.ProfileModel;
import com.oneliferp.cwu.commands.report.misc.actions.*;
import com.oneliferp.cwu.commands.report.models.ReportModel;
import com.oneliferp.cwu.commands.profile.exceptions.ProfileNotFoundException;
import com.oneliferp.cwu.commands.report.exceptions.ReportNotFoundException;
import com.oneliferp.cwu.commands.report.exceptions.ReportValidationException;
import com.oneliferp.cwu.commands.report.misc.*;
import com.oneliferp.cwu.commands.report.utils.ReportBuilderUtils;
import com.oneliferp.cwu.commands.session.exceptions.SessionNotFoundException;
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

    /*
    Abstract Handlers
    */
    @Override
    public void handleCommandInteraction(final SlashCommandInteractionEvent event) throws CwuException {
        final ProfileModel cwu = this.profileDatabase.getFromId(event.getUser().getIdLong());
        if (cwu == null) throw new ProfileNotFoundException();

        final ReportModel ongoingReport = this.reportCache.get(cwu.getCid());
        if (ongoingReport != null && ongoingReport.getType() != ReportType.UNKNOWN) {
            event.replyEmbeds(ReportBuilderUtils.alreadyExistMessage(ongoingReport.getType()))
                    .setComponents(ReportBuilderUtils.resumeOrOverwriteRow(cwu.getCid()))
                    .queue();
            return;
        }

        // Create rapport and set default values
        final ReportModel report = new ReportModel(cwu);
        this.reportCache.add(report.getEmployeeCid(), report);

        event.replyEmbeds(ReportBuilderUtils.beginMessage(cwu.getBranch(), report.getType()))
                .setComponents(ReportBuilderUtils.initRow(report))
                .queue();
    }

    @Override
    public void handleButtonInteraction(final ButtonInteractionEvent event, final CommandContext ctx) throws CwuException {
        final ReportModel report = this.reportCache.get(ctx.getCid());
        if (report == null) {
            event.getMessage().delete().queue();
            throw new ReportNotFoundException();
        }

        switch ((ReportButtonType) ctx.getEnumType()) {
            default -> throw new IllegalArgumentException();
            case BEGIN -> this.handleBeginButton(event, report);
            case FILL -> this.handleFillButton(event, report);
            case EDIT -> this.handleEditButton(event, report);
            case OVERWRITE -> this.handleOverwriteButton(event, report);
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
        final ReportModel report = this.reportCache.get(ctx.getCid());
        if (report == null) throw new SessionNotFoundException();

        switch ((ReportMenuType) ctx.getEnumType()) {
            case SELECT_TYPE -> {
                final ReportType type = ReportType.valueOf(event.getValues().get(0));
                report.setType(type);
                report.setStock(null);
                report.setTokens(null);

                event.editMessageEmbeds(ReportBuilderUtils.beginMessage(report.getBranch(), report.getType()))
                        .setComponents(ReportBuilderUtils.beginRow(report)).queue();
            }
            case SELECT_STOCK -> {
                final StockType type = StockType.valueOf(event.getValues().get(0));
                report.setStock(type);
                report.setTokens(null);

                event.editMessageEmbeds(ReportBuilderUtils.stockMessage(report))
                        .setComponents(ReportBuilderUtils.stockRow(report)).queue();
            }
        }
    }

    @Override
    public void handleModalInteraction(final ModalInteractionEvent event, final CommandContext ctx) throws CwuException {
        final ReportModel report = this.reportCache.get(ctx.getCid());
        if (report == null) throw new ReportNotFoundException();

        final String content = event.getValue("cwu_report.fill/data").getAsString();

        switch ((ReportModalType) ctx.getEnumType()) {
            default -> throw new IllegalArgumentException();
            case FILL_INFO -> {
                report.setInfo(content);
                event.editMessageEmbeds(ReportBuilderUtils.infoMessage(report))
                        .setComponents(ReportBuilderUtils.pageRow(report))
                        .queue();
            }
            case FILL_IDENTITY -> {
                report.setIdentity(IdentityModel.parseIdentity(content));
                event.editMessageEmbeds(ReportBuilderUtils.identityMessage(report))
                        .setComponents(ReportBuilderUtils.pageRow(report))
                        .queue();
            }
            case FILL_TOKENS -> {
                report.setTokens(RegexUtils.parseTokens(content));
                event.editMessageEmbeds(ReportBuilderUtils.tokenMessage(report))
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

        report.begin();
        this.goToPage(event, report);
    }

    private void handleEditButton(final ButtonInteractionEvent event, final ReportModel report) {
        report.goFirstPage();
        this.goToPage(event, report);
    }

    private void handleCancelButton(final ButtonInteractionEvent event, final ReportModel report) {
        this.reportCache.remove(report.getEmployeeCid());
        event.getMessage().delete().queue();

        event.reply("❌ Vous avez annuler votre rapport.")
                .queue();
        event.getMessage().delete().queue();
    }

    private void handleSubmitButton(final ButtonInteractionEvent event, final ReportModel report) throws ReportValidationException {
        if (!report.isValid()) throw new ReportValidationException();

        report.end();
        this.reportCache.remove(report.getEmployeeCid());

        // Save session within database
        this.reportDatabase.addOne(report);
        this.reportDatabase.save();

        event.editMessageEmbeds(ReportBuilderUtils.submitMessage(report))
                .setComponents(new ArrayList<>())
                .queue();
    }

    private void handleOverwriteButton(final ButtonInteractionEvent event, final ReportModel report) {
        // Reset to default values
        report.setType(ReportType.UNKNOWN);
        report.setInfo(null);
        report.setStock(null);
        report.setIdentity(null);
        report.setTokens(null);

        event.replyEmbeds(ReportBuilderUtils.beginMessage(report.getBranch(), report.getType()))
                .setComponents(ReportBuilderUtils.beginRow(report))
                .queue();
    }

    private void handleClearButton(final ButtonInteractionEvent event, final ReportModel report) {
        final ReportPageType currentPage = report.getCurrentPage();

        switch (currentPage) {
            case TYPE -> report.setType(ReportType.UNKNOWN);
            case STOCK -> report.setStock(null);
            case TOKENS -> report.setTokens(null);
            case IDENTITY -> report.setIdentity(null);
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
            case INFO -> {
                modalType = ReportModalType.FILL_INFO;
                inputBuilder.setPlaceholder("Rien à signaler");

                final var info = report.getInfo();
                if (info == null) break;
                inputBuilder.setValue(info);
            }
            case IDENTITY -> {
                modalType = ReportModalType.FILL_IDENTITY;
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

            case IDENTITY -> event.editMessageEmbeds(ReportBuilderUtils.identityMessage(report))
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
