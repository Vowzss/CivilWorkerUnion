package com.oneliferp.cwu.commands.modules.session;

import com.oneliferp.cwu.cache.SessionCache;
import com.oneliferp.cwu.commands.CwuCommand;
import com.oneliferp.cwu.commands.modules.profile.utils.ProfileBuilderUtils;
import com.oneliferp.cwu.database.ProfileDatabase;
import com.oneliferp.cwu.database.SessionDatabase;
import com.oneliferp.cwu.exceptions.CwuException;
import com.oneliferp.cwu.commands.utils.CommandContext;
import com.oneliferp.cwu.commands.modules.session.misc.CitizenType;
import com.oneliferp.cwu.commands.modules.session.misc.ZoneType;
import com.oneliferp.cwu.commands.modules.profile.models.ProfileModel;
import com.oneliferp.cwu.commands.modules.session.misc.actions.*;
import com.oneliferp.cwu.commands.modules.session.models.SessionModel;
import com.oneliferp.cwu.commands.modules.profile.exceptions.ProfileNotFoundException;
import com.oneliferp.cwu.commands.modules.session.exceptions.SessionNotFoundException;
import com.oneliferp.cwu.commands.modules.session.exceptions.SessionValidationException;
import com.oneliferp.cwu.commands.modules.session.misc.*;
import com.oneliferp.cwu.commands.modules.session.utils.SessionBuilderUtils;
import com.oneliferp.cwu.exceptions.IdentityException;
import com.oneliferp.cwu.exceptions.IdentityMalformedException;
import com.oneliferp.cwu.models.IdentityModel;
import com.oneliferp.cwu.utils.RegexUtils;
import com.oneliferp.cwu.utils.Toolbox;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInput.Builder;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.ArrayList;

public class SessionCommand extends CwuCommand {
    private final SessionDatabase sessionDatabase;
    private final SessionCache sessionCache;

    private final ProfileDatabase profileDatabase;

    public SessionCommand() {
        super("session", "session", "Vous permet de créer des sessions de travail.");
        this.sessionDatabase = SessionDatabase.get();
        this.sessionCache = SessionCache.get();

        this.profileDatabase = ProfileDatabase.get();
    }

    /*
    Abstract Handlers
    */
    @Override
    public void handleCommandInteraction(final SlashCommandInteractionEvent event) throws CwuException {
        final ProfileModel cwu = this.profileDatabase.getFromId(event.getUser().getIdLong());
        if (cwu == null) throw new ProfileNotFoundException(event);

        final SessionModel ongoingSession = this.sessionCache.get(cwu.getCid());
        if (ongoingSession != null && ongoingSession.getType() != SessionType.UNKNOWN) {
            this.sessionAlreadyExistFeedback(event, ongoingSession);
            return;
        }

        this.sessionInitFeedback(event, cwu.getIdentity());
    }

    @Override
    public void handleButtonInteraction(final ButtonInteractionEvent event, final CommandContext ctx) throws CwuException {
        final ProfileModel profile = this.profileDatabase.getFromCid(ctx.getCid());
        if (profile == null) throw new ProfileNotFoundException(event);

        final SessionButtonType type = ctx.getEnumType();
        if (type == SessionButtonType.DELETE) {
            final SessionModel session = this.sessionDatabase.get(ctx.getAsString("session"));
            if (session == null) throw new SessionNotFoundException(event);

            event.editMessageEmbeds(SessionBuilderUtils.deleteMessage(session))
                    .setComponents(SessionBuilderUtils.deleteComponent(profile.getCid(), session.getId())).queue();
            return;
        } else if (type == SessionButtonType.DELETE_CONFIRM) {
            this.sessionDatabase.removeOne(ctx.getAsString("session"));
            this.sessionDatabase.save();

            event.reply("Suppression de la session de travail avec succès.")
                    .setEphemeral(true).queue();
            event.getMessage().delete().queue();
            return;
        } else if (type == SessionButtonType.DELETE_CANCEL) {
            event.reply("Vous avez annulé la suppression de la session de travail.")
                    .setEphemeral(true).queue();
            event.getMessage().delete().queue();
            return;
        }

        final SessionModel session = this.sessionCache.get(profile.getCid());
        if (session == null) throw new SessionNotFoundException(event);

        switch (type) {
            default -> throw new IllegalArgumentException();
            case BEGIN -> this.handleBeginButton(event, session);
            case ABORT -> this.handleCancelButton(event, session.getEmployeeCid());
            case RESUME -> this.handleResumeButton(event, session);
            case OVERWRITE -> this.handleOverwriteButton(event, profile.getIdentity());
            case EDIT -> this.handleEditButton(event, session);
            case SUBMIT -> this.handleSubmitButton(event, session);
            case CLEAR -> this.handleClearButton(event, session);
            case FILL -> this.handleFillButton(event, session);
            case PREVIEW -> this.handlePreviewButton(event, session);
            case PAGE_NEXT -> this.handlePageButton(event, session, true);
            case PAGE_PREV -> this.handlePageButton(event, session, false);
        }
    }

