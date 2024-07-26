package com.oneliferp.cwu.modules.session.utils;

import com.oneliferp.cwu.Models.SessionModel;
import com.oneliferp.cwu.misc.PageType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.AbstractMap;

public class SessionFeedbackUtils {
    public static ReplyCallbackAction loyalist(final ButtonInteractionEvent event, final SessionModel session) {
        final var action = event.replyEmbeds(SessionBuilderUtils.participantMessage(session.getType(), PageType.LOYALISTS, session.getLoyalists()));
        return action.setActionRow(SessionBuilderUtils.nextRow(session.getCwuIdentity().split("#")[1]));
    }

    public static ReplyCallbackAction citizen(final ButtonInteractionEvent event, final SessionModel session) {
        final var action = event.replyEmbeds(SessionBuilderUtils.participantMessage(session.getType(), PageType.CITIZENS, session.getCitizens()));
        return action.setActionRow(SessionBuilderUtils.prevAndNextRow(session.getCwuIdentity().split("#")[1]));
    }

    public static ReplyCallbackAction vortigaunt(final ButtonInteractionEvent event, final SessionModel session) {
        final var action = event.replyEmbeds(SessionBuilderUtils.participantMessage(session.getType(), PageType.VORTIGAUNTS, session.getVortigaunts()));
        return action.setActionRow(SessionBuilderUtils.prevAndNextRow(session.getCwuIdentity().split("#")[1]));
    }

    public static ReplyCallbackAction antiCitizen(final ButtonInteractionEvent event, final SessionModel session) {
        final var action = event.replyEmbeds(SessionBuilderUtils.participantMessage(session.getType(), PageType.ANTI_CITIZENS, session.getAntiCitizens()));
        return action.setActionRow(SessionBuilderUtils.prevAndNextRow(session.getCwuIdentity().split("#")[1]));
    }

    public static ReplyCallbackAction info(final ButtonInteractionEvent event, final SessionModel session) {
        final var value = new AbstractMap.SimpleEntry<>("", session.getInfo().isBlank() ? "Rien à signaler" : session.getInfo());
        final var action = event.replyEmbeds(SessionBuilderUtils.singleMessage(session.getType(), PageType.INFO, value));
        return action.setActionRow(SessionBuilderUtils.prevAndNextRow(session.getCwuIdentity().split("#")[1]));
    }

    public static ReplyCallbackAction earnings(final ButtonInteractionEvent event, final SessionModel session) {
        final var value = new AbstractMap.SimpleEntry<>(session.getEarnings().toString(), "Tokens");
        final var action = event.replyEmbeds(SessionBuilderUtils.singleMessage(session.getType(), PageType.EARNINGS, value));
        return action.setActionRow(SessionBuilderUtils.prevRow(session.getCwuIdentity().split("#")[1]));
    }

    public static ReplyCallbackAction preview(final ButtonInteractionEvent event, final SessionModel session) {
        final var action = event.replyEmbeds(SessionBuilderUtils.previewMessage(session));
        return action.setActionRow(SessionBuilderUtils.submitOrEditRow(session.getCwuIdentity().split("#")[1]));
    }
}
