package com.oneliferp.cwu.commands.modules.panel;

import com.oneliferp.cwu.CivilWorkerUnion;
import com.oneliferp.cwu.Config;
import com.oneliferp.cwu.commands.CwuCommand;
import com.oneliferp.cwu.commands.modules.panel.misc.actions.PanelButtonType;
import com.oneliferp.cwu.commands.modules.panel.misc.actions.PanelModalType;
import com.oneliferp.cwu.commands.modules.panel.utils.PanelBuilderUtils;
import com.oneliferp.cwu.commands.modules.profile.exceptions.ProfileNotFoundException;
import com.oneliferp.cwu.commands.modules.profile.exceptions.ProfilePermissionException;
import com.oneliferp.cwu.commands.modules.profile.models.ProfileModel;
import com.oneliferp.cwu.commands.modules.profile.utils.ProfileBuilderUtils;
import com.oneliferp.cwu.commands.utils.CommandContext;
import com.oneliferp.cwu.database.ProfileDatabase;
import com.oneliferp.cwu.exceptions.CwuException;
import com.oneliferp.cwu.utils.SimpleDateTime;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

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

    @Override
    public void handleButtonInteraction(final ButtonInteractionEvent event, final CommandContext ctx) throws CwuException {
        final ProfileModel profile = this.profileDB.getFromCid(ctx.getCid());
        if (profile == null) throw new ProfileNotFoundException(event);
        if (!profile.isAdministration()) throw new ProfilePermissionException(event);

        switch ((PanelButtonType) ctx.getEnumType()) {
            case PERIOD -> {
                final TextInput.Builder inputBuilder = TextInput.create("cwu_panel.fill/data", "Définir le début de la période", TextInputStyle.SHORT)
                        .setPlaceholder("ex: 26/12/2024)")
                        .setRequired(true);

                final SimpleDateTime startPeriod = CivilWorkerUnion.get().getConfig().getStartPeriodDate();
                if (startPeriod != null) inputBuilder.setValue(startPeriod.getDate());

                final Modal modal = Modal.create(PanelModalType.PERIOD.build(profile.getCid()), "Ajout des informations")
                        .addComponents(ActionRow.of(inputBuilder.build()))
                        .build();

                event.replyModal(modal).queue();
            }
            case RESET_REQUEST -> {
                event.editMessageEmbeds(PanelBuilderUtils.resetMessage())
                        .setComponents(PanelBuilderUtils.resetComponent(profile.getCid()))
                        .queue();
            }
            case RESET_CANCEL -> {
                CivilWorkerUnion.get().getConfig().setStartPeriodDate(SimpleDateTime.now());
                CivilWorkerUnion.get().getConfig().save();

                event.reply("Vous avez annuler la réinitialisation des compteurs.")
                        .setEphemeral(true).queue();
                event.getMessage().delete().queue();
            }

            case RESET_CONFIRM -> {
                // TODO
                event.reply("Réinitialisation des compteurs avec succès.")
                        .setEphemeral(true).queue();
                event.getMessage().delete().queue();
            }
        }
    }

    @Override
    public void handleModalInteraction(final ModalInteractionEvent event, final CommandContext ctx) throws CwuException {
        final ProfileModel profile = this.profileDB.getFromCid(ctx.getCid());
        if (profile == null) throw new ProfileNotFoundException(event);
        if (!profile.isAdministration()) throw new ProfilePermissionException(event);

        final String content = event.getInteraction().getValue("cwu_panel.fill/data").getAsString();

        switch ((PanelModalType) ctx.getEnumType()) {
            default -> throw new IllegalStateException("Unexpected value: " + (ctx.getEnumType()));
            case PERIOD -> {
                CivilWorkerUnion.get().getConfig().setStartPeriodDate(SimpleDateTime.parseDate(content));
                CivilWorkerUnion.get().getConfig().save();

                event.editMessageEmbeds(PanelBuilderUtils.panelMessage()).queue();
            }
        }
    }
}
