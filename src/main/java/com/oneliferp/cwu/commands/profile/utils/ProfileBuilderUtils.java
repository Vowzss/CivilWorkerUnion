package com.oneliferp.cwu.commands.profile.utils;

import com.oneliferp.cwu.commands.profile.models.ProfileModel;
import com.oneliferp.cwu.commands.report.models.ReportModel;
import com.oneliferp.cwu.commands.session.models.SessionModel;
import com.oneliferp.cwu.commands.profile.misc.actions.ProfileButtonType;
import com.oneliferp.cwu.utils.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.List;

public class ProfileBuilderUtils {
    /* Messages */
    public static MessageEmbed profileMessage(final ProfileModel cwu) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("\uD83D\uDD0E  Informations du profil");

        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("**Identité:** %s\n", cwu.getIdentity()));
        sb.append("\n");
        sb.append(String.format("**Division:** %s (%s)\n", cwu.getBranch(), cwu.getBranch().getMeaning()));
        sb.append(String.format("**Grade:** %s\n", cwu.getRank().getLabel()));
        sb.append("\n");
        sb.append(String.format("**Associé à:** <@%d>", cwu.getId()));
        embed.setDescription(sb.toString());
        return embed.build();
    }

    public static MessageEmbed statsMessage(final ProfileModel cwu) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("\uD83D\uDCCA  Statistiques du profil");

        final SessionModel latestSession = cwu.getLatestSession();

        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("**Rejoint le:** %s\n", cwu.getJoinedAt().getDate()));
        sb.append("\n");
        if (latestSession != null) {
            sb.append(String.format("**Dernière session le:** %s\n", latestSession.getPeriod().getStartedAt().getDate()));
            sb.append(String.format("**Type de session:** %s\n", latestSession.getType().getLabel()));
            sb.append("\n");
        }
        sb.append(String.format("**Nombre de sessions:** %d\n", cwu.getSessionCount()));
        sb.append(String.format("**Compteur hebdomadaire:** %d\n", cwu.getWeeklySessionCount()));
        sb.append("\n");

        final ReportModel latestReport = cwu.getLatestReport();
        if (latestReport != null) {
            sb.append(String.format("**Dernier rapport le:** %s\n", latestReport.getCreatedAt().getDate()));
            sb.append(String.format("**Type de rapport:** %s\n", latestReport.getType().getLabel()));
            sb.append("\n");
        }
        sb.append(String.format("**Nombre de rapports:** %d\n", cwu.getReportCount()));
        sb.append(String.format("**Compteur hebdomadaire:** %d\n", cwu.getWeeklyReportCount()));
        embed.setDescription(sb.toString());
        return embed.build();
    }

    public static MessageEmbed suppressMessage(final ProfileModel cwu) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("\uD83D\uDDD1  Suppression du profil");
        embed.setDescription("Afin de terminer la procédure, veuillez confirmer votre choix.");
        embed.addField(new MessageEmbed.Field("Action en cours", String.format("Vous allez supprimer le profil: **%s**.", cwu.getIdentity()), false));
        return embed.build();
    }

    /* Buttons */
    private static Button statsButton(final String cid) {
        return Button.primary(ProfileButtonType.STATS.build(cid), "Statistiques");
    }

    private static Button deleteButton(final String cid) {
        return Button.danger(ProfileButtonType.DELETE.build(cid), "Supprimer");
    }

    public static Button returnButton(final String cid) {
        return Button.secondary(ProfileButtonType.RETURN.build(cid), "Retour");
    }

    public static Button confirmButton(final ProfileButtonType type, final String cid) {
        return Button.danger(type.build(cid), "Confirmer");
    }

    public static Button cancelButton(final ProfileButtonType type, final String cid) {
        return Button.primary(type.build(cid), "Annuler");
    }

    /* Rows */
    public static List<Button> statsAndDeleteRow(final String cid) {
        return List.of(deleteButton(cid), statsButton(cid));
    }

    public static List<Button> confirmAndCancelRow(final ProfileButtonType confirm, final ProfileButtonType cancel, final String cid) {
        return List.of(cancelButton(cancel, cid), confirmButton(confirm, cid));
    }
}
