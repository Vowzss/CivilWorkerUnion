package com.oneliferp.cwu.modules.profile.utils;

import com.oneliferp.cwu.Models.CwuModel;
import com.oneliferp.cwu.Models.IdentityModel;
import com.oneliferp.cwu.exceptions.IdentityMalformedException;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.misc.CwuRank;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class OptionUtils {
    public static CwuModel createCwu(final SlashCommandInteractionEvent event) throws IdentityMalformedException {
        final IdentityModel identity = new IdentityModel(event.getOption("identity").getAsString());

        final String branch = event.getOption("branch").getAsString();
        final String rank = event.getOption("rank").getAsString();

        return new CwuModel(identity, CwuBranch.valueOf(branch), CwuRank.valueOf(rank));
    }
}
