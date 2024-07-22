package com.oneliferp.cwu.modules.profile.utils;

import com.oneliferp.cwu.Models.CwuModel;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.misc.CwuRank;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import java.util.Objects;

public class OptionUtils {
    public static CwuModel createCwu(final SlashCommandInteractionEvent event, final Long userID) {

        final String firstName = Objects.requireNonNull(event.getOption("firstname")).getAsString();
        final String lastName = Objects.requireNonNull(event.getOption("lastname")).getAsString();
        final String cid = Objects.requireNonNull(event.getOption("cid")).getAsString();

        final String branch = Objects.requireNonNull(event.getOption("branch")).getAsString();
        final String rank = Objects.requireNonNull(event.getOption("rank")).getAsString();

        return new CwuModel(userID, firstName, lastName, cid, CwuBranch.valueOf(branch), CwuRank.valueOf(rank));
    }
}
