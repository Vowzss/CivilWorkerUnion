package com.oneliferp.cwu.modules.report.command;

import com.oneliferp.cwu.cache.ReportCache;
import com.oneliferp.cwu.commands.CwuCommand;
import com.oneliferp.cwu.database.CwuDatabase;
import com.oneliferp.cwu.database.ReportDatabase;
import com.oneliferp.cwu.misc.EventTypeData;
import com.oneliferp.cwu.modules.report.misc.ReportPageType;
import com.oneliferp.cwu.modules.session.misc.SessionPageType;
import com.oneliferp.cwu.models.CwuModel;
import com.oneliferp.cwu.models.ReportModel;
import com.oneliferp.cwu.modules.profile.exceptions.ProfileNotFoundException;
import com.oneliferp.cwu.modules.report.exceptions.ReportNotFoundException;
import com.oneliferp.cwu.modules.report.misc.ReportButtonType;
import com.oneliferp.cwu.modules.report.misc.ReportCommandType;
import com.oneliferp.cwu.modules.report.utils.ReportBuilderUtils;
import com.oneliferp.cwu.modules.session.utils.SessionBuilderUtils;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class ReportCommand extends CwuCommand {
    private final ReportDatabase reportDatabase;
    private final ReportCache reportCache;

    private final CwuDatabase cwuDatabase;

    public ReportCommand() {
        super(ReportCommandType.BASE.getId(), ReportCommandType.BASE.getDescription());
        this.reportDatabase = ReportDatabase.get();
        this.reportCache = ReportCache.get();

        this.cwuDatabase = CwuDatabase.get();
    }

    /*
    Abstract Handlers
    */
    @Override
    public void handleCommandInteraction(final SlashCommandInteractionEvent event) throws Exception {
        final CwuModel cwu = this.cwuDatabase.getFromId(event.getUser().getIdLong());
        if (cwu == null) throw new ProfileNotFoundException();

        // Create rapport and set default values
        final ReportModel report = new ReportModel(cwu);
        this.reportCache.add(report.getManagerCid(), report);

        event.replyEmbeds(ReportBuilderUtils.startMessage(cwu.getBranch()))
                .setComponents(ReportBuilderUtils.startAndCancelRow(cwu.getCid()))
                .setEphemeral(true).queue();
    }

    @Override
    public void handleButtonInteraction(ButtonInteractionEvent event, String eventID) throws Exception {
        final EventTypeData eventType = new EventTypeData(eventID);

        final String cid = eventType.getCid();
        final ReportModel report = this.reportCache.get(cid);
        if (report == null) throw new ReportNotFoundException();

        switch ((ReportButtonType) eventType.enumType) {
            default -> throw new IllegalArgumentException();
            case START -> this.handleStartButton(event, cid);
            case CANCEL -> this.handleCancelButton(event, cid);
        }
    }

    /*
    Button handlers
    */
    protected void handleStartButton(final ButtonInteractionEvent event, final String cid) throws ReportNotFoundException {
        final ReportModel report = this.reportCache.get(cid);
        if (report == null) throw new ReportNotFoundException();

        this.goToPage(event, report);
    }

    private void handleCancelButton(final ButtonInteractionEvent event, final String cid) throws ReportNotFoundException {
        final ReportModel report = this.reportCache.get(cid);
        if (report == null) throw new ReportNotFoundException();

        this.reportCache.remove(cid);

        event.reply("❌ Vous avez annuler votre rapport.")
                .setEphemeral(true).queue();
    }

    /*
    Utils
    */
    protected void goToPage(final ButtonInteractionEvent event, final ReportModel report) {
        final ReportPageType page = report.getCurrentPage();

        final MessageEmbed embed = (switch (page) {
            default -> throw new IllegalArgumentException();
            case INFO -> ReportBuilderUtils.infoMessage(report);
            case PREVIEW -> ReportBuilderUtils.previewMessage(report);
        });

        final var callback = event.editMessageEmbeds(embed);

        callback.queue();
    }
}
