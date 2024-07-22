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
        embed.setDescription(String.format(
                """
                **Identité:** %s
                **Division:** %s (%s)
                **Grade:** %s
                """, cwu.getIdentity(),
                    cwu.getBranch(), cwu.getBranch().getMeaning(),
                    cwu.getRank().getLabel()
        ));
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

    /*
    Buttons
    */
    private static Button statsButton(final String cid) {
        return Button.primary(ProfileButtonType.STATS.getId() + ":" + cid, "Statistiques");
    }

    private static Button deleteButton(final String cid) {
        return Button.danger(ProfileButtonType.DELETE.getId() + ":" + cid, "Supprimer");
    }

    public static Button returnButton(final String cid) {
        return Button.secondary(ProfileButtonType.RETURN.getId() + ":" + cid, "Retour");
    }

    /*
    Utils
    */
    public static List<Button> statsAndDeleteRow(final String cid) {
        return List.of(statsButton(cid), deleteButton(cid));
    }
}
