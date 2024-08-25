package com.oneliferp.cwu.commands.modules.manage.utils;

import com.oneliferp.cwu.commands.modules.profile.misc.ProfilePageType;
import com.oneliferp.cwu.commands.modules.manage.misc.actions.*;
import com.oneliferp.cwu.commands.modules.profile.models.ProfileModel;
import com.oneliferp.cwu.commands.modules.profile.utils.ProfileBuilderUtils;
import com.oneliferp.cwu.commands.modules.session.models.SessionModel;
import com.oneliferp.cwu.database.ProfileDatabase;
import com.oneliferp.cwu.database.ReportDatabase;
import com.oneliferp.cwu.database.SessionDatabase;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.misc.CwuRank;
import com.oneliferp.cwu.misc.IActionType;
import com.oneliferp.cwu.models.IdentityModel;
import com.oneliferp.cwu.utils.EmbedUtils;
import com.oneliferp.cwu.utils.EmojiUtils;
import com.oneliferp.cwu.utils.SimpleDate;
import com.oneliferp.cwu.utils.Toolbox;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.List;
import java.util.stream.Collectors;

public class ManageBuilderUtils {
    /* Messages */
    public static MessageEmbed sessionOverviewComponent() {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Historique des sessions");

        final StringBuilder sb = new StringBuilder();
        sb.append("""
                Cette interface vous permet d'accéder aux sessions de travail.
                Pour toutes actions, munisser vous de l'ID uniquement.
                """).append("\n");

        sb.append("**Sessions récemment effectué :**").append("\n");
        SessionDatabase.get().resolveLatestLimit(5).forEach(s -> {
            sb.append(s.getDescriptionFormat()).append("\n");
        });

        embed.setDescription(sb.toString());
        return embed.build();
    }

