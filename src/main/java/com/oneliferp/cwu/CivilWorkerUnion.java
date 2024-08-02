package com.oneliferp.cwu;

import com.oneliferp.cwu.commands.CwuCommand;
import com.oneliferp.cwu.modules.profile.commands.ProfileCommand;
import com.oneliferp.cwu.listeners.SlashCommandListener;
import com.oneliferp.cwu.modules.report.command.ReportCommand;
import com.oneliferp.cwu.utils.Environment;
import com.oneliferp.cwu.modules.session.commands.SessionCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

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

        List.of(new ProfileCommand(), new SessionCommand(), new ReportCommand()).forEach(command -> {
            this.commands.put(command.getName(), command);
            this.jda.upsertCommand(command.configure(command.toSlashCommand())).queue();
        });
    }

    public <T extends CwuCommand> T getCommand(final String name) {
        return (T) commands.get(name);
    }

    public static void main(String[] args) {
        CivilWorkerUnion.get();
    }
}