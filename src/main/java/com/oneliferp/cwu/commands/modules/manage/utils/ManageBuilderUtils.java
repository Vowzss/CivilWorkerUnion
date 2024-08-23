package com.oneliferp.cwu.commands.modules.manage.utils;

import com.oneliferp.cwu.commands.modules.manage.misc.EmployeePageType;
import com.oneliferp.cwu.commands.modules.manage.misc.actions.ManageButtonType;
import com.oneliferp.cwu.commands.modules.manage.misc.actions.ManageMenuType;
import com.oneliferp.cwu.commands.modules.manage.misc.actions.ManageModalType;
import com.oneliferp.cwu.commands.modules.manage.models.EmployeeModel;
import com.oneliferp.cwu.commands.modules.profile.models.ProfileModel;
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
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.Arrays;
import java.util.List;

public class ManageBuilderUtils {
    /* Messages */
    public static MessageEmbed sessionsSummaryMessage() {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Historique des sessions");

        final StringBuilder sb = new StringBuilder();
        sb.append("""
                Cette interface vous permet d'accéder aux sessions de travail.
                Pour toutes actions, munisser vous de l'ID uniquement.
                """).append("\n");

        sb.append("**Sessions récemment effectué :**").append("\n");
        SessionDatabase.get().resolveLatestLimit(5).forEach(session -> {
            sb.append(SessionModel.getAsDescription(session)).append("\n");
        });

        embed.setDescription(sb.toString());
        return embed.build();
    }

    public static MessageEmbed sessionStatsMessage() {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Statistiques de la semaine");

        final StringBuilder sb = new StringBuilder();

        {
            final ProfileModel supervisor = ProfileDatabase.get().getSupervisor();
            if (supervisor != null) {
                sb.append(String.format("%s  **Superviseur** | Sessions: %d", EmojiUtils.getPenBall(), supervisor.resolveWeekSessions().size())).append("\n");
                sb.append("\n");
            }

            final ProfileModel assistant = ProfileDatabase.get().getAssistant();
            if (assistant != null) {
                sb.append(String.format("%s  **Adjoint** | Sessions: %d", EmojiUtils.getPenFountain(), assistant.resolveWeekSessions().size())).append("\n");
                sb.append("\n");
            }
        }

        ProfileDatabase.get().getAsGroupAndOrder().forEach((branch, profiles) -> {
            sb.append(String.format("%s  **Branche %s** | Sessions: %d", branch.getEmoji(), branch.name(), profiles.stream().mapToInt(p -> p.resolveWeekSessions().size()).sum())).append("\n");
        });
        sb.append("\n");

        {
            final List<SessionModel> sessions = SessionDatabase.get().resolveWithinWeek();
            final SessionModel latest = SessionDatabase.get().resolveLatest();

            sb.append("**Dernière session :**").append("\n");
            if (latest == null || !latest.getPeriod().getEndedAt().isWithinWeek()) {
                sb.append("Aucune").append("\n").append("\n");
            } else {
                sb.append(SessionModel.getAsDescription(latest)).append("\n");
            }

            sb.append(String.format("Total de sessions: %d", sessions.size())).append("\n");
            sb.append(String.format("Revenus générés: %d tokens", SessionDatabase.resolveEarnings(sessions))).append("\n");
            sb.append(String.format("Paies distribuées: %d tokens", SessionDatabase.resolveWages(sessions))).append("\n");
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

        ReportDatabase.get().getLatests(8).forEach(report -> {
            sb.append(String.format("%s  %s - %s **(ID: %s)**", report.getBranch().getEmoji(), report.getBranch().name(), report.getType().getLabel(), report.getId())).append("\n");
        });

        embed.setDescription(sb.toString());
        return embed.build();
    }

    /* Employee messages */
    public static MessageEmbed employeeListView() {
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

        {
            final ProfileModel supervisor = ProfileDatabase.get().getSupervisor();
            sb.append(String.format("%s  **Superviseur**", EmojiUtils.getPenBall())).append("\n");
            sb.append(String.format("%s", supervisor == null ? "Aucun" : supervisor)).append("\n");
            if (supervisor != null) sb.append(supervisor.getOverallStats()).append("\n");
            sb.append("\n");
        }

        {
            final ProfileModel assistant = ProfileDatabase.get().getAssistant();
            sb.append(String.format("%s  **Adjoint**", EmojiUtils.getPenFountain())).append("\n");
            sb.append(String.format("%s", assistant == null ? "Aucun" : assistant)).append("\n");
            if (assistant != null) sb.append(assistant.getOverallStats()).append("\n");
            sb.append("\n");
        }

        ProfileDatabase.get().getAsGroupAndOrder().forEach((branch, profiles) -> {
            sb.append(String.format("%s  **Employés %s**", branch.getEmoji(), branch.name())).append("\n");
            profiles.forEach(profile -> {
                sb.append(profile).append("\n");
                sb.append(profile.getOverallStats()).append("\n");
            });
            sb.append("\n");
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

        final String name = String.format("%s  %s", EmojiUtils.getGreenOrRedCircle(hasValue), EmployeePageType.IDENTITY.getDescription());
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

        final String name = String.format("%s  %s", EmojiUtils.getGreenOrYellowCircle(hasValue), EmployeePageType.ID.getDescription());
        final String value = String.format("%s", hasValue ? String.format("<@%d>", id) : "`Information manquante`");
        return new MessageEmbed.Field(name, value, false);
    }

    public static MessageEmbed profileIdMessage(final Long id) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Enregistrement d'un employé | 2/5");
        embed.addField(employeeIdField(id));
        return embed.build();
    }

    private static MessageEmbed.Field employeeBranchField(final CwuBranch branch) {
        final boolean hasValue = branch != null;

        final String name = String.format("%s %s", EmojiUtils.getGreenOrRedCircle(hasValue), EmployeePageType.BRANCH.getDescription());
        final String value = String.format("%s", hasValue ? String.format("%s - %s", branch.name(), branch.getMeaning()) : "`Information manquante`");
        return new MessageEmbed.Field(name, value, false);
    }

    public static MessageEmbed employeeBranchMessage(final CwuBranch branch) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Enregistrement d'un employé | 3/5");
        embed.addField(employeeBranchField(branch));
        return embed.build();
    }

    private static MessageEmbed.Field employeeRankField(final CwuRank rank) {
        final boolean hasValue = rank != null;

        final String name = String.format("%s  %s", EmojiUtils.getGreenOrRedCircle(hasValue), EmployeePageType.RANK.getDescription());
        final String value = String.format("%s", hasValue ? rank.getLabel() : "`Information manquante`");
        return new MessageEmbed.Field(name, value, false);
    }

    public static MessageEmbed profileRankMessage(final CwuRank rank) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Enregistrement d'un employé | 4/5");
        embed.addField(employeeRankField(rank));
        return embed.build();
    }

