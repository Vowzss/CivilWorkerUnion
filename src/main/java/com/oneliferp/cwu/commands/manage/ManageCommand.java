package com.oneliferp.cwu.commands.manage;

import com.oneliferp.cwu.commands.CommandContext;
import com.oneliferp.cwu.commands.CwuCommand;
import com.oneliferp.cwu.commands.manage.misc.ids.ManageModalType;
import com.oneliferp.cwu.commands.manage.misc.ids.ProfileChoiceType;
import com.oneliferp.cwu.commands.manage.misc.ids.ReportChoiceType;
import com.oneliferp.cwu.commands.manage.misc.ids.SessionChoiceType;
import com.oneliferp.cwu.commands.manage.utils.ManageBuilderUtils;
import com.oneliferp.cwu.exceptions.CwuException;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.Objects;

public class ManageCommand extends CwuCommand {
    public ManageCommand() {
        super("manage", "gestion", "Vous permet de gérer les différents aspect de la CWU.");
    }

    @Override
    public SlashCommandData configure(SlashCommandData slashCommand) {
        final SubcommandData createProfileCommand = new SubcommandData("profil", "Vous permet de gérer les employés de la CWU.");
        final SubcommandData sessionSubCommand = new SubcommandData("session", "Vous permet de gérer les sessions de travail.");
        final SubcommandData reportSubCommand = new SubcommandData("rapport", "Vous permet de gérer les rapports.");
        slashCommand.addSubcommands(createProfileCommand, sessionSubCommand, reportSubCommand);
        return super.configure(slashCommand);
    }

    @Override
    public void handleCommandInteraction(SlashCommandInteractionEvent event) {
        switch (Objects.requireNonNull(event.getSubcommandName())) {
            default:
                throw new IllegalArgumentException();
            case "profil": {
                event.replyEmbeds(ManageBuilderUtils.profileView())
                        .setComponents(ManageBuilderUtils.profileActionRow())
                        .queue();
                break;
            }
            case "session": {
                event.replyEmbeds(ManageBuilderUtils.sessionView())
                        .setComponents(ManageBuilderUtils.sessionActionRow())
                        .queue();
                break;
            }
            case "rapport": {
                event.replyEmbeds(ManageBuilderUtils.reportView())
                        .setComponents(ManageBuilderUtils.reportActionRow())
                        .queue();
                break;
            }
        }
    }

    @Override
    public void handleButtonInteraction(final ButtonInteractionEvent event, final CommandContext ctx) throws CwuException {
        final var type = ctx.getEnumType();

        final TextInput.Builder inputBuilder = TextInput.create("cwu_manage.fill/data", "ew", TextInputStyle.SHORT)
                .setRequired(true);

        if (type instanceof SessionChoiceType choice) {
            final var modalType = switch (choice) {
                case VIEW, DELETE, EDIT -> ManageModalType.SESSION_ID;
            };

            final Modal modal = Modal.create(modalType.build(), "Ajout des informations")
                    .addComponents(ActionRow.of(inputBuilder.build()))
                    .build();
            event.replyModal(modal).queue();
        } else if (type instanceof ReportChoiceType choice) {
            final var modalType = switch (choice) {
                case VIEW, DELETE, EDIT -> ManageModalType.REPORT_ID;
            };

            final Modal modal = Modal.create(modalType.build(), "Ajout des informations")
                    .addComponents(ActionRow.of(inputBuilder.build()))
                    .build();
            event.replyModal(modal).queue();
        } else if (type instanceof ProfileChoiceType choice) {
            final var modalType = switch (choice) {
                case CREATE -> ManageModalType.PROFILE_CID;
                case VIEW, DELETE, EDIT -> ManageModalType.PROFILE_CID;
            };

            final Modal modal = Modal.create(modalType.build(), "Ajout des informations")
                    .addComponents(ActionRow.of(inputBuilder.build()))
                    .build();
            event.replyModal(modal).queue();
        } else throw new IllegalArgumentException();
    }
}
