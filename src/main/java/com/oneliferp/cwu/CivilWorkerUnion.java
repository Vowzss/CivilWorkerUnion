package com.oneliferp.cwu;

import com.oneliferp.cwu.Commands.ClearCommand;
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
        if (instance == null)  {
            instance = new CivilWorkerUnion();
        }

        return instance;
    }

    private final JDA jda;
    private final HashMap<String, CwuCommand> nameToCommandMap;

    public CivilWorkerUnion() {
        nameToCommandMap = new HashMap<>();

        final List<CwuCommand> commands = new ArrayList<>();
        commands.add(new ClearCommand());
        commands.add(new ProfileCommand());
        commands.add(new SessionCommand());

        commands.forEach(cmd -> nameToCommandMap.put(cmd.getName(), cmd));

        jda = JDABuilder.createLight(Environment.getToken())
                .addEventListeners(new SlashCommandListener())
                .build();
    }

    private void setup() {
        final CommandListUpdateAction commands = this.jda.updateCommands();
        nameToCommandMap.values().forEach(command -> commands.addCommands(command.configure(command.toSlashCommand())));
        commands.addCommands(Commands.slash("shutdown", "Debug"));
        commands.queue();
    }

    public void shutdown() {
        this.jda.shutdown();
    }

    public <T extends CwuCommand> T getCommand(String name) {
        return (T) nameToCommandMap.get(name);
    }

    public static void main(String[] args) {
        CivilWorkerUnion.get().setup();
    }
}