    private static MessageEmbed.Field employeeJoinedAtField(final SimpleDate joinedAt) {
        final boolean hasValue = joinedAt != null;

        final String name = String.format("%s  %s", EmojiUtils.getGreenOrRedCircle(hasValue), EmployeePageType.JOINED_AT.getDescription());
        final String value = String.format("%s", hasValue ? joinedAt.getDate() : "`Information manquante`");
        return new MessageEmbed.Field(name, value, false);
    }

    public static MessageEmbed employeeJoinedAtMessage(final SimpleDate joinedAt) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Enregistrement d'un employé | 5/5");
        embed.addField(employeeJoinedAtField(joinedAt));
        return embed.build();
    }

    public static MessageEmbed employeePreviewMessage(final EmployeeModel employee) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Enregistrement d'un employé | Apperçu");

        embed.addField(employeeIdentityField(employee.getIdentity()));
        embed.addField(employeeIdField(employee.getId()));
        embed.addField(employeeBranchField(employee.getBranch()));
        embed.addField(employeeRankField(employee.getRank()));
        embed.addField(employeeJoinedAtField(employee.getJoinedAt()));

        return embed.build();
    }

    /* Menus */
    private static StringSelectMenu branchMenu(final CwuBranch branch, final String cid) {
        final var menu = StringSelectMenu.create(ManageMenuType.SELECT_BRANCH.build(cid));
        final var options = Arrays.stream(CwuBranch.values())
                .map(v -> SelectOption.of(String.format("%s - %s", v.name(), v.getMeaning()), v.name())).toList();
        options.forEach(menu::addOptions);

        if (branch != null) Toolbox.setDefaulMenuOption(menu, options, branch.name());
        return menu.build();
    }

    private static StringSelectMenu rankMenu(final CwuRank rank, final String cid) {
        final var menu = StringSelectMenu.create(ManageMenuType.SELECT_RANK.build(cid));
        final var options = Arrays.stream(CwuRank.values())
                .map(v -> SelectOption.of(v.getLabel(), v.name())).toList();
        options.forEach(menu::addOptions);

        if (rank != null) Toolbox.setDefaulMenuOption(menu, options, rank.name());
        return menu.build();
    }

    /* Modals */
    public static Modal employeeSearchModal(final String cid) {
        final TextInput.Builder searchInput = TextInput.create("cwu_manage.fill/data", "Cid :", TextInputStyle.SHORT)
                .setPlaceholder("ex: #12345")
                .setRequired(true);

        return Modal.create(ManageModalType.EMPLOYEE_SEARCH.build(cid), "Recherche d'un employé")
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
        return Button.primary(ManageButtonType.PROFILE_START.build(cid), "Commencer");
    }

    public static Button cancelButton(final String cid) {
        return Button.danger(ManageButtonType.PROFILE_ABORT.build(cid), "Abandonner");
    }

    public static Button returnButton(final IActionType type, final String cid) {
        return Button.secondary(type.build(cid), "Revenir en arrière");
    }

    public static Button statsButton(final IActionType type, final String cid) {
        return Button.primary(type.build(cid), "Statistiques");
    }

    public static Button updateButton(final IActionType type, final String cid) {
        return Button.secondary(type.build(cid), "Mettre à jour");
    }

    public static Button editButton(final IActionType type, final String cid) {
        return Button.secondary(type.build(cid), "Modifier");
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
        return Button.primary(ManageButtonType.PROFILE_RESUME.build(cid), "Reprendre");
    }

    private static Button overwriteButton(final String cid) {
        return Button.danger(ManageButtonType.PROFILE_OVERWRITE.build(cid), "Écraser");
    }

    /* Rows */
    public static ActionRow employeeListComponent(final String cid) {
        return ActionRow.of(statsButton(ManageButtonType.EMPLOYEE_SUMMARY, cid), manageButton(ManageButtonType.EMPLOYEE_MANAGE, cid));
    }

    public static ActionRow sessionsSummaryMessage(final String cid) {
        return ActionRow.of(statsButton(ManageButtonType.SESSION_STATS, cid), manageButton(ManageButtonType.SESSION_MANAGE, cid));
    }

    public static ActionRow reportsComponent(final String cid) {
        return ActionRow.of(statsButton(ManageButtonType.REPORT_STATS, cid), manageButton(ManageButtonType.SESSION_MANAGE, cid));
    }

    private static ActionRow submitOrEditRow(final IActionType submitType, final IActionType editType, final String cid) {
        return ActionRow.of(submitButton(submitType, cid), editButton(editType, cid));
    }

    private static ActionRow profileNextRow(final String cid) {
        return ActionRow.of(clearButton(ManageButtonType.PROFILE_CLEAR, cid), fillButton(ManageButtonType.PROFILE_FILL, cid), nextButton(ManageButtonType.PROFILE_NEXT, cid));
    }

    private static ActionRow profilePrevRow(final String cid) {
        return ActionRow.of(previewButton(ManageButtonType.PROFILE_PREVIEW, cid), clearButton(ManageButtonType.PROFILE_CLEAR, cid), fillButton(ManageButtonType.PROFILE_FILL, cid), prevButton(ManageButtonType.PROFILE_PREV, cid));
    }

    private static ActionRow profilePrevAndNextRow(final String cid) {
        return ActionRow.of(clearButton(ManageButtonType.PROFILE_CLEAR, cid), fillButton(ManageButtonType.PROFILE_FILL, cid), prevButton(ManageButtonType.PROFILE_PREV, cid), nextButton(ManageButtonType.PROFILE_NEXT, cid));
    }

    public static ActionRow createEmployeeComponent(final String cid) {
        return ActionRow.of(startButton(cid), cancelButton(cid));
    }

    public static LayoutComponent profileIdentityComponent(final String cid) {
        return profileNextRow(cid);
    }

    public static LayoutComponent profileIdComponent(final String cid) {
        return profilePrevAndNextRow(cid);
    }

    public static LayoutComponent profileJoinedAtComponent(final String cid) {
        return profilePrevRow(cid);
    }

    public static LayoutComponent profilePreviewComponents(final String cid) {
        return submitOrEditRow(ManageButtonType.PROFILE_SUBMIT, ManageButtonType.PROFILE_EDIT, cid);
    }

    public static LayoutComponent employeeResumeOrOverwriteComponent(final String cid) {
        return ActionRow.of(overwriteButton(cid), resumeButton(cid));
    }

    public static List<LayoutComponent> profileBranchComponents(final String cid, final CwuBranch branch) {
        return List.of(
                ActionRow.of(branchMenu(branch, cid)),
                profilePrevAndNextRow(cid)
        );
    }

    public static List<LayoutComponent> profileRankComponents(final String cid, final CwuRank rank) {
        return List.of(
                ActionRow.of(rankMenu(rank, cid)),
                profilePrevAndNextRow(cid)
        );
    }

    public static LayoutComponent sessionStatsComponent(final String cid) {
        return ActionRow.of(returnButton(ManageButtonType.SESSION_SUMMARY, cid));
    }
}