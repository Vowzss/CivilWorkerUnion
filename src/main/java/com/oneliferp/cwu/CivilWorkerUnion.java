package com.oneliferp.cwu;

import com.oneliferp.cwu.Commands.CwuCommand;
import com.oneliferp.cwu.modules.profile.commands.ProfileCommand;
import com.oneliferp.cwu.Listeners.SlashCommandListener;
import com.oneliferp.cwu.Utils.Environment;
import com.oneliferp.cwu.modules.session.commands.SessionCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CivilWorkerUnion {
    private static CivilWorkerUnion instance;
    public static CivilWorkerUnion get() {
        if (instance == null) instance = new CivilWorkerUnion();
        return instance;
    }

    private final JDA jda;
    private final HashMap<String, CwuCommand> commands;

    public CivilWorkerUnion() {
        commands = new HashMap<>();

        jda = JDABuilder.createLight(Environment.getToken())
                .addEventListeners(new SlashCommandListener())
                .build();

        List.of(new ProfileCommand(), new SessionCommand()).forEach(command -> {
            this.commands.put(command.getName(), command);
            this.jda.upsertCommand(command.configure(command.toSlashCommand())).queue();
        });
    }

    public JDA getJda() {
        return this.jda;
    }

    public <T extends CwuCommand> T getCommand(final String name) {
        return (T) commands.get(name);
    }

    public static void main(String[] args) {
        CivilWorkerUnion.get();
    }
}