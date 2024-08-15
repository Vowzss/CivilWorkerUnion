package com.oneliferp.cwu.modules.report.utils;

import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.modules.report.misc.StockType;
import com.oneliferp.cwu.modules.report.models.ReportModel;
import com.oneliferp.cwu.modules.report.misc.ids.ReportButtonType;
import com.oneliferp.cwu.modules.report.misc.ids.ReportMenuType;
import com.oneliferp.cwu.modules.report.misc.ids.ReportPageType;
import com.oneliferp.cwu.modules.report.misc.ReportType;
import com.oneliferp.cwu.utils.EmbedUtils;
import com.oneliferp.cwu.utils.EmojiUtils;
import com.oneliferp.cwu.utils.Toolbox;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.Arrays;
import java.util.List;

public class ReportBuilderUtils {
    /* Embeds */
    public static MessageEmbed beginMessage(final CwuBranch branch, final ReportType type) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(type == ReportType.UNKNOWN ? buildTitleWithBranch(branch) : buildTitleWithBranchAndType(branch, type));
        embed.setDescription("""
                Vous allez procéder au remplissage des informations.
                S'ensuivra un résumé de votre rapport.""");
        return embed.build();
    }

    public static MessageEmbed ongoingMessage(final ReportModel report) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Vous avez un rapport en cours!");

        final StringBuilder sb = new StringBuilder();
        sb.append("Vous ne pouvez faire qu'un rapport en simultané.\n");
        sb.append("Souhaitez-vous reprendre ou écraser le rapport en cours ?\n");
        sb.append("\n");
        sb.append(String.format("**Rapport:** %s\n", report.getType().getLabel()));
        embed.setDescription(sb.toString());
        return embed.build();
    }

    public static MessageEmbed infoMessage(final ReportModel report) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(report.hasSingleStep() ? buildTitleWithBranchAndType(report.getBranch(), report.getType()) : buildStepTitle(report));

        {
            final var info = report.getInfo();

            final String name = String.format("%s  %s", EmojiUtils.getGreenCircle(), ReportPageType.INFO.getDescription());
            final String value = info == null ? "Rien à signaler" : info;
            embed.addField(new MessageEmbed.Field(name, value, false));
        }

        return embed.build();
    }

    public static MessageEmbed stockMessage(final ReportModel report) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(buildStepTitle(report));

        {
            final var stock = report.getStock();
            final boolean hasValue = stock != null;

            final String name = String.format("%s  %s", hasValue ? EmojiUtils.getGreenCircle() : EmojiUtils.getRedCircle(), ReportPageType.STOCK.getDescription());
            final String value = String.format("%s", hasValue ? stock.getLabel() : "`Information manquante`");
            embed.addField(new MessageEmbed.Field(name, value, false));
        }

        return embed.build();
    }

    public static MessageEmbed identityMessage(final ReportModel report) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(buildStepTitle(report));

        {
            final var identity = report.getIdentity();
            final boolean hasValue = identity != null;

            final String name = String.format("%s  %s", hasValue ? EmojiUtils.getGreenCircle() : EmojiUtils.getRedCircle(), report.getCurrentPage().getDescription());
            final String value = String.format("%s", hasValue ? identity : "`Information manquante`");
            embed.addField(new MessageEmbed.Field(name, value, false));
        }

        return embed.build();
    }

    public static MessageEmbed tokenMessage(final ReportModel report) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(buildStepTitle(report));

        {
            final var tokens = report.getTokens();
            final boolean hasValue = tokens != null;

            final String name = String.format("%s  %s", hasValue ? EmojiUtils.getGreenCircle() : EmojiUtils.getRedCircle(), report.getCurrentPage().getDescription());
            final String value = String.format("%s", hasValue ? (tokens + " Tokens") : "`Information manquante`");
            embed.addField(new MessageEmbed.Field(name, value, false));
        }

        return embed.build();
    }

    public static MessageEmbed submitMessage(final ReportModel report) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(buildEndingTitle(report));

        final StringBuilder sb = new StringBuilder();
        sb.append("Le rapport à été transmit avec succès.\n");
        sb.append("\n");

        sb.append(String.format("Nombre de **rapport soumis** cette semaine: %d Rapports.\n", report.resolveCwu().getWeeklyReportCount()));
        embed.setDescription(sb.toString());
        return embed.build();
    }

    public static MessageEmbed previewMessage(final ReportModel report) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(String.format(buildPreviewTitle(report)));

        {
            final String name = String.format("%s  %s",  EmojiUtils.getGreenCircle() , ReportPageType.TYPE.getDescription());
            embed.addField(new MessageEmbed.Field(name, report.getType().getLabel(), false));
        }
        if (report.hasPage(ReportPageType.STOCK))
        {
            final var stock = report.getStock();
            final boolean hasValue = stock != null;

            final String name = String.format("%s  %s",  hasValue ? EmojiUtils.getGreenCircle() : EmojiUtils.getRedCircle(), ReportPageType.STOCK.getDescription());
            final String value = String.format("%s", hasValue ? stock.getLabel() : "`Information manquante`");
            embed.addField(new MessageEmbed.Field(name, value, false));
        }
        if (report.hasPage(ReportPageType.IDENTITY))
        {
            final var identity = report.getIdentity();
            final boolean hasValue = identity != null;

            final String name = String.format("%s  %s",  hasValue ? EmojiUtils.getGreenCircle() : EmojiUtils.getRedCircle(), ReportPageType.IDENTITY.getDescription());
            final String value = String.format("%s", hasValue ? identity : "`Information manquante`");
            embed.addField(new MessageEmbed.Field(name, value, false));
        }
        if (report.hasPage(ReportPageType.TOKENS))
        {
            final var tokens = report.getTokens();
            final boolean hasValue = tokens != null;

            final String name = String.format("%s  %s",  hasValue ? EmojiUtils.getGreenCircle() : EmojiUtils.getRedCircle(), ReportPageType.TOKENS.getDescription());
            final String value = hasValue ? (tokens + " Tokens") : "`Information manquante`";
            embed.addField(new MessageEmbed.Field(name, value, false));
        }
        {
            final var info = report.getInfo();

            final String name = String.format("%s  %s", EmojiUtils.getGreenCircle(), ReportPageType.INFO.getDescription());
            final String value = info == null ? "Rien à signaler" : info;
            embed.addField(new MessageEmbed.Field(name, value, false));
        }
        return embed.build();
    }

    /* Menus */
    private static StringSelectMenu typeMenu(final ReportModel report) {
        final var menu = StringSelectMenu.create(ReportMenuType.SELECT_TYPE.build(report.getManagerCid()));
        final var options = ReportType.getAvailableReportTypes(report.getBranch())
                .stream().map(v -> SelectOption.of(v.getLabel(), v.name()))
                .toList();
        options.forEach(menu::addOptions);

        final ReportType type = report.getType();
        if (type != ReportType.UNKNOWN) Toolbox.setDefaulMenuOption(menu, options, type.name());
        return menu.build();
    }

    private static StringSelectMenu stockMenu(final ReportModel report) {
        final var menu = StringSelectMenu.create(ReportMenuType.SELECT_STOCK.build(report.getManagerCid()));
        final var options = Arrays.stream(StockType.values())
                .map(v -> SelectOption.of(v.getLabel(), v.name())).toList();
        options.forEach(menu::addOptions);

        final StockType type = report.getStock();
        if (type != null) Toolbox.setDefaulMenuOption(menu, options, type.name());
        return menu.build();
    }

    /* Buttons */
    private static Button startButton(final String cid) {
        return Button.primary(ReportButtonType.BEGIN.build(cid), "Commencer");
    }

    private static Button abortButton(final String cid) {
        return Button.danger(ReportButtonType.ABORT.build(cid), "Abandonner");
    }

    private static Button clearButton(final String cid) {
        return Button.danger(ReportButtonType.CLEAR.build(cid), "Effacer");
    }

    private static Button prevButton(final String cid) {
        return Button.secondary(ReportButtonType.PAGE_PREV.build(cid), Emoji.fromUnicode("\u2B05"));
    }

    private static Button nextButton(final String cid) {
        return Button.secondary(ReportButtonType.PAGE_NEXT.build(cid), Emoji.fromUnicode("\u27A1"));
    }

    private static Button fillButton(final String cid) {
        return Button.primary(ReportButtonType.FILL.build(cid), "Remplir");
    }

    private static Button previewButton(final String cid) {
        return Button.success(ReportButtonType.PREVIEW.build(cid), "Apperçu");
    }

    private static Button resumeButton(final String cid) {
        return Button.primary(ReportButtonType.RESUME.build(cid), "Reprendre");
    }

    private static Button overwriteButton(final String cid) {
        return Button.danger(ReportButtonType.OVERWRITE.build(cid), "Écraser");
    }

    private static Button editButton(final String cid) {
        return Button.secondary(ReportButtonType.EDIT.build(cid), "Modifier");
    }

    private static Button submitButton(final String cid) {
        return Button.success(ReportButtonType.SUBMIT.build(cid), "Envoyer");
    }

    /* Rows */
    private static ActionRow nextRow(final String cid) {
        return ActionRow.of(clearButton(cid), fillButton(cid), nextButton(cid));
    }

    private static ActionRow prevRow(final String cid) {
        return ActionRow.of(previewButton(cid), clearButton(cid), fillButton(cid), prevButton(cid));
    }

    public static ActionRow resumeOrOverwriteRow(final String cid) {
        return ActionRow.of(overwriteButton(cid), resumeButton(cid));
    }

    private static ActionRow submitOrEditRow(final String cid) {
        return ActionRow.of(editButton(cid), submitButton(cid));
    }

    private static ActionRow prevAndNextRow(final String cid) {
        return ActionRow.of(clearButton(cid), fillButton(cid), prevButton(cid), nextButton(cid));
    }

    public static ActionRow pageRow(final ReportModel report) {
        final String cid = report.getManagerCid();

        if (report.getCurrentPage() == ReportPageType.PREVIEW) {
            return ReportBuilderUtils.submitOrEditRow(cid);
        }

        return report.isFirstPage() ? nextRow(cid) : report.isLastPage() ? prevRow(cid) : prevAndNextRow(cid);
    }

    public static List<ActionRow> stockRow(final ReportModel report) {
        final String cid = report.getManagerCid();

        return List.of(
                ActionRow.of(stockMenu(report)),
                ActionRow.of(abortButton(cid), nextButton(cid))
        );
    }

    public static List<ActionRow> initRow(final ReportModel report) {
        final String cid = report.getManagerCid();

        return List.of(
                ActionRow.of(typeMenu(report)),
                ActionRow.of(abortButton(cid))
        );
    }

    public static List<ActionRow> beginRow(final ReportModel report) {
        final String cid = report.getManagerCid();

        return List.of(
                ActionRow.of(typeMenu(report)),
                ActionRow.of(abortButton(cid), startButton(cid))
        );
    }

    /* Utils */
    public static String buildTitleWithBranch(final CwuBranch branch) {
        return String.format("%s  Rapport %s", branch.getEmoji(), branch.name());
    }

    public static String buildTitleWithBranchAndType(final CwuBranch branch, final ReportType type) {
        return String.format("%s  Rapport %s - %s", branch.getEmoji(), branch.name(), type.getLabel());
    }

    public static String buildStepTitle(final ReportModel report) {
        final ReportType type = report.getType();
        return String.format("%s  Rapport %s - %s | %d/%d", type.getBranchEmoji(), report.getBranch(), type.getLabel(), report.getCurrentStep(), report.getMaxSteps());
    }

    public static String buildPreviewTitle(final ReportModel report) {
        final ReportType type = report.getType();
        return String.format("%s  Rapport %s - %s | Apperçu", type.getBranchEmoji(), report.getBranch(), type.getLabel());
    }

    public static String buildEndingTitle(final ReportModel report) {
        final ReportType type = report.getType();
        return String.format("%s  Rapport %s - %s | Finalisation", type.getBranchEmoji(), report.getBranch(), type.getLabel());
    }
}