    @Override
    public void handleModalInteraction(final ModalInteractionEvent event, final CommandContext ctx) throws CwuException {
        final ProfileModel cwu = this.profileDatabase.getFromCid(ctx.getCid());
        if (cwu == null) throw new ProfileNotFoundException(event);

        final SessionModel session = this.sessionCache.get(cwu.getCid());
        if (session == null) throw new SessionNotFoundException(event);

        final SessionPageType currentPage = session.getCurrentPage();
        final String content = event.getValue("cwu_session.fill/data").getAsString();

        switch ((SessionModalType) ctx.getEnumType()) {
            default -> throw new IllegalArgumentException();
            case FILL_PARTICIPANTS -> {
                try {
                    session.addCitizens(CitizenType.fromPage(currentPage), IdentityModel.parseIdentities(content));
                } catch (final IdentityMalformedException ex) {
                    throw new IdentityException(event);
                }

                event.editMessageEmbeds(SessionBuilderUtils.participantMessage(session))
                        .setComponents(SessionBuilderUtils.pageRow(session))
                        .queue();
            }
            case FILL_INFO -> {
                session.setInfo(content);
                event.editMessageEmbeds(SessionBuilderUtils.infoMessage(session))
                        .setComponents(SessionBuilderUtils.pageRow(session))
                        .queue();
            }
            case FILL_EARNINGS -> {
                session.setEarnings(RegexUtils.parseTokens(content));
                event.editMessageEmbeds(SessionBuilderUtils.earningsMessage(session))
                        .setComponents(SessionBuilderUtils.pageRow(session))
                        .queue();
            }
        }
    }

    @Override
    public void handleSelectionInteraction(final StringSelectInteractionEvent event, final CommandContext ctx) throws CwuException {
        final ProfileModel cwu = this.profileDatabase.getFromCid(ctx.getCid());
        if (cwu == null) throw new ProfileNotFoundException(event);

        final SessionModel session = this.sessionCache.get(cwu.getCid());
        if (session == null) throw new SessionNotFoundException(event);

        switch ((SessionMenuType) ctx.getEnumType()) {
            case SELECT_TYPE -> {
                final SessionType type = SessionType.valueOf(event.getValues().get(0));
                if (session.getType() != type) session.setType(type);

                event.editMessageEmbeds(SessionBuilderUtils.initMessage(type))
                        .setComponents(SessionBuilderUtils.beginRow(session.getEmployeeCid(), type))
                        .queue();
            }
            case SELECT_ZONE -> {
                final ZoneType zone = ZoneType.valueOf(event.getValues().get(0));
                if (session.getZone() != zone) session.setZone(zone);

                event.editMessageEmbeds(SessionBuilderUtils.zoneMessage(session))
                        .setComponents(SessionBuilderUtils.zoneRows(session))
                        .queue();
            }
        }
    }

    /* Feedback */
    private void sessionAlreadyExistFeedback(final SlashCommandInteractionEvent event, final SessionModel session) {
        event.replyEmbeds(SessionBuilderUtils.existMessage(session))
                .setComponents(SessionBuilderUtils.resumeOrOverwriteRow(session.getEmployeeCid()))
                .queue();
    }

    private void sessionInitFeedback(final IReplyCallback callback, final IdentityModel identity) {
        final SessionModel session = new SessionModel(identity);
        this.sessionCache.add(identity.cid, session);

        callback.replyEmbeds(SessionBuilderUtils.initMessage(SessionType.UNKNOWN))
                .setComponents(SessionBuilderUtils.initRow(session.getEmployeeCid(), SessionType.UNKNOWN))
                .queue();
    }

    /* Button handlers */
    private void handleCancelButton(final ButtonInteractionEvent event, final String cid) {
        this.sessionCache.remove(cid);

        event.reply("❌ Vous avez annuler votre session de travail.")
                .setEphemeral(true).queue();
        event.getMessage().delete().queue();
    }

    private void handleEditButton(final ButtonInteractionEvent event, final SessionModel session) {
        session.goFirstPage();
        this.goToPage(event, session);
    }

