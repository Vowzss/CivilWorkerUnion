package com.oneliferp.cwu.modules.report.utils;

import com.oneliferp.cwu.Models.SessionModel;
import com.oneliferp.cwu.misc.PageType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.AbstractMap;

public class ReportFeedbackUtils {
    public static ReplyCallbackAction sessionCreationSuccess(final SlashCommandInteractionEvent event, final PrivateChannel channel) {
        return event.reply(String.format("Accéder à votre rapport de session ici: %s", channel.getAsMention()));
    }

    public static ReplyCallbackAction sessionCreationFailure(final SlashCommandInteractionEvent event) {
        return event.reply(String.format("Veuillez autoriser la réception de messages privés provenant de **%s**.", event.getGuild().getName()));
    }

    public static ReplyCallbackAction loyalist(final ButtonInteractionEvent event, final SessionModel session) {
        final var action = event.replyEmbeds(ReportBuilderUtils.participantMessage(session.getType(), PageType.LOYALISTS, session.getLoyalists()));
        return action.setActionRow(ReportBuilderUtils.nextRow());
    }
    public static MessageCreateAction loyalist(final Message message, final SessionModel session) {
        final var action = message.replyEmbeds(ReportBuilderUtils.participantMessage(session.getType(), PageType.LOYALISTS, session.getLoyalists()));
        return action.setActionRow(ReportBuilderUtils.nextRow());
    }

    public static ReplyCallbackAction citizen(final ButtonInteractionEvent event, final SessionModel session) {
        final var action = event.replyEmbeds(ReportBuilderUtils.participantMessage(session.getType(), PageType.CITIZENS, session.getCitizens()));
        return action.setActionRow(ReportBuilderUtils.prevAndNextRow());
    }
    public static MessageCreateAction citizen(final Message message, final SessionModel session) {
        final var action = message.replyEmbeds(ReportBuilderUtils.participantMessage(session.getType(), PageType.CITIZENS, session.getCitizens()));
        return action.setActionRow(ReportBuilderUtils.prevAndNextRow());
    }

    public static ReplyCallbackAction vortigaunt(final ButtonInteractionEvent event, final SessionModel session) {
        final var action = event.replyEmbeds(ReportBuilderUtils.participantMessage(session.getType(), PageType.VORTIGAUNTS, session.getVortigaunts()));
        return action.setActionRow(ReportBuilderUtils.prevAndNextRow());
    }
    public static MessageCreateAction vortigaunt(final Message message, final SessionModel session) {
        final var action = message.replyEmbeds(ReportBuilderUtils.participantMessage(session.getType(), PageType.VORTIGAUNTS, session.getVortigaunts()));
        return action.setActionRow(ReportBuilderUtils.prevAndNextRow());
    }

    public static ReplyCallbackAction antiCitizen(final ButtonInteractionEvent event, final SessionModel session) {
        final var action = event.replyEmbeds(ReportBuilderUtils.participantMessage(session.getType(), PageType.ANTI_CITIZENS, session.getAntiCitizens()));
        return action.setActionRow(ReportBuilderUtils.prevAndNextRow());
    }
    public static MessageCreateAction antiCitizen(final Message message, final SessionModel session) {
        final var action = message.replyEmbeds(ReportBuilderUtils.participantMessage(session.getType(), PageType.ANTI_CITIZENS, session.getAntiCitizens()));
        return action.setActionRow(ReportBuilderUtils.prevAndNextRow());
    }

    public static ReplyCallbackAction info(final ButtonInteractionEvent event, final SessionModel session) {
        final var value = new AbstractMap.SimpleEntry<>("", session.getInfo().isBlank() ? "Rien à signaler" : session.getInfo());
        final var action = event.replyEmbeds(ReportBuilderUtils.singleMessage(session.getType(), PageType.INFO, value));
        return action.setActionRow(ReportBuilderUtils.prevAndNextRow());
    }
    public static MessageCreateAction info(final Message message, final SessionModel session) {
        final var value = new AbstractMap.SimpleEntry<>("", session.getInfo().isBlank() ? "Rien à signaler" : session.getInfo());
        final var action = message.replyEmbeds(ReportBuilderUtils.singleMessage(session.getType(), PageType.INFO, value));
        return action.setActionRow(ReportBuilderUtils.prevAndNextRow());
    }

    public static ReplyCallbackAction earnings(final ButtonInteractionEvent event, final SessionModel session) {
        final var value = new AbstractMap.SimpleEntry<>(session.getEarnings().toString(), "Tokens");
        final var action = event.replyEmbeds(ReportBuilderUtils.singleMessage(session.getType(), PageType.EARNINGS, value));
        return action.setActionRow(ReportBuilderUtils.prevRow());
    }
    public static MessageCreateAction earnings(final Message message, final SessionModel session) {
        final var value = new AbstractMap.SimpleEntry<>(session.getEarnings().toString(), "Tokens");
        final var action = message.replyEmbeds(ReportBuilderUtils.singleMessage(session.getType(), PageType.EARNINGS, value));
        return action.setActionRow(ReportBuilderUtils.prevRow());
    }

    public static ReplyCallbackAction preview(final ButtonInteractionEvent event, final SessionModel session) {
        final var action = event.replyEmbeds(ReportBuilderUtils.previewMessage(session));
        return action.setActionRow(ReportBuilderUtils.submitOrEditRow());
    }
}
