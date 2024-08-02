package com.oneliferp.cwu.modules.session.utils;

import com.oneliferp.cwu.modules.session.misc.SessionPageType;
import com.oneliferp.cwu.misc.ParticipantType;
import com.oneliferp.cwu.misc.SessionType;
import com.oneliferp.cwu.misc.ZoneType;
import com.oneliferp.cwu.models.SessionModel;
import com.oneliferp.cwu.modules.session.misc.SessionButtonType;
import com.oneliferp.cwu.modules.session.misc.SessionMenuType;
import com.oneliferp.cwu.utils.EmbedUtils;
import com.oneliferp.cwu.utils.SimpleDate;
import com.oneliferp.cwu.utils.Toolbox;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SessionBuilderUtils {
    /*
    Messages
    */
    public static MessageEmbed startMessage(final SessionType type) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(String.format("%s  Session - %s", type.getEmoji(), type.getLabel()));
        embed.setDescription("Vous allez procéder au remplissage des informations.\nS'ensuivra un résumé de votre session.");
        return embed.build();
    }

    public static MessageEmbed ongoingMessage(final SessionModel ongoingSession, final SessionType newSessionType) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Vous êtes sur le point d'écraser votre ancienne session.\n");
        sb.append("Souhaitez-vous reprendre ou écraser la session ?\n");
        sb.append("\n");
        {
            final SessionType ongoingSessionType = ongoingSession.getType();
            sb.append(String.format("**Ancienne session:** %s %s\n", ongoingSessionType.getLabel(), ongoingSessionType.getEmoji()));
        }
        {
            final SimpleDate startedAt = ongoingSession.getPeriod().getStartedAt();
            sb.append(String.format("**Débutée le:** %s, à %s.\n", startedAt.getDate(), startedAt.getTime()));
        }
        sb.append("\n");
        sb.append(String.format("**Nouvelle session:** %s %s\n", newSessionType.getLabel(), newSessionType.getEmoji()));

        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Vous avez une session de travail en cours!");
        embed.setDescription(sb.toString());
        return embed.build();
    }

    public static MessageEmbed participantMessage(final SessionModel session) {
        final SessionType type = session.getType();
        final SessionPageType page = session.getCurrentPage();
        final var value = session.getParticipants(ParticipantType.fromPage(page));

        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(String.format("%s  Session - %s | Étape %d/%d", type.getEmoji(), type.getLabel(), session.getCurrentStep(), session.getMaxSteps()));
        embed.addField(new MessageEmbed.Field(page.getDescription(), value.isEmpty() ? "Aucun." : Toolbox.flatten(value), false));
        return embed.build();
    }

    public static MessageEmbed infoMessage(final SessionModel session) {
        final SessionType type = session.getType();
        final SessionPageType page = session.getCurrentPage();
        final var value = session.getInfo();

        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(String.format("%s  Session - %s | Étape %d/%d", type.getEmoji(), type.getLabel(), session.getCurrentStep(), session.getMaxSteps()));
        embed.addField(new MessageEmbed.Field(page.getDescription(), value == null ? "Rien à signaler." : value, false));

        return embed.build();
    }

    public static MessageEmbed earningsMessage(final SessionModel session) {
        final SessionType type = session.getType();
        final SessionPageType page = session.getCurrentPage();
        final var value = session.getIncome().getEarnings();

        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(String.format("%s  Session - %s | Étape %d/%d", type.getEmoji(), type.getLabel(), session.getCurrentStep(), session.getMaxSteps()));
        embed.addField(new MessageEmbed.Field(page.getDescription(), String.format("%s %s", value == null ? 0 : value, "Tokens"), false));

        return embed.build();
    }

    public static MessageEmbed previewMessage(final SessionModel session) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(String.format("%s  Session - %s | Apperçu", session.getType().getEmoji(), session.getType().getLabel()));
        embed.addField(new Field("Type de session:", session.getType().getLabel(), false));
        embed.addField(new Field("Emplacement de session:", session.getZone().getLabel(), false));
        {
            final var participants = session.getParticipants(ParticipantType.LOYALIST);
            final String value = participants.isEmpty() ? "Aucun." : Toolbox.flatten(participants);
            embed.addField(new Field(SessionPageType.LOYALISTS.getDescription(), value, false));
        }
        {
            final var participants = session.getParticipants(ParticipantType.CITIZEN);
            final String value = participants.isEmpty() ? "Aucun." : Toolbox.flatten(participants);
            embed.addField(new Field(SessionPageType.CITIZENS.getDescription(), value, false));
        }
        {
            final var participants = session.getParticipants(ParticipantType.VORTIGAUNT);
            final String value = participants.isEmpty() ? "Aucun." : Toolbox.flatten(participants);
            embed.addField(new Field(SessionPageType.VORTIGAUNTS.getDescription(), value, false));
        }
        {
            final var participants = session.getParticipants(ParticipantType.ANTI_CITIZEN);
            final String value = participants.isEmpty() ? "Aucun." : Toolbox.flatten(participants);
            embed.addField(new Field(SessionPageType.ANTI_CITIZENS.getDescription(), value, false));
        }
        {
            final var info = session.getInfo();
            final String value = info == null ? "Rien à signaler." : info;
            embed.addField(new Field(SessionPageType.INFO.getDescription(), value, false));
        }
        {
            final var earnings = session.getIncome().getEarnings();
            final Integer value = earnings == null ? 0 : earnings;
            embed.addField(new Field(SessionPageType.EARNINGS.getDescription(), String.format("%d Tokens.", value), false));
        }

        return embed.build();
    }

    public static MessageEmbed submitMessage(final SessionModel session) {
        final SessionType sessionType = session.getType();

        final StringBuilder sb = new StringBuilder();
        sb.append("Le rapport à été transmit avec succès.\n");
        sb.append("\n");
        sb.append(String.format("Somme à **déposer** dans la caisse commune: **%s Tokens**.\n", Math.max(session.getIncome().getDeposit(), 0)));
        sb.append(String.format("Somme à **verser** aux participants: **%s Tokens**.\n", session.getIncome().getWages()));
        sb.append("\n");
        sb.append(String.format("Nombre de **rapport soumis** cette semaine: %d Rapports.\n", session.resolveCwu().getWeeklySessionCount()));

        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(String.format("%s  Session - %s | Feedback", sessionType.getEmoji(), sessionType.getLabel()));
        embed.setDescription(sb.toString());
        return embed.build();
    }

    public static MessageEmbed zoneMessage(SessionModel session) {
        final SessionType type = session.getType();
        final SessionPageType page = session.getCurrentPage();
        final var value = session.getZone();

        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(String.format("%s  Session - %s | Étape %d/%d", type.getEmoji(), type.getLabel(), session.getCurrentStep(), session.getMaxSteps()));
        embed.addField(new MessageEmbed.Field(page.getDescription(), value == ZoneType.UNKNOWN ? "Aucun." : value.getLabel(), false));

        return embed.build();
    }

    public static StringSelectMenu locationMenu(final String cid, final ZoneType zone) {
        final var menu = StringSelectMenu.create(SessionMenuType.FILL_ZONE.build(cid));
        final var options = Arrays.stream(ZoneType.values()).skip(1).map(type -> SelectOption.of(type.getLabel(), type.name())).toList();
        options.forEach(menu::addOptions);

        if (zone != ZoneType.UNKNOWN) {
            menu.setDefaultOptions(options.stream().filter(o -> o.getValue().equals(zone.name())).findFirst().get());
        }

        return menu.build();
    }

    /*
    Buttons
    */
    private static Button previewButton(final String cid) {
        return Button.primary(SessionButtonType.PREVIEW.build(cid), "Apperçu");
    }

    private static Button resumeButton(final String cid) {
        return Button.primary(SessionButtonType.RESUME.build(cid), "Reprendre");
    }

    private static Button overwriteButton(final String cid, final SessionType sessionType) {
        return Button.danger(SessionButtonType.REPLACE.build(cid, Map.of("session", sessionType.name())), "Écraser");
    }

    private static Button editButton(final String cid) {
        return Button.secondary(SessionButtonType.EDIT.build(cid), "Modifier");
    }

    private static Button startButton(final String cid) {
        return Button.primary(SessionButtonType.START.build(cid), "Commencer");
    }

    private static Button cancelButton(final String cid) {
        return Button.danger(SessionButtonType.CANCEL.build(cid), "Abandonner");
    }

    private static Button clearButton(final String cid) {
        return Button.danger(SessionButtonType.CLEAR.build(cid), "Effacer");
    }

    private static Button submitButton(final String cid) {
        return Button.primary(SessionButtonType.SUBMIT.build(cid), "Envoyer");
    }

    private static Button nextButton(final String cid) {
        return Button.secondary(SessionButtonType.PAGE_NEXT.build(cid), Emoji.fromUnicode("\u27A1"));
    }

    private static Button prevButton(final String cid) {
        return Button.secondary(SessionButtonType.PAGE_PREV.build(cid), Emoji.fromUnicode("\u2B05"));
    }

    private static Button fillButton(final String cid) {
        return Button.primary(SessionButtonType.FILL.build(cid), "Remplir");
    }

    /*
    Utils
    */
    public static ActionRow startAndCancelRow(final String cid) {
        return ActionRow.of(startButton(cid), cancelButton(cid));
    }

    public static ActionRow resumeOrOverwriteRow(final String cid, final SessionType sessionType) {
        return ActionRow.of(resumeButton(cid), overwriteButton(cid, sessionType));
    }

    private static ActionRow nextRow(final String cid) {
        return ActionRow.of(fillButton(cid), clearButton(cid), nextButton(cid));
    }

    private static ActionRow prevRow(final String cid) {
        return ActionRow.of(fillButton(cid), previewButton(cid), clearButton(cid), prevButton(cid));
    }

    private static ActionRow submitOrEditRow(final String cid) {
        return ActionRow.of(submitButton(cid), editButton(cid));
    }

    private static ActionRow prevAndNextRow(final String cid) {
        return ActionRow.of(fillButton(cid), clearButton(cid), prevButton(cid), nextButton(cid));
    }

    public static List<ActionRow> zoneRows(final SessionModel session) {
        final String cid = session.getManagerCid();

        return List.of(
            ActionRow.of(locationMenu(cid, session.getZone())),
            ActionRow.of(clearButton(cid), nextButton(cid))
        );
    }

    public static ActionRow pageRow(final SessionModel session) {
        final String cid = session.getManagerCid();

        if (session.getCurrentPage() == SessionPageType.PREVIEW) {
            return SessionBuilderUtils.submitOrEditRow(cid);
        }

        return session.isFirstPage() ? nextRow(cid) : session.isLastPage() ? prevRow(cid) : prevAndNextRow(cid);
    }

}
