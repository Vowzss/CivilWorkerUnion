package com.oneliferp.cwu.commands.modules.session.utils;

import com.oneliferp.cwu.commands.modules.session.misc.ParticipantType;
import com.oneliferp.cwu.commands.modules.session.misc.SessionType;
import com.oneliferp.cwu.commands.modules.session.misc.ZoneType;
import com.oneliferp.cwu.commands.modules.session.misc.actions.SessionButtonType;
import com.oneliferp.cwu.commands.modules.session.misc.actions.SessionMenuType;
import com.oneliferp.cwu.commands.modules.session.misc.actions.SessionPageType;
import com.oneliferp.cwu.commands.modules.session.models.SessionModel;
import com.oneliferp.cwu.utils.EmbedUtils;
import com.oneliferp.cwu.utils.EmojiUtils;
import com.oneliferp.cwu.utils.SimpleDate;
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

public class SessionBuilderUtils {
    /* Embeds */
    public static MessageEmbed initMessage(final SessionType type) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        final String title = type == SessionType.UNKNOWN ? "\uD83D\uDCBC  Session de travail" :
                String.format("%s  Session - %s", type.getEmoji(), type.getLabel());

        embed.setTitle(title);
        embed.setDescription("""
                Cette interface vous permet d'enregistrer une Session de travail.
                                
                Vous allez procéder au remplissage des informations.
                S'ensuivra un résumé de votre session."""
        );
        return embed.build();
    }

    public static MessageEmbed existMessage(final SessionModel session) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Vous avez une session en cours!");

        final SessionType type = session.getType();
        final SimpleDate startedAt = session.getPeriod().getStartedAt();

        embed.setDescription(String.format("""
                Vous ne pouvez faire qu'une session en simultané.
                Souhaitez-vous reprendre ou écraser la session en cours ?
                
                **Session:** %s %s
                **Débutée le:** %s, à %s.""",
                type.getLabel(), type.getEmoji(),
                startedAt.getDate(), startedAt.getTime())
        );
        return embed.build();
    }

    public static MessageEmbed participantMessage(final SessionModel session) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(buildStepTitle(session));

        {
            final var participants = session.getParticipants(ParticipantType.fromPage(session.getCurrentPage()));

            final String name = String.format("%s  %s", EmojiUtils.getGreenOrYellowCircle(session.hasParticipants()), session.getCurrentPage().getDescription());
            final String value = String.format("%s", !participants.isEmpty() ? Toolbox.flatten(participants) : "Aucun.");
            embed.addField(new MessageEmbed.Field(name, value, false));
        }

        return embed.build();
    }

    public static MessageEmbed infoMessage(final SessionModel session) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(buildStepTitle(session));

        {
            final var info = session.getInfo();

            final String name = String.format("%s  %s", EmojiUtils.getGreenCircle(), SessionPageType.INFO.getDescription());
            final String value = info != null ? info : "Rien à signaler";
            embed.addField(new MessageEmbed.Field(name, value, false));
        }

        return embed.build();
    }

    public static MessageEmbed zoneMessage(final SessionModel session) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(buildStepTitle(session));

        {
            final var zone = session.getZone();
            final boolean hasValue = zone != ZoneType.UNKNOWN;

            final String name = String.format("%s  %s", EmojiUtils.getGreenOrRedCircle(hasValue), SessionPageType.ZONE.getDescription());
            final String value = hasValue ? zone.getLabel() : "`Information manquante`";
            embed.addField(new MessageEmbed.Field(name, value, false));
        }

        return embed.build();
    }

    public static MessageEmbed earningsMessage(final SessionModel session) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(buildStepTitle(session));

        {
            final var earnings = session.getIncome().getEarnings();
            final boolean hasValue = earnings != null;

            final String name = String.format("%s  %s", EmojiUtils.getGreenOrRedCircle(hasValue), SessionPageType.TOKENS.getDescription());
            final String value = String.format("%s", (hasValue ? earnings : 0) + " Tokens");
            embed.addField(new MessageEmbed.Field(name, value, false));
        }

        return embed.build();
    }

    public static MessageEmbed previewMessage(final SessionModel session) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(buildPreviewTitle(session));
        {
            final var zone = session.getZone();
            final boolean hasValue = zone != ZoneType.UNKNOWN;

            final String name = String.format("%s  %s", EmojiUtils.getGreenOrRedCircle(hasValue), SessionPageType.ZONE.getDescription());
            final String value = hasValue ? zone.getLabel() : "`Information manquante`";
            embed.addField(new MessageEmbed.Field(name, value, false));
        }

        final boolean hasParticipants = session.hasParticipants();
        final String participantStatus = EmojiUtils.getGreenOrYellowCircle(hasParticipants);
        {
            final var participants = session.getParticipants(ParticipantType.LOYALIST);

            final String name = String.format("%s  %s", participantStatus, SessionPageType.LOYALISTS.getDescription());
            final String value = String.format("%s", !participants.isEmpty() ? Toolbox.flatten(participants) : "Aucun.");
            embed.addField(new MessageEmbed.Field(name, value, false));
        }
        {
            final var participants = session.getParticipants(ParticipantType.CITIZEN);

            final String name = String.format("%s  %s", participantStatus, SessionPageType.CITIZENS.getDescription());
            final String value = String.format("%s", !participants.isEmpty() ? Toolbox.flatten(participants) : "Aucun.");
            embed.addField(new MessageEmbed.Field(name, value, false));
        }
        {
            final var participants = session.getParticipants(ParticipantType.VORTIGAUNT);

            final String name = String.format("%s  %s", participantStatus, SessionPageType.VORTIGAUNTS.getDescription());
            final String value = String.format("%s", !participants.isEmpty() ? Toolbox.flatten(participants) : "Aucun.");
            embed.addField(new MessageEmbed.Field(name, value, false));
        }
        {
            final var participants = session.getParticipants(ParticipantType.ANTI_CITIZEN);

            final String name = String.format("%s  %s", participantStatus, SessionPageType.ANTI_CITIZENS.getDescription());
            final String value = String.format("%s", !participants.isEmpty() ? Toolbox.flatten(participants) : "Aucun.");
            embed.addField(new MessageEmbed.Field(name, value, false));
        }
        if (session.hasPage(SessionPageType.TOKENS)) {
            final var earnings = session.getIncome().getEarnings();
            final boolean hasValue = earnings != null;

            final String name = String.format("%s  %s", EmojiUtils.getGreenOrRedCircle(hasValue), SessionPageType.TOKENS.getDescription());
            final String value = String.format("%s", (hasValue ? earnings : 0) + " Tokens");
            embed.addField(new MessageEmbed.Field(name, value, false));
        }
        {
            final var info = session.getInfo();

            final String name = String.format("%s  %s", EmojiUtils.getGreenCircle(), SessionPageType.INFO.getDescription());
            final String value = info != null ? info : "Rien à signaler";
            embed.addField(new MessageEmbed.Field(name, value, false));
        }

        return embed.build();
    }

    public static MessageEmbed submitMessage(final SessionModel session) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(buildEndingTitle(session));

        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("""
                La session a été enregistrée avec succès.
                                
                "Somme à **déposer** dans la caisse commune: **%s Tokens**."
                "Somme à **verser** aux participants: **%s Tokens**."
                """, Math.max(session.getIncome().getDeposit(), 0),
                session.getIncome().getWages())
        );
        embed.setDescription(sb.toString());
        return embed.build();
    }

    /* Menus */
    private static StringSelectMenu zoneMenu(final String cid, final ZoneType type) {
        final var menu = StringSelectMenu.create(SessionMenuType.SELECT_ZONE.build(cid));
        final var options = Arrays.stream(ZoneType.values()).skip(1)
                .map(v -> SelectOption.of(v.getLabel(), v.name())).toList();
        options.forEach(menu::addOptions);

        if (type != ZoneType.UNKNOWN) Toolbox.setDefaulMenuOption(menu, options, type.name());
        return menu.build();
    }

    private static StringSelectMenu typeMenu(final String cid, final SessionType type) {
        final var menu = StringSelectMenu.create(SessionMenuType.SELECT_TYPE.build(cid));
        final var options = Arrays.stream(SessionType.values()).skip(1)
                .map(v -> SelectOption.of(v.getLabel(), v.name())).toList();
        options.forEach(menu::addOptions);

        if (type != SessionType.UNKNOWN) Toolbox.setDefaulMenuOption(menu, options, type.name());
        return menu.build();
    }

    /* Buttons */
    private static Button previewButton(final String cid) {
        return Button.success(SessionButtonType.PREVIEW.build(cid), "Apperçu");
    }

    private static Button resumeButton(final String cid) {
        return Button.primary(SessionButtonType.RESUME.build(cid), "Reprendre");
    }

    private static Button overwriteButton(final String cid) {
        return Button.danger(SessionButtonType.OVERWRITE.build(cid), "Écraser");
    }

    private static Button editButton(final String cid) {
        return Button.secondary(SessionButtonType.EDIT.build(cid), "Modifier");
    }

    private static Button startButton(final String cid) {
        return Button.primary(SessionButtonType.BEGIN.build(cid), "Commencer");
    }

    private static Button abortButton(final String cid) {
        return Button.danger(SessionButtonType.ABORT.build(cid), "Abandonner");
    }

    private static Button clearButton(final String cid) {
        return Button.danger(SessionButtonType.CLEAR.build(cid), "Effacer");
    }

    private static Button submitButton(final String cid) {
        return Button.success(SessionButtonType.SUBMIT.build(cid), "Envoyer");
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

    public static List<ActionRow> zoneRows(final SessionModel session) {
        final String cid = session.getEmployeeCid();

        return List.of(
                ActionRow.of(zoneMenu(cid, session.getZone())),
                ActionRow.of(clearButton(cid), nextButton(cid))
        );
    }

    public static List<ActionRow> initRow(final String cid, final SessionType type) {
        return List.of(
                ActionRow.of(typeMenu(cid, type)),
                ActionRow.of(abortButton(cid))
        );
    }

    public static List<ActionRow> beginRow(final String cid, final SessionType type) {
        return List.of(
                ActionRow.of(typeMenu(cid, type)),
                ActionRow.of(abortButton(cid), startButton(cid))
        );
    }

    public static ActionRow pageRow(final SessionModel session) {
        final String cid = session.getEmployeeCid();

        if (session.getCurrentPage() == SessionPageType.PREVIEW) {
            return SessionBuilderUtils.submitOrEditRow(cid);
        }

        return session.isFirstPage() ? nextRow(cid) : session.isLastPage() ? prevRow(cid) : prevAndNextRow(cid);
    }

    /* Utils */
    public static String buildStepTitle(final SessionModel session) {
        final SessionType type = session.getType();
        return String.format("%s  Session - %s | %d/%d", type.getEmoji(), type.getLabel(), session.getCurrentStep(), session.getMaxSteps());
    }

    public static String buildPreviewTitle(final SessionModel session) {
        final SessionType type = session.getType();
        return String.format("%s  Session - %s | Apperçu", type.getEmoji(), type.getLabel());
    }

    public static String buildEndingTitle(final SessionModel session) {
        final SessionType type = session.getType();
        return String.format("%s  Session - %s | Finalisation", type.getEmoji(), type.getLabel());
    }
}