    public static MessageEmbed sessionStatsMessage() {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Statistiques de la semaine");

        final List<SessionModel> sessions = SessionDatabase.get().resolveWithinWeek();
        final int totalSessionCount = sessions.size();
        final int totalEarnings = SessionDatabase.resolveEarnings(sessions);

        final StringBuilder sb = new StringBuilder();
        sb.append("**Productivité commune :**").append("\n");
        sb.append(String.format("Total de sessions: %d", sessions.size())).append("\n");
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
            sb.append(String.format("Sessions : %d (%.2f%%)", branchSessions.size(), Toolbox.getPercent(branchSessionCount, totalSessionCount))).append("\n");
            sb.append(String.format("Revenus générés: %d tokens (%.2f%%)", branchEarnings, Toolbox.getPercent(branchEarnings, totalEarnings))).append("\n\n");
        });

        {
            final SessionModel latest = SessionDatabase.get().resolveLatest();
            sb.append("**Dernière session :**").append("\n");
            if (latest == null || !latest.getPeriod().getEndedAt().isWithinWeek()) {
                sb.append("Aucune").append("\n\n");
            } else {
                sb.append(latest.getDescriptionFormat()).append("\n");
            }
        }
        embed.setDescription(sb.toString());
        return embed.build();
    }

    public static MessageEmbed reportsLatestMessage() {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Historique des rapports");

        final StringBuilder sb = new StringBuilder();
        sb.append("""
                Cette interface vous permet d'accéder aux rapports.
                Pour toutes actions, munisser vous de l'ID uniquement.
                """);
        sb.append("\n");

        ReportDatabase.get().resolveLatestLimit(5).forEach(r -> {
            sb.append(r.getDescriptionFormat()).append("\n");
        });

        embed.setDescription(sb.toString());
        return embed.build();
    }

    /* Employee messages */
    public static MessageEmbed workforceOverviewMessage() {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Liste des employés");

        final StringBuilder sb = new StringBuilder();
        sb.append("Cette interface vous permet de gérer l'effectif total.").append("\n");
        sb.append("Pour toutes actions, munisser vous du CID uniquement.").append("\n\n");

        sb.append(String.format("%s **Branche CWU (%s)**", EmojiUtils.getScales(), CwuBranch.CWU.getMeaning())).append("\n");
        {
            final ProfileModel supervisor = ProfileDatabase.get().getSupervisor();
            if (supervisor != null) sb.append(supervisor).append("\n");

            final ProfileModel assistant = ProfileDatabase.get().getAssistant();
            if (assistant != null) sb.append(assistant).append("\n");
        }
        sb.append("\n");

        ProfileDatabase.get().getAsGroupAndOrder().forEach((branch, profiles) -> {
            if (branch == CwuBranch.CWU) return;
            sb.append(String.format("%s **Branche %s (%s)**", branch.getEmoji(), branch.name(), branch.getMeaning())).append("\n");
            profiles.forEach(profile -> sb.append(profile).append("\n"));
            sb.append("\n");
        });

        embed.setDescription(sb.toString());
        return embed.build();
    }

    public static MessageEmbed employeeStatsView() {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Statistiques des employés");

        final StringBuilder sb = new StringBuilder();
        ProfileDatabase.get().getAsGroupAndOrder().forEach((branch, profiles) -> {
            sb.append(String.format("%s  **Branche %s (%s)**", branch.getEmoji(), branch.name(), branch.getMeaning())).append("\n");

            if (profiles.isEmpty()) return;
            profiles.forEach(profile -> sb.append(profile.getWeekStats()).append("\n\n"));
        });

        embed.setDescription(sb.toString());
        return embed.build();
    }

    public static MessageEmbed employeeAlreadyExistMessage(final IdentityModel identity, final Long id) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Vous avez un profil en cours de création!");

        final StringBuilder sb = new StringBuilder();
        sb.append("Vous ne pouvez créer qu'un profil en simultané.\n");
        sb.append("Souhaitez-vous reprendre ou écraser le profil en cours ?\n");
        sb.append("\n");
        sb.append(String.format("**Identité :** %s | **Identifiant :** <@%s>\n", identity, id));
        embed.setDescription(sb.toString());
        return embed.build();
    }

    public static MessageEmbed createEmployeeMessage() {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Enregistrement d'un employé | Initialisation");

        final StringBuilder sb = new StringBuilder();
        sb.append("""
                Cette interface vous permet de créer le profil d'un employé.
                                
                Vous allez procéder au remplissage des informations.
                S'ensuivra un résumé de votre session.
                """);
        sb.append("\n");

        embed.setDescription(sb.toString());
        return embed.build();
    }

    private static MessageEmbed.Field employeeIdentityField(final IdentityModel identity) {
        final boolean hasValue = identity != null;

        final String name = String.format("%s  %s", EmojiUtils.getGreenOrRedCircle(hasValue), ProfilePageType.IDENTITY.getDescription());
        final String value = String.format("%s", hasValue ? identity.toString() : "`Information manquante`");
        return new MessageEmbed.Field(name, value, false);
    }

    public static MessageEmbed employeeIdentityMessage(final IdentityModel identity) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Enregistrement d'un employé | 1/5");
        embed.addField(employeeIdentityField(identity));
        return embed.build();
    }

    private static MessageEmbed.Field employeeIdField(final Long id) {
        final boolean hasValue = id != null;

        final String name = String.format("%s  %s", EmojiUtils.getGreenOrYellowCircle(hasValue), ProfilePageType.ID.getDescription());
        final String value = String.format("%s", hasValue ? String.format("<@%d>", id) : "`Information manquante`");
        return new MessageEmbed.Field(name, value, false);
    }

    public static MessageEmbed profileIdMessage(final Long id) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Enregistrement d'un employé | 2/5");
        embed.addField(employeeIdField(id));
        return embed.build();
    }

    public static MessageEmbed employeeBranchMessage(final CwuBranch branch) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Enregistrement d'un employé | 3/5");
        embed.addField(ProfileBuilderUtils.branchField(branch));
        return embed.build();
    }

    public static MessageEmbed profileRankMessage(final CwuRank rank) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Enregistrement d'un employé | 4/5");
        embed.addField(ProfileBuilderUtils.rankField(rank));
        return embed.build();
    }

    private static MessageEmbed.Field employeeJoinedAtField(final SimpleDate joinedAt) {
        final boolean hasValue = joinedAt != null;

        final String name = String.format("%s  %s", EmojiUtils.getGreenOrRedCircle(hasValue), ProfilePageType.JOINED_AT.getDescription());
        final String value = String.format("%s", hasValue ? joinedAt.getDate() : "`Information manquante`");
        return new MessageEmbed.Field(name, value, false);
    }

    public static MessageEmbed employeeJoinedAtMessage(final SimpleDate joinedAt) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Enregistrement d'un employé | 5/5");
        embed.addField(employeeJoinedAtField(joinedAt));
        return embed.build();
    }

    public static MessageEmbed employeePreviewMessage(final ProfileModel employee) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Enregistrement d'un employé | Apperçu");

        embed.addField(employeeIdentityField(employee.getIdentity()));
        embed.addField(employeeIdField(employee.getId()));
        embed.addField(ProfileBuilderUtils.branchField(employee.getBranch()));
        embed.addField(ProfileBuilderUtils.rankField(employee.getRank()));
        embed.addField(employeeJoinedAtField(employee.getJoinedAt()));

        return embed.build();
    }

    /* Modals */
    public static Modal employeeSearchModal(final String cid) {
        final TextInput.Builder searchInput = TextInput.create("cwu_manage.fill/data", "Cid :", TextInputStyle.SHORT)
                .setPlaceholder("ex: (#)12345")
                .setRequired(true);

        return Modal.create(ManageModalType.PROFILE_SEARCH.build(cid), "Recherche d'un employé")
                .addComponents(ActionRow.of(searchInput.build()))
                .build();
    }

    public static Modal reportSearchModal(final String cid) {
        final TextInput.Builder searchInput = TextInput.create("cwu_manage.fill/data", "Identifiant :", TextInputStyle.SHORT)
                .setPlaceholder("ex: 15wegf869")
                .setRequired(true);

        return Modal.create(ManageModalType.REPORT_SEARCH.build(cid), "Recherche d'un rapport")
                .addComponents(ActionRow.of(searchInput.build()))
                .build();
    }

    public static Modal sessionSearchModal(final String cid) {
        final TextInput.Builder searchInput = TextInput.create("cwu_manage.fill/data", "Identifiant :", TextInputStyle.SHORT)
                .setPlaceholder("ex: 15wegf869")
                .setRequired(true);
        return Modal.create(ManageModalType.SESSION_SEARCH.build(cid), "Recherche d'une session")
                .addComponents(ActionRow.of(searchInput.build()))
                .build();
    }

    /* Buttons */
    private static Button startButton(final String cid) {
        return Button.primary(WorkforceButtonType.START.build(cid), "Commencer");
    }

    public static Button cancelButton(final IActionType type, final String cid) {
        return Button.danger(type.build(cid), "Abandonner");
    }

    public static Button confirmButton(final IActionType type, final String cid) {
        return Button.danger(type.build(cid), "Confirmer");
    }

    public static Button returnButton(final IActionType type, final String cid) {
        return Button.secondary(type.build(cid), "Revenir en arrière");
    }

    public static Button statsButton(final IActionType type, final String cid) {
        return Button.primary(type.build(cid), "Statistiques");
    }

    public static Button updateButton(final IActionType type, final String cid) {
        return Button.primary(type.build(cid), "Mettre à jour");
    }

    public static Button editButton(final IActionType type, final String cid) {
        return Button.primary(type.build(cid), "Modifier");
    }

    public static Button manageButton(final IActionType type, final String cid) {
        return Button.secondary(type.build(cid), "Gestion");
    }

    private static Button fillButton(final IActionType type, final String cid) {
        return Button.primary(type.build(cid), "Remplir");
    }

    private static Button clearButton(final IActionType type, final String cid) {
        return Button.danger(type.build(cid), "Effacer");
    }

    private static Button prevButton(final IActionType type, final String cid) {
        return Button.secondary(type.build(cid), EmojiUtils.asDiscordEmoji(EmojiUtils.getLeftArrow()));
    }

    private static Button nextButton(final IActionType type, final String cid) {
        return Button.secondary(type.build(cid), EmojiUtils.asDiscordEmoji(EmojiUtils.getRightArrow()));
    }

    private static Button previewButton(final IActionType type, final String cid) {
        return Button.success(type.build(cid), "Apperçu");
    }

    private static Button submitButton(final IActionType type, final String cid) {
        return Button.success(type.build(cid), "Envoyer");
    }

    private static Button resumeButton(final String cid) {
        return Button.primary(WorkforceButtonType.RESUME.build(cid), "Reprendre");
    }

    private static Button deleteButton(final IActionType type, final String cid) {
        return Button.danger(type.build(cid), "Supprimer");
    }

    private static Button overwriteButton(final String cid) {
        return Button.danger(WorkforceButtonType.OVERWRITE.build(cid), "Écraser");
    }

    /* Single Components */
    public static ActionRow workforceOverviewComponent(final String cid) {
        return ActionRow.of(statsButton(WorkforceButtonType.STATS, cid), manageButton(WorkforceButtonType.MANAGE, cid));
    }

    public static ActionRow sessionOverviewComponent(final String cid) {
        return ActionRow.of(statsButton(SessionChoiceType.OVERVIEW, cid), manageButton(SessionChoiceType.MANAGE, cid));
    }

    public static ActionRow reportsOverviewComponent(final String cid) {
        return ActionRow.of(statsButton(ReportChoiceType.OVERVIEW, cid), manageButton(ReportChoiceType.MANAGE, cid));
    }

    private static ActionRow submitOrEditRow(final IActionType submitType, final IActionType editType, final String cid) {
        return ActionRow.of(submitButton(submitType, cid), editButton(editType, cid));
    }

    // TODO: FIX
    private static ActionRow profileNextRow(final String cid) {
        return ActionRow.of(startButton(cid));
        //return ActionRow.of(clearButton(ManageButtonType.PROFILE_CLEAR, cid), fillButton(ManageButtonType.PROFILE_FILL, cid), nextButton(ManageButtonType.PROFILE_NEXT, cid));
    }

    private static ActionRow profilePrevRow(final String cid) {
        return ActionRow.of(startButton(cid));
        //return ActionRow.of(previewButton(ManageButtonType.PROFILE_PREVIEW, cid), clearButton(ManageButtonType.PROFILE_CLEAR, cid), fillButton(ManageButtonType.PROFILE_FILL, cid), prevButton(ManageButtonType.PROFILE_PREV, cid));
    }

    private static ActionRow profilePrevAndNextRow(final String cid) {
        return ActionRow.of(startButton(cid));
        //return ActionRow.of(clearButton(ManageButtonType.PROFILE_CLEAR, cid), fillButton(ManageButtonType.PROFILE_FILL, cid), prevButton(ManageButtonType.PROFILE_PREV, cid), nextButton(ManageButtonType.PROFILE_NEXT, cid));
    }

    public static ActionRow createEmployeeComponent(final String cid) {
        return ActionRow.of(startButton(cid), cancelButton(WorkforceButtonType.ABORT, cid));
    }

    public static LayoutComponent employeeIdentityComponent(final String cid) {
        return profileNextRow(cid);
    }

    public static LayoutComponent employeeIdComponent(final String cid) {
        return profilePrevAndNextRow(cid);
    }

    public static LayoutComponent employeeJoinedAtComponent(final String cid) {
        return profilePrevRow(cid);
    }

    public static LayoutComponent employeePreviewComponents(final String cid) {
        return submitOrEditRow(WorkforceButtonType.SUBMIT, WorkforceButtonType.EDIT, cid);
    }

    public static LayoutComponent employeeResumeOrOverwriteComponent(final String cid) {
        return ActionRow.of(overwriteButton(cid), resumeButton(cid));
    }



    public static LayoutComponent employeeStatsComponent(final String cid) {
        return ActionRow.of(returnButton(WorkforceButtonType.OVERVIEW, cid));
    }

    /* Combined components */
    public static List<LayoutComponent> employeeBranchComponents(final String cid, final CwuBranch branch) {
        return List.of(/*branchMenu(branch, cid)*/profilePrevAndNextRow(cid));
    }

    public static List<LayoutComponent> employeeRankComponents(final String cid, final CwuRank rank) {
        return List.of(/*rankMenu(rank, cid),*/ profilePrevAndNextRow(cid));
    }
}
