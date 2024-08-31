package com.oneliferp.cwu.commands.modules.session.utils;

import com.oneliferp.cwu.commands.modules.profile.models.ProfileModel;
import com.oneliferp.cwu.commands.modules.session.misc.CitizenType;
import com.oneliferp.cwu.commands.modules.session.misc.SessionType;
import com.oneliferp.cwu.commands.modules.session.misc.ZoneType;
import com.oneliferp.cwu.commands.modules.session.misc.actions.SessionButtonType;
import com.oneliferp.cwu.commands.modules.session.misc.actions.SessionMenuType;
import com.oneliferp.cwu.commands.modules.session.misc.actions.SessionPageType;
import com.oneliferp.cwu.commands.modules.session.models.SessionModel;
import com.oneliferp.cwu.database.ProfileDatabase;
import com.oneliferp.cwu.database.SessionDatabase;
import com.oneliferp.cwu.misc.IActionType;
import com.oneliferp.cwu.models.CitizenIdentityModel;
import com.oneliferp.cwu.utils.EmbedUtils;
import com.oneliferp.cwu.utils.EmojiUtils;
import com.oneliferp.cwu.utils.SimpleDate;
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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SessionBuilderUtils {
    /* Messages */
    public static MessageEmbed historyMessage(final Collection<SessionModel> sessions, final int pageIndex, final int maxIndex) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(String.format("Historique des sessions | Page %d sur %d", pageIndex, maxIndex == 0 ? maxIndex + 1 : maxIndex));

        final StringBuilder sb = new StringBuilder();
        sb.append("""
                Cette interface recense toutes les sessions de travail.
                Pour toutes actions, munissez vous de l'ID uniquement.
                """).append("\n");

        if (maxIndex == 0) sb.append("`Aucune session n'a été trouvé.`");
        sessions.forEach(r -> sb.append(r.getDescriptionFormat()).append("\n\n"));

        embed.setDescription(sb.toString());
        return embed.build();
    }

    public static MessageEmbed overviewMessage(final Collection<SessionModel> sessions) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Statistiques de la semaine | Sessions");

        final int totalSessionCount = sessions.size();
        final int totalEarnings = SessionDatabase.resolveEarnings(sessions);

        final StringBuilder sb = new StringBuilder();
        sb.append("""
                Cette interface vous permet d'accéder aux statistiques de la semaine.
                Pour toutes actions, munissez vous de l'ID uniquement.
                """).append("\n");

        sb.append("**Productivité commune :**").append("\n");
        sb.append(String.format("Total de sessions: %d", totalSessionCount)).append("\n");
        sb.append(String.format("Revenus générés: %d tokens", totalEarnings)).append("\n");
        sb.append(String.format("Paies distribuées: %d tokens", SessionDatabase.resolveWages(sessions))).append("\n\n");

        sb.append("**Productivité respective :**").append("\n");
        ProfileDatabase.get().getAsGroupAndOrder().forEach((branch, profiles) -> {
            sb.append(String.format("%s  **Branche %s (%s)** ", branch.getEmoji(), branch.name(), branch.getMeaning())).append("\n");

            final var identities = profiles.stream()
                    .map(ProfileModel::getIdentity)
                    .collect(Collectors.toSet());
            final var branchSessions = sessions.stream()
                    .filter(s -> identities.contains(s.getEmployee()))
                    .toList();

            final int branchEarnings = SessionDatabase.resolveEarnings(branchSessions);
            final int branchSessionCount = branchSessions.size();
            sb.append(String.format("Sessions : %d **(%.2f%%)**", branchSessions.size(), Toolbox.resolveDistribution(branchSessionCount, totalSessionCount))).append("\n");
            sb.append(String.format("Revenus générés: %d tokens **(%.2f%%)**", branchEarnings, Toolbox.resolveDistribution(branchEarnings, totalEarnings))).append("\n\n");
        });

        {
            final SessionModel latest = SessionDatabase.get().resolveLatest();
            sb.append("**Dernière session :**").append("\n");
            if (latest == null || !latest.getPeriod().getEndedAt().isWithinWeek()) {
                sb.append("`Aucune`").append("\n\n");
            } else {
                sb.append(latest.getDescriptionFormat()).append("\n");
            }
        }
        embed.setDescription(sb.toString());
        return embed.build();
    }

    public static MessageEmbed initMessage(final SessionType type) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        final String title = type == SessionType.UNKNOWN ? "\uD83D\uDCBC  Session de travail" :
                String.format("%s  Session - %s", type.getEmoji(), type.getLabel());

        embed.setTitle(title);
        embed.setDescription("""
                Cette interface vous permet d'enregistrer une session de travail.
                                
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

    public static MessageEmbed previewMessage(final SessionModel session) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(buildPreviewTitle(session));
        embed.addField(zoneField(session.getZone()));

        {
            final var citizens = session.getCitizensWithType(CitizenType.LOYALIST);
            if (!citizens.isEmpty()) {
                embed.addField(participantField(citizens, SessionPageType.LOYALISTS.getDescription()));
            }
        }
        {
            final var citizens = session.getCitizensWithType(CitizenType.CIVILIAN);
            if (!citizens.isEmpty()) {
                embed.addField(participantField(citizens, SessionPageType.CIVILIANS.getDescription()));
            }
        }
        {
            final var citizens = session.getCitizensWithType(CitizenType.VORTIGAUNT);
            if (!citizens.isEmpty()) {
                embed.addField(participantField(citizens, SessionPageType.VORTIGAUNTS.getDescription()));
            }
        }
        {
            final var citizens = session.getCitizensWithType(CitizenType.ANTI_CITIZEN);
            if (!citizens.isEmpty()) {
                embed.addField(participantField(citizens, SessionPageType.ANTI_CITIZENS.getDescription()));
            }
        }

        if (session.hasPage(SessionPageType.TOKENS)) {
            embed.addField(tokenField(session.getIncome().getEarnings()));
        }

        embed.addField(infoField(session.getInfo()));
        return embed.build();
    }

    public static MessageEmbed submitMessage(final SessionModel session) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(buildEndingTitle(session));

        String sb = String.format("""
                        La session a été enregistrée avec succès.
                                        
                        Somme à **déposer** dans la caisse commune: **%s Tokens**.
                        Somme à **verser** aux participants: **%s Tokens**.
                        """, Math.max(session.getIncome().getDeposit(), 0),
                session.getIncome().getWages());
        embed.setDescription(sb);
        return embed.build();
    }

    public static MessageEmbed displayMessage(final SessionModel session) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Informations de la session");

        embed.setDescription(session.getDisplayFormat() + "\n");
        return embed.build();
    }

    public static MessageEmbed deleteMessage(final SessionModel session) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("\uD83D\uDDD1 Action en cours - Suppression d'une session de travail");

        String sb = "Afin de terminer la procédure, veuillez confirmer votre choix." + "\n\n" +
                    "**Aperçu de la session concernée :**" + "\n" +
                    session.getDescriptionFormat() + "\n";
        embed.setDescription(sb);
        return embed.build();
    }

    /* Fields */
    private static MessageEmbed.Field participantField(final List<CitizenIdentityModel> identities, final String description) {
        final boolean hasValue = !identities.isEmpty();

        final String name = String.format("%s  %s", EmojiUtils.getGreenOrYellowCircle(hasValue), description);
        final String value = String.format("%s", hasValue ? Toolbox.flatten(identities) : "Aucun.");
        return new MessageEmbed.Field(name, value, false);
    }

    public static MessageEmbed participantMessage(final SessionModel session) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(buildStepTitle(session));

        final var participants = session.getCitizensWithType(CitizenType.fromPage(session.getCurrentPage()));
        embed.addField(participantField(participants, session.getCurrentPage().getDescription()));

        return embed.build();
    }

    private static MessageEmbed.Field infoField(final String info) {
        final String name = String.format("%s  %s", EmojiUtils.getGreenCircle(), SessionPageType.INFO.getDescription());
        final String value = info != null ? info : "Rien à signaler";
        return new MessageEmbed.Field(name, value, false);
    }

    public static MessageEmbed infoMessage(final SessionModel session) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(buildStepTitle(session));
        embed.addField(infoField(session.getInfo()));
        return embed.build();
    }

    private static MessageEmbed.Field zoneField(final ZoneType zone) {
        final boolean hasValue = zone != ZoneType.UNKNOWN;

        final String name = String.format("%s  %s", EmojiUtils.getGreenOrRedCircle(hasValue), SessionPageType.ZONE.getDescription());
        final String value = hasValue ? zone.getLabel() : "`Information manquante`";
        return new MessageEmbed.Field(name, value, false);
    }

    public static MessageEmbed zoneMessage(final SessionModel session) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(buildStepTitle(session));
        embed.addField(zoneField(session.getZone()));
        return embed.build();
    }

    private static MessageEmbed.Field tokenField(final Integer token) {
        final boolean hasValue = token != null;

        final String name = String.format("%s  %s", EmojiUtils.getGreenOrRedCircle(hasValue), SessionPageType.TOKENS.getDescription());
        final String value = String.format("%s", (hasValue ? token : 0) + " Tokens");
        return new MessageEmbed.Field(name, value, false);
    }

    public static MessageEmbed earningsMessage(final SessionModel session) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(buildStepTitle(session));
        embed.addField(tokenField(session.getIncome().getEarnings()));
        return embed.build();
    }

    /* Menus */
    private static StringSelectMenu zoneMenu(final String cid, final ZoneType type) {
        final var menu = StringSelectMenu.create(SessionMenuType.SELECT_ZONE.build(cid));
        final var options = Arrays.stream(ZoneType.values()).skip(1)
                .map(v -> SelectOption.of(v.getLabel(), v.name())).toList();
        options.forEach(menu::addOptions);

        if (type != ZoneType.UNKNOWN) Toolbox.setDefaultMenuOption(menu, options, type.name());
        return menu.build();
    }

    private static StringSelectMenu typeMenu(final String cid, final SessionType type) {
        final var menu = StringSelectMenu.create(SessionMenuType.SELECT_TYPE.build(cid));
        final var options = Arrays.stream(SessionType.values()).skip(1)
                .map(v -> SelectOption.of(v.getLabel(), v.name()).withEmoji(EmojiUtils.asDiscordEmoji(v.getEmoji()))).toList();
        options.forEach(menu::addOptions);

        if (type != SessionType.UNKNOWN) Toolbox.setDefaultMenuOption(menu, options, type.name());
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

    private static Button returnButton(final IActionType type, final String cid) {
        return Button.secondary(type.build(cid), "Retour en arrière");
    }

    private static Button deleteButton(final IActionType type, final String cid, final String id) {
        return Button.danger(type.build(cid, Map.of("session", id)), "Supprimer");
    }

    private static Button cancelButton(final IActionType type, final String cid, final String id) {
        return Button.primary(type.build(cid, Map.of("session", id)), "Abandonner");
    }

    private static Button confirmButton(final IActionType type, final String cid, final String id) {
        return Button.danger(type.build(cid, Map.of("session", id)), "Confirmer");
    }

    /* Components */
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

    public static LayoutComponent displayComponent(final String cid, final String id) {
        return ActionRow.of(deleteButton(SessionButtonType.DELETE, cid, id), returnButton(SessionButtonType.OVERVIEW, cid));
    }

    public static LayoutComponent deleteComponent(final String cid, final String id) {
        return ActionRow.of(cancelButton(SessionButtonType.DELETE_CANCEL, cid, id), confirmButton(SessionButtonType.DELETE_CONFIRM, cid, id));
    }

    /*  */
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
