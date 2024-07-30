package com.oneliferp.cwu.modules.session.utils;

import com.oneliferp.cwu.models.SessionModel;
import com.oneliferp.cwu.utils.EmbedUtils;
import com.oneliferp.cwu.utils.Toolbox;
import com.oneliferp.cwu.misc.PageType;
import com.oneliferp.cwu.misc.ParticipantType;
import com.oneliferp.cwu.misc.SessionType;
import com.oneliferp.cwu.modules.session.misc.SessionButtonType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.List;

public class SessionBuilderUtils {
    /*
    Messages
    */
    public static MessageEmbed startMessage(final SessionType sessionType) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(String.format("%s  Session - %s", sessionType.getEmoji(), sessionType.getLabel()));
        embed.setDescription("Vous allez procéder au remplissage des informations.\nS'ensuivra un résumé de votre session.");
        return embed.build();
    }

    public static MessageEmbed ongoingMessage(final SessionType sessionType) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(String.format("%s  Session - %s", sessionType.getEmoji(), sessionType.getLabel()));
        embed.setDescription("Vous avez une session de travail en cours.\nSouhaitez-vous reprendre le rapport ?");
        return embed.build();
    }

    public static MessageEmbed participantMessage(final SessionModel session) {
        final SessionType sessionType = session.getType();
        final PageType pageType = session.currentPage;
        final var value = session.getParticipants(ParticipantType.fromPage(pageType));

        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(String.format("%s  Session - %s | Étape %d/%d", sessionType.getEmoji(), sessionType.getLabel(), pageType.ordinal() + 1, PageType.values().length - 1));
        embed.addField(new MessageEmbed.Field(pageType.getDescription(), value.isEmpty() ? "Aucun." : Toolbox.flatten(value), false));
        return embed.build();
    }

    public static MessageEmbed infoMessage(final SessionModel session) {
        final SessionType sessionType = session.getType();
        final PageType pageType = session.currentPage;
        final var value = session.getInfo();

        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(String.format("%s  Session - %s | Étape %d/%d", sessionType.getEmoji(), sessionType.getLabel(), pageType.ordinal() + 1, PageType.values().length -1));
        embed.addField(new MessageEmbed.Field(pageType.getDescription(), value == null ? "Rien à signaler." : value, false));

        return embed.build();
    }

    public static MessageEmbed earningsMessage(final SessionModel session) {
        final SessionType sessionType = session.getType();
        final PageType pageType = session.currentPage;
        final var value = session.getIncome().getEarnings();

        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(String.format("%s  Session - %s | Étape %d/%d", sessionType.getEmoji(), sessionType.getLabel(), pageType.ordinal() + 1, PageType.values().length -1));
        embed.addField(new MessageEmbed.Field(pageType.getDescription(), String.format("%s %s", value == null ? 0 : value, "Tokens"), false));

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
            embed.addField(new Field(PageType.LOYALISTS.getDescription(), value, false));
        }
        {
            final var participants = session.getParticipants(ParticipantType.CITIZEN);
            final String value = participants.isEmpty() ? "Aucun." : Toolbox.flatten(participants);
            embed.addField(new Field(PageType.CITIZENS.getDescription(), value, false));
        }
        {
            final var participants = session.getParticipants(ParticipantType.VORTIGAUNT);
            final String value = participants.isEmpty() ? "Aucun." : Toolbox.flatten(participants);
            embed.addField(new Field(PageType.VORTIGAUNTS.getDescription(), value, false));
        }
        {
            final var participants = session.getParticipants(ParticipantType.ANTI_CITIZEN);
            final String value = participants.isEmpty() ? "Aucun." : Toolbox.flatten(participants);
            embed.addField(new Field(PageType.ANTI_CITIZENS.getDescription(), value, false));
        }
        {
            final var info = session.getInfo();
            final String value = info == null ? "Rien à signaler." : info;
            embed.addField(new Field(PageType.INFO.getDescription(), value, false));
        }
        {
            final var earnings = session.getIncome().getEarnings();
            final Integer value = earnings == null ? 0 : earnings;
            embed.addField(new Field(PageType.EARNINGS.getDescription(), String.format("%d Tokens.", value), false));
        }

        return embed.build();
    }

    public static MessageEmbed submitMessage(final SessionModel session) {
        final SessionType sessionType = session.getType();

        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(String.format("%s  Session - %s | Feedback", sessionType.getEmoji(), sessionType.getLabel()));
        embed.setDescription("✔️ Le rapport à été transmit avec succès.");

        final int deposit = session.getIncome().getDeposit();
        embed.addField(new Field(String.format("Somme à **%s** la caisse commune:", deposit < 0 ? "prendre depuis" : "déposer dans"), String.format("%d tokens.", deposit), false));
        embed.addField(new Field("Somme à **verser** aux participants:", String.format("%d Tokens.", session.getIncome().getWages()), false));

        embed.addBlankField(false);
        embed.addField(new Field("Nombre de rapport soumis cette semaine:", String.format("%d Rapports.", session.resolveCwu().getWeeklySessionCount()), false));

        return embed.build();
    }

    /*
    Buttons
    */
    private static Button previewButton(final String cid) {
        return Button.primary(SessionButtonType.PREVIEW.build(cid), "Apperçu");
    }
    private static Button resumeButton(final String cid) {
        return Button.primary(SessionButtonType.PREVIEW.build(cid), "Reprendre");
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
    public static List<Button> startAndCancelRow(final String cid) {
        return List.of(startButton(cid), cancelButton(cid));
    }
    public static List<Button> resumeOrCancelRow(final String cid) {
        return List.of(resumeButton(cid), cancelButton(cid));
    }
    public static List<Button> nextRow(final String cid) {
        return List.of(fillButton(cid), clearButton(cid), nextButton(cid));
    }
    public static List<Button> prevRow(final String cid) {
        return List.of(fillButton(cid), previewButton(cid), clearButton(cid), prevButton(cid));
    }
    public static List<Button> submitOrEditRow(final String cid) {
        return List.of(submitButton(cid), editButton(cid));
    }
    public static List<Button> prevAndNextRow(final String cid) {
        return List.of(fillButton(cid),clearButton(cid), prevButton(cid), nextButton(cid));
    }
    public static List<Button> pageRow(final SessionModel session) {
        final String cid = session.getManagerCid();

        if (session.currentPage == PageType.PREVIEW) {
            return SessionBuilderUtils.submitOrEditRow(cid);
        }

        return session.isFirstPage() ? SessionBuilderUtils.nextRow(cid) :
                session.isLastPage() ? SessionBuilderUtils.prevRow(cid) : SessionBuilderUtils.prevAndNextRow(cid);
    }
}
