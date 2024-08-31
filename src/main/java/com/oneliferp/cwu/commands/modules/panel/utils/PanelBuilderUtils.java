package com.oneliferp.cwu.commands.modules.panel.utils;

import com.oneliferp.cwu.commands.modules.panel.misc.actions.PanelButtonType;
import com.oneliferp.cwu.utils.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class PanelBuilderUtils {
    /* Messages */

    public static MessageEmbed panelMessage() {
        final EmbedBuilder embed = EmbedUtils.createDefault();
        embed.setTitle("Panel de gestion");
        embed.setDescription("""
                """);
        return embed.build();
    }

    /* Buttons */
    private static Button resetButton(final String cid) {
        return Button.danger(PanelButtonType.RESET.build(cid), "Réinitialiser");
    }

    /* Components */
    public static ActionRow panelComponent(final String cid) {
        return ActionRow.of(resetButton(cid));
    }
}
