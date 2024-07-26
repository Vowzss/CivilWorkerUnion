package com.oneliferp.cwu.modules.session.utils;

import com.oneliferp.cwu.Models.SessionModel;
import com.oneliferp.cwu.Utils.EmbedUtils;
import com.oneliferp.cwu.Utils.Toolbox;
import com.oneliferp.cwu.misc.PageType;
import com.oneliferp.cwu.misc.SessionType;
import com.oneliferp.cwu.modules.session.misc.SessionButtonType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

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

    public static MessageEmbed cancelMessage(final SessionType sessionType) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(String.format("%s  Session - %s", sessionType.getEmoji(), sessionType.getLabel()));
        embed.setDescription("Vous avez annuler votre session de travail.");
        return embed.build();
    }

    public static MessageEmbed participantMessage(final SessionType sessionType, final PageType pageType, final Map<String, String> participants) {
        final String value = participants.isEmpty() ? "Aucun" : Toolbox.flatten(participants);

        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(String.format("%s  Session - %s | Étape %d/%d", sessionType.getEmoji(), sessionType.getLabel(), pageType.ordinal() + 1, PageType.values().length - 1));
        embed.addField(new MessageEmbed.Field(pageType.getDescription(), value, false));
        return embed.build();
    }

    public static MessageEmbed singleMessage(final SessionType sessionType, final PageType pageType, final AbstractMap.SimpleEntry<String, String> entry) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(String.format("%s  Session - %s | Étape %d/%d", sessionType.getEmoji(), sessionType.getLabel(), pageType.ordinal() + 1, PageType.values().length -1));
        if (entry != null) {
            final String value = String.format("%s %s", entry.getKey(), entry.getValue());
            embed.addField(new MessageEmbed.Field(pageType.getDescription(), value, false));
        }
        return embed.build();
    }

    public static MessageEmbed previewMessage(final SessionModel session) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(String.format("%s  Session - %s | Apperçu", session.getType().getEmoji(), session.getType().getLabel()));
        embed.addField(new Field("Type de session:", session.getType().getLabel(), false));
        embed.addField(new Field("Emplacement de session:", session.getZone().getLabel(), false));
        {
            final var participants = session.getLoyalists();
            final String value = participants.isEmpty() ? "Aucun": Toolbox.flatten(participants);
            embed.addField(new Field(PageType.LOYALISTS.getDescription(), value, false));
        }
        {
            final var participants = session.getCitizens();
            final String value = participants.isEmpty() ? "Aucun": Toolbox.flatten(participants);
            embed.addField(new Field(PageType.CITIZENS.getDescription(), value, false));
        }
        {
            final var participants = session.getVortigaunts();
            final String value = participants.isEmpty() ? "Aucun": Toolbox.flatten(participants);
            embed.addField(new Field(PageType.VORTIGAUNTS.getDescription(), value, false));
        }
        {
            final var participants = session.getAntiCitizens();
            final String value = participants.isEmpty() ? "Aucun": Toolbox.flatten(participants);
            embed.addField(new Field(PageType.ANTI_CITIZENS.getDescription(), value, false));
        }
        {
            final var info = session.getInfo();
            final String value = info.isBlank() ? "Rien à signaler": info;
            embed.addField(new Field(PageType.CITIZENS.getDescription(), value, false));
        }
        embed.addField(new Field(PageType.EARNINGS.getDescription(), session.getEarnings().toString() + " tokens", false));
        return embed.build();
    }

    /*
    Buttons
    */
    private static Button confirmButton(final String cid) {
        return Button.primary(SessionButtonType.PREVIEW.getId() + String.format(":%s", cid), "Apperçu");
    }
    private static Button editButton(final String cid) {
        return Button.secondary(SessionButtonType.EDIT.getId() + String.format(":%s", cid), "Modifier");
    }
    private static Button startButton(final String cid, final SessionType sessionType) {
        return Button.primary(SessionButtonType.START.getId() + String.format(".%s:%s", ":" + sessionType.name().toLowerCase(), cid), "Commencer");
    }
    private static Button cancelButton(final String cid) {
        return Button.danger(SessionButtonType.CANCEL.getId() + String.format(":%s", cid), "Abandonner");
    }
    private static Button clearButton(final String cid) {
        return Button.danger(SessionButtonType.CLEAR.getId() + String.format(":%s", cid), "Effacer");
    }
    private static Button submitButton(final String cid) {
        return Button.primary(SessionButtonType.SUBMIT.getId() + String.format(":%s", cid), "Envoyer");
    }
    private static Button nextButton(final String cid) {
        return Button.secondary(SessionButtonType.PAGE.getId() + String.format(".next:%s", cid), Emoji.fromUnicode("\u27A1"));
    }
    private static Button prevButton(final String cid) {
        return Button.secondary(SessionButtonType.PAGE.getId() + String.format(".prev:%s", cid), Emoji.fromUnicode("\u2B05"));
    }
    private static Button fillButton(final String cid) {
        return Button.primary(SessionButtonType.FILL.getId() + String.format(":%s", cid), "Remplir");
    }

    /*
    Utils
    */
    public static List<Button> startAndCancelRow(final String cid, final SessionType sessionType) {
        return List.of(startButton(cid, sessionType), cancelButton(cid));
    }
    public static List<Button> nextRow(final String cid) {
        return List.of(fillButton(cid), clearButton(cid), nextButton(cid));
    }
    public static List<Button> prevRow(final String cid) {
        return List.of(fillButton(cid),confirmButton(cid), clearButton(cid), prevButton(cid));
    }
    public static List<Button> submitOrEditRow(final String cid) {
        return List.of(submitButton(cid), editButton(cid));
    }
    public static List<Button> prevAndNextRow(final String cid) {
        return List.of(fillButton(cid),clearButton(cid), prevButton(cid), nextButton(cid));
    }
}
