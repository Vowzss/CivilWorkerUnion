package com.oneliferp.cwu.commands.manage.utils;

import com.oneliferp.cwu.commands.manage.misc.ids.ManageButtonType;
import com.oneliferp.cwu.database.EmployeeDatabase;
import com.oneliferp.cwu.database.ReportDatabase;
import com.oneliferp.cwu.database.SessionDatabase;
import com.oneliferp.cwu.misc.IActionType;
import com.oneliferp.cwu.utils.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class ManageBuilderUtils {
    /* Messages */
    public static MessageEmbed profileView() {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Liste des employés.");

        final StringBuilder sb = new StringBuilder();
        sb.append("Cette interface vous permet de gérer l'effectif total.\n");
        sb.append("Pour toutes actions, munisser vous du CID uniquement.\n");
        sb.append("\n");

        EmployeeDatabase.get().getAsGroupAndOrder().forEach((branch, employees) -> {
            sb.append(String.format("%s  **Employés %s**", branch.getEmoji(), branch.name()));
            sb.append("\n");
            employees.forEach(employee -> sb.append(String.format("[%s] %s", employee.getRank().getLabel(), employee.getIdentity().toString())).append("\n"));
            sb.append("\n");
        });

        embed.setDescription(sb.toString());
        return embed.build();
    }

    public static MessageEmbed sessionView() {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Historique des sessions.");

        final StringBuilder sb = new StringBuilder();
        sb.append("Cette interface vous permet d'accéder aux sessions de travail.\n");
        sb.append("Pour toutes actions, munisser vous de l'ID uniquement.\n");
        sb.append("\n");

        SessionDatabase.get().getLatests(8).forEach(session -> {
            sb.append(String.format("%s  %s **(ID: %s)**", session.getType().getEmoji(), session.getType().getLabel(), session.getId())).append("\n");
        });

        embed.setDescription(sb.toString());
        return embed.build();
    }

    public static MessageEmbed reportView() {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Historique des rapports.");

        final StringBuilder sb = new StringBuilder();
        sb.append("Cette interface vous permet d'accéder aux rapports.\n");
        sb.append("Pour toutes actions, munisser vous de l'ID uniquement.\n");
        sb.append("\n");

        ReportDatabase.get().getLatests(8).forEach(report -> {
            sb.append(String.format("%s  %s **(ID: %s)**", report.getBranch().getEmoji(), report.getType().getLabel(), report.getId())).append("\n");
        });

        embed.setDescription(sb.toString());
        return embed.build();
    }

    /* Modals */


    /* Buttons */
    public static Button createButton(final IActionType type) {
        return Button.primary(type.build(), "Enregistrement");
    }

    public static Button deleteButton(final IActionType type) {
        return Button.danger(type.build(), "Suppression");
    }

    public static Button editButton(final IActionType type) {
        return Button.secondary(type.build(), "Modification");
    }

    public static Button viewButton(final IActionType type) {
        return Button.secondary(type.build(), "Visualisation");
    }

    /* Rows */
    public static ActionRow profileActionRow() {
        return ActionRow.of(createButton(ManageButtonType.PROFILE_CREATE), deleteButton(ManageButtonType.PROFILE_DELETE), editButton(ManageButtonType.PROFILE_EDIT), viewButton(ManageButtonType.PROFILE_VIEW));
    }

    public static ActionRow sessionActionRow() {
        return ActionRow.of(deleteButton(ManageButtonType.SESSION_DELETE), viewButton(ManageButtonType.SESSION_VIEW));
    }

    public static ActionRow reportActionRow() {
        return ActionRow.of(deleteButton(ManageButtonType.REPORT_DELETE), viewButton(ManageButtonType.REPORT_VIEW));
    }
}
