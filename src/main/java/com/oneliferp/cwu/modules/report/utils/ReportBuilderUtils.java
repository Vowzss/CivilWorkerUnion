package com.oneliferp.cwu.modules.report.utils;

import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.misc.ReportType;
import com.oneliferp.cwu.misc.SessionType;
import com.oneliferp.cwu.models.ReportModel;
import com.oneliferp.cwu.models.SessionModel;
import com.oneliferp.cwu.modules.report.misc.ReportButtonType;
import com.oneliferp.cwu.modules.report.misc.ReportPageType;
import com.oneliferp.cwu.modules.session.misc.SessionPageType;
import com.oneliferp.cwu.utils.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class ReportBuilderUtils {
    /*
    Messages
    */
    public static MessageEmbed startMessage(final CwuBranch branch) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(String.format("%s  Rapport - %s", branch.getEmoji(), branch.name()));
        embed.setDescription("Vous allez procéder au remplissage des informations.\nS'ensuivra un résumé de votre rapport.");
        return embed.build();
    }

    public static MessageEmbed infoMessage(final ReportModel report) {
        final ReportType type = report.getType();
        final ReportPageType page = report.getCurrentPage();
        final var value = report.getInfo();

        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(String.format("%s  Rapport - %s | Étape %d/%d", report.getType().getBranchEmoji(), type.getLabel(), report.getCurrentStep(), report.getMaxSteps()));
        embed.addField(new MessageEmbed.Field(page.getDescription(), value == null ? "Rien à signaler." : value, false));

        return embed.build();
    }

    public static MessageEmbed previewMessage(final ReportModel report) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(String.format("%s  Rapport - %s | Apperçu", report.getType().getBranchEmoji(), report.getType().getLabel()));
        embed.addField(new MessageEmbed.Field("Type de rapport:", report.getType().getLabel(), false));

        return embed.build();
    }

    /*
    Buttons
    */
    private static Button startButton(final String cid) {
        return Button.primary(ReportButtonType.START.build(cid), "Commencer");
    }

    private static Button cancelButton(final String cid) {
        return Button.danger(ReportButtonType.CANCEL.build(cid), "Abandonner");
    }

    /*
    Utils
    */
    public static ActionRow startAndCancelRow(final String cid) {
        return ActionRow.of(startButton(cid), cancelButton(cid));
    }
}
