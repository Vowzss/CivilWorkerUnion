package com.oneliferp.cwu.commands.modules.panel.utils;

import com.oneliferp.cwu.CivilWorkerUnion;
import com.oneliferp.cwu.commands.modules.panel.misc.actions.PanelButtonType;
import com.oneliferp.cwu.models.CwuIdentityModel;
import com.oneliferp.cwu.utils.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class PanelBuilderUtils {
    /* Messages */
    public static MessageEmbed panelMessage() {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Civil Worker's Union | Panel");

        final StringBuilder sb = new StringBuilder();
        sb.append("""
                Cette interface vous permet de gérer la période d'activité
                sur laquelle toutes les statistiques se basent.""").append("\n\n");

        sb.append("Lors d'une réinitialisation :").append("\n");
        sb.append("- Une sauvegarde sera effectué en interne").append("\n");
        sb.append("- Un rapport récapitulatif vous sera transmit").append("\n\n");

        sb.append("**Période en cours :**").append("\n");
        sb.append(String.format("Du `%s` à `aujourd'hui`.", CivilWorkerUnion.get().getConfig().getStartPeriodDate().getDate()));

        embed.setDescription(sb.toString());
        return embed.build();
    }

    public static MessageEmbed resetMessage() {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("\uD83D\uDDD1  Réinitialisation des compteurs");
        embed.setDescription("Afin de terminer la procédure, veuillez confirmer votre choix.");
        embed.addField(new MessageEmbed.Field("Action en cours", "`Vous allez réinitialiser les statistiques`", false));
        return embed.build();
    }

    /* Buttons */
    private static Button resetButton(final String cid) {
        return Button.danger(PanelButtonType.RESET_REQUEST.build(cid), "Réinitialiser les compteurs");
    }

    private static Button confirmButton(final String cid) {
        return Button.danger(PanelButtonType.RESET_CONFIRM.build(cid), "Confirmer");
    }

    private static Button cancelButton(final String cid) {
        return Button.primary(PanelButtonType.RESET_CANCEL.build(cid), "Abandonner");
    }

    private static Button dateButton(final String cid) {
        return Button.secondary(PanelButtonType.PERIOD.build(cid), "Définir la période");
    }

    /* Components */
    public static ActionRow panelComponent(final String cid) {
        return ActionRow.of(dateButton(cid), resetButton(cid));
    }

    public static ActionRow resetComponent(final String cid) {
        return ActionRow.of(confirmButton(cid), cancelButton(cid));
    }
}
