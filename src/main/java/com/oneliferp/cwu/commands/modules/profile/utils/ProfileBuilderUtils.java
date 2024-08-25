package com.oneliferp.cwu.commands.modules.profile.utils;

import com.oneliferp.cwu.commands.modules.profile.misc.ProfilePageType;
import com.oneliferp.cwu.commands.modules.manage.misc.actions.WorkforceButtonType;
import com.oneliferp.cwu.commands.modules.profile.misc.actions.ProfileButtonType;
import com.oneliferp.cwu.commands.modules.profile.misc.actions.ProfileMenuType;
import com.oneliferp.cwu.commands.modules.profile.models.ProfileModel;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.misc.CwuRank;
import com.oneliferp.cwu.misc.IActionType;
import com.oneliferp.cwu.models.IdentityModel;
import com.oneliferp.cwu.utils.EmbedUtils;
import com.oneliferp.cwu.utils.EmojiUtils;
import com.oneliferp.cwu.utils.Toolbox;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.Arrays;
import java.util.List;

public class ProfileBuilderUtils {
    /* Messages */
    public static MessageEmbed displayMessage(final ProfileModel profile) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Fiche d'information de l'employé");

        final StringBuilder sb = new StringBuilder();
        sb.append(profile.getDescriptionFormat()).append("\n");
        sb.append(profile.getStatsFormat()).append("\n");
        embed.setDescription(sb.toString());
        return embed.build();
    }

    public static MessageEmbed deleteMessage(final IdentityModel identity) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("\uD83D\uDDD1  Suppression de l'employé");
        embed.setDescription("Afin de terminer la procédure, veuillez confirmer votre choix.");
        embed.addField(new MessageEmbed.Field("Action en cours", String.format("Vous allez supprimer l'employé: **%s**.", identity), false));
        return embed.build();
    }

    public static MessageEmbed updateMessage(final CwuBranch branch, final CwuRank rank) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle(String.format("%s Mise à jour de l'employé", EmojiUtils.getPencilMemo()));
        embed.setDescription("Afin de terminer la procédure, veuillez confirmer votre choix.");

        embed.addField(branchField(branch));
        embed.addField(rankField(rank));

        return embed.build();
    }

    /* Fields */
    public static MessageEmbed.Field branchField(final CwuBranch branch) {
        final boolean hasValue = branch != null;

        final String name = String.format("%s %s", EmojiUtils.getGreenOrRedCircle(hasValue), ProfilePageType.BRANCH.getDescription());
        final String value = String.format("%s", hasValue ? String.format("%s - %s", branch.name(), branch.getMeaning()) : "`Information manquante`");
        return new MessageEmbed.Field(name, value, false);
    }

    public static MessageEmbed.Field rankField(final CwuRank rank) {
        final boolean hasValue = rank != null;

        final String name = String.format("%s  %s", EmojiUtils.getGreenOrRedCircle(hasValue), ProfilePageType.RANK.getDescription());
        final String value = String.format("%s", hasValue ? rank.getLabel() : "`Information manquante`");
        return new MessageEmbed.Field(name, value, false);
    }

    /* Buttons */
    private static Button deleteButton(final String cid) {
        return Button.danger(ProfileButtonType.DELETE.build(cid), "Supprimer");
    }

    private static Button returnButton(final IActionType type, final String cid) {
        return Button.secondary(type.build(cid), "Retour en arrière");
    }

    private static Button confirmButton(final IActionType type, final String cid) {
        return Button.danger(type.build(cid), "Confirmer");
    }

    private static Button updateButton(final String cid) {
        return Button.primary(ProfileButtonType.UPDATE.build(cid), "Mettre à jour");
    }

    private static Button cancelButton(final IActionType type, final String cid) {
        return Button.primary(type.build(cid), "Abandonner");
    }

    /* Menus */
    private static ActionRow branchMenu(final CwuBranch branch, final String cid) {
        final var menu = StringSelectMenu.create(ProfileMenuType.SELECT_BRANCH.build(cid));
        final var options = Arrays.stream(CwuBranch.values())
                .map(v -> SelectOption.of(String.format("%s - %s", v.name(), v.getMeaning()), v.name())).toList();
        options.forEach(menu::addOptions);

        if (branch != null) Toolbox.setDefaultMenuOption(menu, options, branch.name());
        return ActionRow.of(menu.build());
    }

    private static ActionRow rankMenu(final CwuRank rank, final String cid) {
        final var menu = StringSelectMenu.create(ProfileMenuType.SELECT_RANK.build(cid));
        final var options = Arrays.stream(CwuRank.values())
                .map(v -> SelectOption.of(v.getLabel(), v.name())).toList();
        options.forEach(menu::addOptions);

        if (rank != null) Toolbox.setDefaultMenuOption(menu, options, rank.name());
        return ActionRow.of(menu.build());
    }

    /* Components */
    public static LayoutComponent deleteComponent(final String cid) {
        return ActionRow.of(cancelButton(ProfileButtonType.DELETE_CANCEL, cid), confirmButton(ProfileButtonType.DELETE_CONFIRM, cid));
    }

    public static LayoutComponent displayComponent(final String cid) {
        return ActionRow.of(deleteButton(cid), updateButton(cid), returnButton(WorkforceButtonType.OVERVIEW, cid));
    }

    public static List<LayoutComponent> updateComponent(final String cid, final CwuBranch branch, final CwuRank rank) {
        return List.of(
                branchMenu(branch, cid),
                rankMenu(rank, cid),
                ActionRow.of(cancelButton(ProfileButtonType.UPDATE_CANCEL, cid), confirmButton(ProfileButtonType.UPDATE_CONFIRM, cid))
        );
    }
}
