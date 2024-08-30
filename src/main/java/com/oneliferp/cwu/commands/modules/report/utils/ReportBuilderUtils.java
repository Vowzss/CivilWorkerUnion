package com.oneliferp.cwu.commands.modules.report.utils;

import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.commands.modules.report.misc.StockType;
import com.oneliferp.cwu.commands.modules.report.models.ReportModel;
import com.oneliferp.cwu.commands.modules.report.misc.actions.ReportButtonType;
import com.oneliferp.cwu.commands.modules.report.misc.actions.ReportMenuType;
import com.oneliferp.cwu.commands.modules.report.misc.actions.ReportPageType;
import com.oneliferp.cwu.commands.modules.report.misc.ReportType;
import com.oneliferp.cwu.misc.IActionType;
import com.oneliferp.cwu.models.IdentityModel;
import com.oneliferp.cwu.utils.EmbedUtils;
import com.oneliferp.cwu.utils.EmojiUtils;
import com.oneliferp.cwu.utils.Toolbox;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ReportBuilderUtils {
    /* Messages */
    public static MessageEmbed initMessage(final CwuBranch branch, final ReportType type) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(type == ReportType.UNKNOWN ? buildTitleWithBranch(branch) : buildTitleWithBranchAndType(branch, type));
        embed.setDescription("""
                Cette interface vous permet de créer un rapport.
                
                Vous allez procéder au remplissage des informations.
                S'ensuivra un résumé de votre rapport.""");
        return embed.build();
    }

    public static MessageEmbed alreadyExistMessage(final ReportType type) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Vous avez un rapport en cours!");

        final StringBuilder sb = new StringBuilder();
        sb.append("Vous ne pouvez faire qu'un rapport en simultané.\n");
        sb.append("Souhaitez-vous reprendre ou écraser le rapport en cours ?\n");
        sb.append("\n");
        sb.append(String.format("**Rapport :** %s\n", type.getLabel()));
        embed.setDescription(sb.toString());
        return embed.build();
    }

    public static MessageEmbed displayMessage(final ReportModel report) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Informations du rapport");

        final StringBuilder sb = new StringBuilder();
        sb.append(report.getDisplayFormat()).append("\n");
        embed.setDescription(sb.toString());
        return embed.build();
    }

    public static MessageEmbed deleteMessage(final ReportModel report) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("\uD83D\uDDD1 Action en cours - Suppression d'un rapport'");

        final StringBuilder sb = new StringBuilder();
        sb.append("Afin de terminer la procédure, veuillez confirmer votre choix.").append("\n\n");
        sb.append("**Aperçu du rapport concerné :**").append("\n");
        sb.append(report.getDescriptionFormat()).append("\n");
        embed.setDescription(sb.toString());
        return embed.build();
    }

    /* Fields */
    private static MessageEmbed.Field infoField(final String info) {
        final String name = String.format("%s  %s", EmojiUtils.getGreenCircle(),  ReportPageType.INFO.getDescription());
        final String value = info != null ? info : "`Rien à signaler`";
        return new MessageEmbed.Field(name, value, false);
    }
    public static MessageEmbed infoMessage(final ReportModel report) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(report.getStepTitle());
        embed.addField(infoField(report.getInfo()));
        return embed.build();
    }

    private static MessageEmbed.Field medicalField(final String medical) {
        final boolean hasValue = medical != null;

        final String name = String.format("%s  %s", EmojiUtils.getGreenOrRedCircle(hasValue),  ReportPageType.MEDICAL.getDescription());
        final String value = hasValue ? medical : "`Information manquante`";
        return new MessageEmbed.Field(name, value, false);
    }
    public static MessageEmbed medicalMessage(final ReportModel report) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(report.getStepTitle());
        embed.addField(medicalField(report.getMedical()));
        return embed.build();
    }

    private static MessageEmbed.Field stockField(final StockType type) {
        final boolean hasValue = type != StockType.UNKNOWN;

        final String name = String.format("%s  %s", EmojiUtils.getGreenOrRedCircle(hasValue),  ReportPageType.STOCK.getDescription());
        final String value = hasValue ? type.getLabel() : "`Information manquante`";
        return new MessageEmbed.Field(name, value, false);
    }
    public static MessageEmbed stockMessage(final ReportModel report) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(report.getStepTitle());
        embed.addField(stockField(report.getStock()));
        return embed.build();
    }

    private static MessageEmbed.Field identityField(final IdentityModel identity, final String description) {
        final boolean hasValue = identity != null;

        final String name = String.format("%s  %s", EmojiUtils.getGreenOrRedCircle(hasValue), description);
        final String value = hasValue ? identity.toString() : "`Information manquante`";
        return new MessageEmbed.Field(name, value, false);
    }
    private static MessageEmbed identityMessage(final ReportModel report, final String description) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(report.getStepTitle());
        embed.addField(identityField(report.getIdentity(), description));
        return embed.build();
    }
    public static MessageEmbed tenantMessage(final ReportModel report) {
        return identityMessage(report, ReportPageType.TENANT.getDescription());
    }
    public static MessageEmbed patientMessage(final ReportModel report) {
        return identityMessage(report, ReportPageType.PATIENT.getDescription());
    }
    public static MessageEmbed merchantMessage(final ReportModel report) {
        return identityMessage(report, ReportPageType.MERCHANT.getDescription());
    }

    private static MessageEmbed.Field tokenField(final Integer tokens, final String description) {
        final boolean hasValue = tokens != null;

        final String name = String.format("%s  %s", EmojiUtils.getGreenOrRedCircle(hasValue), description);
        final String value = String.format("%s", hasValue ? (tokens + " Tokens") : "`Information manquante`");
        return new MessageEmbed.Field(name, value, false);
    }
    public static MessageEmbed rentMessage(final ReportModel report) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(report.getStepTitle());
        embed.addField(tokenField(report.getRent(), ReportPageType.RENT.getDescription()));
        return embed.build();
    }
    public static MessageEmbed costMessage(final ReportModel report) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(report.getStepTitle());
        embed.addField(tokenField(report.getCost(), ReportPageType.COST.getDescription()));
        return embed.build();
    }


    private static MessageEmbed.Field healthinessField(final String healthiness) {
        final boolean hasValue = healthiness != null;

        final String name = String.format("%s  %s", EmojiUtils.getGreenOrRedCircle(hasValue), ReportPageType.HEALTHINESS.getDescription());
        final String value = hasValue ? healthiness : "`Information manquante`";
        return new MessageEmbed.Field(name, value, false);
    }
    public static MessageEmbed healthinessMessage(final ReportModel report) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(report.getStepTitle());
        embed.addField(healthinessField(report.getHealthiness()));
        return embed.build();
    }

    private static MessageEmbed.Field taxField(final Integer tax) {
        final boolean hasValue = tax != null;

        final String name = String.format("%s  %s", EmojiUtils.getGreenOrRedCircle(hasValue), ReportPageType.TAX.getDescription());
        final String value = hasValue ? (tax + " Tokens") : "`Information manquante`";
        return new MessageEmbed.Field(name, value, false);
    }
    public static MessageEmbed taxMessage(final ReportModel report) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(report.getStepTitle());
        embed.addField(taxField(report.getTax()));
        return embed.build();
    }

    public static MessageEmbed submitMessage(final ReportModel report) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(report.getEndingTitle());

        final StringBuilder sb = new StringBuilder();
        sb.append("Le rapport à été transmit avec succès.\n");
        sb.append("\n");

        embed.setDescription(sb.toString());
        return embed.build();
    }

    public static MessageEmbed previewMessage(final ReportModel report) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(report.getPreviewTitle());

        {
            final String name = String.format("%s  %s", EmojiUtils.getGreenCircle() , ReportPageType.TYPE.getDescription());
            embed.addField(new MessageEmbed.Field(name, report.getType().getLabel(), false));
        }
        if (report.hasPage(ReportPageType.STOCK)) {
            embed.addField(stockField(report.getStock()));
        }
        if (report.hasPage(ReportPageType.TENANT)) {
            embed.addField(identityField(report.getIdentity(), ReportPageType.TENANT.getDescription()));
        }
        if (report.hasPage(ReportPageType.PATIENT)) {
            embed.addField(identityField(report.getIdentity(), ReportPageType.PATIENT.getDescription()));
        }
        if (report.hasPage(ReportPageType.TAX)) {
            embed.addField(taxField(report.getTax()));
        }
        if (report.hasPage(ReportPageType.HEALTHINESS)) {
            embed.addField(healthinessField(report.getHealthiness()));
        }
        if (report.hasPage(ReportPageType.RENT)) {
            embed.addField(tokenField(report.getRent(), ReportPageType.RENT.getDescription()));
        }
        if (report.hasPage(ReportPageType.COST)) {
            embed.addField(tokenField(report.getCost(), ReportPageType.COST.getDescription()));
        }
        if (report.hasPage(ReportPageType.MEDICAL)) {
            embed.addField(medicalField(report.getMedical()));
        }
        if (report.hasPage(ReportPageType.INFO)) {
            embed.addField(infoField(report.getInfo()));
        }
        return embed.build();
    }

    /* Menus */
    private static StringSelectMenu typeMenu(final CwuBranch branch, final String cid, final ReportType type) {
        final var menu = StringSelectMenu.create(ReportMenuType.SELECT_TYPE.build(cid));
        final var options = ReportType.getAvailableReportTypes(branch)
                .stream().map(v -> SelectOption.of(v.getLabel(), v.name()).withEmoji(EmojiUtils.asDiscordEmoji(v.getBranchEmoji())))
                .toList();
        options.forEach(menu::addOptions);

        if (type != ReportType.UNKNOWN) Toolbox.setDefaultMenuOption(menu, options, type.name());
        return menu.build();
    }

    private static StringSelectMenu stockMenu(final ReportModel report) {
        final var menu = StringSelectMenu.create(ReportMenuType.SELECT_STOCK.build(report.getEmployeeCid()));
        final var options = Arrays.stream(StockType.values()).skip(1)
                .map(v -> SelectOption.of(v.getLabel(), v.name())).toList();
        options.forEach(menu::addOptions);

        final StockType type = report.getStock();
        if (type != StockType.UNKNOWN) Toolbox.setDefaultMenuOption(menu, options, type.name());
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

    private static Button cancelButton(final IActionType type, final String cid, final String id) {
        return Button.primary(type.build(cid, Map.of("report", id)), "Abandonner");
    }

    private static Button confirmButton(final IActionType type, final String cid, final String id) {
        return Button.danger(type.build(cid, Map.of("report", id)), "Confirmer");
    }

    private static Button deleteButton(final IActionType type, final String cid, final String id) {
        return Button.danger(type.build(cid, Map.of("report", id)), "Supprimer");
    }

    private static Button returnButton(final IActionType type, final String cid) {
        return Button.secondary(type.build(cid), "Retour en arrière");
    }

    /* Components */
    public static LayoutComponent displayComponent(final String cid, final String id) {
        return ActionRow.of(deleteButton(ReportButtonType.DELETE, cid, id), returnButton(ReportButtonType.OVERVIEW, cid));
    }

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

    public static LayoutComponent deleteComponent(final String cid, final String id) {
        return ActionRow.of(cancelButton(ReportButtonType.DELETE_CANCEL, cid, id), confirmButton(ReportButtonType.DELETE_CONFIRM, cid, id));
    }

    /* Components */
    public static ActionRow pageRow(final ReportModel report) {
        final String cid = report.getEmployeeCid();

        if (report.getCurrentPage() == ReportPageType.PREVIEW) {
            return ReportBuilderUtils.submitOrEditRow(cid);
        }

        return report.isFirstPage() ? nextRow(cid) : report.isLastPage() ? prevRow(cid) : prevAndNextRow(cid);
    }

    public static List<ActionRow> stockRow(final ReportModel report) {
        final String cid = report.getEmployeeCid();

        return List.of(
                ActionRow.of(stockMenu(report)),
                ActionRow.of(abortButton(cid), nextButton(cid))
        );
    }

    public static List<ActionRow> initRow(final CwuBranch branch, final String cid) {
        return List.of(
                ActionRow.of(typeMenu(branch, cid, ReportType.UNKNOWN)),
                ActionRow.of(abortButton(cid))
        );
    }

    public static List<ActionRow> beginRow(final CwuBranch branch, final ReportModel report) {
        final String cid = report.getEmployeeCid();

        return List.of(
                ActionRow.of(typeMenu(branch, cid, report.getType())),
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
}
