package com.oneliferp.cwu.modules.profile.utils;

import com.oneliferp.cwu.models.CwuModel;
import com.oneliferp.cwu.utils.EmbedUtils;
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

        final String content = String.format("""
                        **Identité:** %s
                        **Division:** %s (%s)
                        **Grade:** %s
                                        
                        **Associé à:** <@%d>
                        """, cwu.getIdentity(),
                cwu.getBranch(), cwu.getBranch().getMeaning(),
                cwu.getRank().getLabel(), cwu.getId()
        );

        embed.setDescription(content);
        return embed.build();
    }

    public static MessageEmbed statsMessage(final CwuModel cwu) {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("\uD83D\uDCCA  Statistiques du profil");
        embed.setDescription(String.format("""
                        **Rejoint le:** %s
                        
                        **Rapport(s) totaux:** %d
                        **Rapport(s) hebdomadaire:** %d
                        """, cwu.getJoinedAt().date,
                cwu.getSessionCount(), cwu.getWeeklySessionCount()
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

    /*
    Buttons
    */
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

    /*
    Utils
    */
    public static List<Button> statsAndDeleteRow(final String cid) {
        return List.of(statsButton(cid), deleteButton(cid));
    }

    public static List<Button> confirmAndCancelRow(final List<ProfileButtonType> types, final String cid) {
        return List.of(confirmButton(types.get(0), cid), cancelButton(types.get(1), cid));
    }
}
