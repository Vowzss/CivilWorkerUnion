package com.oneliferp.cwu.commands.modules.panel;

import com.oneliferp.cwu.commands.CwuCommand;
import com.oneliferp.cwu.commands.modules.panel.utils.PanelBuilderUtils;
import com.oneliferp.cwu.commands.modules.profile.exceptions.ProfileNotFoundException;
import com.oneliferp.cwu.commands.modules.profile.exceptions.ProfilePermissionException;
import com.oneliferp.cwu.commands.modules.profile.models.ProfileModel;
import com.oneliferp.cwu.database.ProfileDatabase;
import com.oneliferp.cwu.exceptions.CwuException;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class PanelCommand extends CwuCommand {
    private final ProfileDatabase profileDB;

    public PanelCommand() {
        super("panel", "panel", "Vous permet d'accéder ");

        this.profileDB = ProfileDatabase.get();
    }

    @Override
    public void handleCommandInteraction(final SlashCommandInteractionEvent event) throws CwuException {
        final ProfileModel profile = this.profileDB.getFromId(event.getUser().getIdLong());
        if (profile == null) throw new ProfileNotFoundException(event);
        if (!profile.isAdministration()) throw new ProfilePermissionException(event);

        event.replyEmbeds(PanelBuilderUtils.panelMessage())
                .setComponents(PanelBuilderUtils.panelComponent(profile.getCid()))
                .setEphemeral(true).queue();
    }
}