    private void handleOverwriteButton(final ButtonInteractionEvent event, final IdentityModel identity) {
        this.sessionCache.remove(identity.cid);
        this.sessionInitFeedback(event, identity);
    }

    private void handleResumeButton(final ButtonInteractionEvent event, final SessionModel session) {
        session.goPreviewPage();
        this.goToPage(event, session);
    }

    private void handleFillButton(final ButtonInteractionEvent event, final SessionModel session) {
        final SessionPageType currentPage = session.getCurrentPage();
        final Builder inputBuilder = TextInput.create("cwu_session.fill/data", currentPage.getDescription(), TextInputStyle.PARAGRAPH)
                .setRequired(true);

        // Fill input if data exist at page
        SessionModalType modalType;
        switch (currentPage) {
            default -> throw new IllegalArgumentException();
            case LOYALISTS, CIVILIANS, VORTIGAUNTS, ANTI_CITIZENS -> {
                modalType = SessionModalType.FILL_PARTICIPANTS;
                inputBuilder.setPlaceholder("Nom Prénom, #12345");

                final var participants = session.getCitizens(CitizenType.fromPage(currentPage));
                if (participants.isEmpty()) break;
                inputBuilder.setValue(Toolbox.flatten(participants));
            }
            case INFO -> {
                modalType = SessionModalType.FILL_INFO;
                inputBuilder.setPlaceholder("Rien à signaler");

                final var info = session.getInfo();
                if (info == null) break;
                inputBuilder.setValue(info);
            }
            case TOKENS -> {
                modalType = SessionModalType.FILL_EARNINGS;
                inputBuilder.setPlaceholder("ex: 1438");

                final var earnings = session.getIncome().getEarnings();
                if (earnings == null) break;
                inputBuilder.setValue(earnings.toString());
            }
        }

        final Modal modal = Modal.create(modalType.build(session.getEmployeeCid()), "Ajout des informations")
                .addComponents(ActionRow.of(inputBuilder.build()))
                .build();

        event.replyModal(modal).queue();
    }

    private void handleClearButton(final ButtonInteractionEvent event, final SessionModel session) {
        final SessionPageType currentPage = session.getCurrentPage();
        switch (currentPage) {
            case INFO -> session.setInfo(null);
            case TOKENS -> session.setEarnings(null);
            case ZONE -> session.setZone(ZoneType.UNKNOWN);
            default -> session.clearCitizens(CitizenType.fromPage(currentPage));
        }

        this.goToPage(event, session);
    }

    protected void handleBeginButton(final ButtonInteractionEvent event, final SessionModel session) {
        if (session.getType() == SessionType.UNKNOWN) {
            event.reply("⛔ Vous devez sélectionner un type de session.").queue();
            return;
        }

        session.start();
        this.goToPage(event, session);
    }

    private void handlePreviewButton(final ButtonInteractionEvent event, final SessionModel session)  {
        session.goPreviewPage();
        this.goToPage(event, session);
    }

    private void handleSubmitButton(final ButtonInteractionEvent event, final SessionModel session) throws SessionValidationException {
        if (!session.verify()) throw new SessionValidationException(event);

        session.end();
        this.sessionCache.remove(session.getEmployeeCid());

        // Save session within database
        this.sessionDatabase.addOne(session);
        this.sessionDatabase.save();

        event.editMessageEmbeds(SessionBuilderUtils.submitMessage(session))
                .setComponents(new ArrayList<>())
                .queue();
    }

    private void handlePageButton(final ButtonInteractionEvent event, final SessionModel session, final boolean isNext) {
        if (isNext) session.goNextPage();
        else session.goPrevPage();

        this.goToPage(event, session);
    }

    /* Utils */
    protected void goToPage(final ButtonInteractionEvent event, final SessionModel session) {
        final SessionPageType page = session.getCurrentPage();

        final MessageEmbed embed = (switch (page) {
            case ZONE -> SessionBuilderUtils.zoneMessage(session);
            case LOYALISTS, CIVILIANS, VORTIGAUNTS, ANTI_CITIZENS -> SessionBuilderUtils.participantMessage(session);
            case INFO -> SessionBuilderUtils.infoMessage(session);
            case TOKENS -> SessionBuilderUtils.earningsMessage(session);
            case PREVIEW -> SessionBuilderUtils.previewMessage(session);
        });

        final var callback = event.editMessageEmbeds(embed);
        if (page == SessionPageType.ZONE) {
            callback.setComponents(SessionBuilderUtils.zoneRows(session));
        } else {
            callback.setComponents(SessionBuilderUtils.pageRow(session));
        }

        callback.queue();
    }
}
