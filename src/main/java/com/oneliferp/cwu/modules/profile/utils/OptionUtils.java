package com.oneliferp.cwu.modules.profile.utils;

import com.oneliferp.cwu.models.CwuModel;
import com.oneliferp.cwu.models.IdentityModel;
import com.oneliferp.cwu.utils.RegexUtils;
import com.oneliferp.cwu.exceptions.IdentityMalformedException;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.misc.CwuRank;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class OptionUtils {
    public static CwuModel createCwu(final SlashCommandInteractionEvent event) throws IdentityMalformedException {
        final IdentityModel identity = RegexUtils.parseIdentity(event.getOption("identity").getAsString());

        final String branch = event.getOption("branch").getAsString();
        final String rank = event.getOption("rank").getAsString();

        return new CwuModel(event.getUser().getIdLong(), identity, CwuBranch.valueOf(branch), CwuRank.valueOf(rank));
    }
}
