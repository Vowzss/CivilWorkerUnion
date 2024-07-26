package com.oneliferp.cwu.modules.profile.utils;

import com.oneliferp.cwu.Models.CwuModel;
import com.oneliferp.cwu.Utils.EmbedUtils;
import com.oneliferp.cwu.modules.profile.misc.ProfileButtonType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.List;

public class ProfileBuilderUtils {
    /*
    Messages
    */
    public static MessageEmbed profileMessage(final CwuModel cwu) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("\uD83D\uDD0E  Informations du profil");

        final StringBuilder content = new StringBuilder(String.format(
                """
                **Identité:** %s
                **Division:** %s (%s)
                **Grade:** %s
                """, cwu.getIdentity(),
                cwu.getBranch(), cwu.getBranch().getMeaning(),
                cwu.getRank().getLabel()
        ));

        if (cwu.isLinked()) {
            content.append(String.format("\n**Associé à:** %s", String.format("<@%d>", cwu.getId())));
        }

        embed.setDescription(content.toString());
        return embed.build();
    }

    public static MessageEmbed statsMessage(final CwuModel cwu) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("\uD83D\uDCCA  Statistiques du profil");
        embed.setDescription(String.format(
                """
                **Identité:** %s
                **Rapport(s) totaux/mensuel/hebdomadaire:** %d/%d/%d
                """, cwu.getIdentity(),
                cwu.getSessionCount(), cwu.getMonthlySessionCount(), cwu.getWeeklySessionCount()
        ));
        return embed.build();
    }

    public static MessageEmbed suppressMessage(final CwuModel cwu) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("\uD83D\uDDD1  Suppression du profil");
        embed.setDescription("Afin de terminer la procédure, veuillez confirmer votre choix.");
        embed.addField(new MessageEmbed.Field("Action en cours", String.format("Vous allez supprimer le profil: **%s**.", cwu.getIdentity()), false));
        return embed.build();
    }

    public static MessageEmbed unlinkMessage(final CwuModel cwu) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("\uD83D\uDD17  Dissociation du profil");
        embed.setDescription("Afin de terminer la procédure, veuillez confirmer votre choix.");
        embed.addField(new MessageEmbed.Field("Action en cours", String.format("Vous allez dissocier le profil: **%s** de l'utilisateur: <@%s>.", cwu.getIdentity(), cwu.getId()), false));
        return embed.build();
    }

    public static MessageEmbed linkMessage(final long userID, final CwuModel cwu) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("\uD83D\uDD17  Association du profil");
        embed.setDescription("Afin de terminer la procédure, veuillez confirmer votre choix.");
        embed.addField(new MessageEmbed.Field("Action en cours", String.format("Vous allez associer le profil: **%s** à l'utilisateur: <@%s>.", cwu.getIdentity(), userID), false));
        return embed.build();
    }

    /*
    Buttons
    */
    private static Button statsButton(final String cid) {
        return Button.primary(ProfileButtonType.STATS.getId() + String.format(":%s", cid), "Statistiques");
    }

    private static Button deleteButton(final String cid) {
        return Button.danger(ProfileButtonType.DELETE.getId() + String.format(":%s", cid), "Supprimer");
    }

    public static Button returnButton(final String cid) {
        return Button.secondary(ProfileButtonType.RETURN.getId() + String.format(":%s", cid), "Retour");
    }

    public static Button linkButton(final String cid) {
        return Button.secondary(ProfileButtonType.LINK.getId() + String.format(":%s", cid), "Associer");
    }

    public static Button unlinkButton(final String cid) {
        return Button.danger(ProfileButtonType.UNLINK.getId() + String.format(":%s", cid), "Dissocier");
    }

    public static Button confirmButton(final ProfileButtonType type, final String cid) {
        return Button.danger(type.getId() + String.format(":%s", cid), "Confirmer");
    }

    public static Button cancelButton(final ProfileButtonType type, final String cid) {
        return Button.primary(type.getId() + String.format(":%s", cid), "Annuler");
    }

    /*
    Utils
    */
    public static List<Button> actionsRow(final String cid, final boolean isLinked) {
        return List.of(statsButton(cid), isLinked ? unlinkButton(cid) : linkButton(cid), deleteButton(cid));
    }

    public static List<Button> confirmAndCancelRow(final List<ProfileButtonType> types, final String cid) {
        return List.of(confirmButton(types.get(0), cid), cancelButton(types.get(1), cid));
    }
}
