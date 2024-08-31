package com.oneliferp.cwu;

import com.oneliferp.cwu.commands.CwuCommand;
import com.oneliferp.cwu.commands.exceptions.CommandNotFoundException;
import com.oneliferp.cwu.commands.modules.manage.ManageCommand;
import com.oneliferp.cwu.commands.modules.panel.PanelCommand;
import com.oneliferp.cwu.commands.modules.profile.ProfileCommand;
import com.oneliferp.cwu.commands.modules.report.ReportCommand;
import com.oneliferp.cwu.commands.modules.session.SessionCommand;
import com.oneliferp.cwu.listeners.ButtonListener;
import com.oneliferp.cwu.listeners.CommandListener;
import com.oneliferp.cwu.listeners.MenuSelectionListener;
import com.oneliferp.cwu.listeners.ModalListener;
import com.oneliferp.cwu.utils.Environment;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import java.util.HashMap;
import java.util.List;

public class CivilWorkerUnion {
    private static CivilWorkerUnion instance;
    private final JDA jda;
    private final HashMap<String, CwuCommand> commands;
    public CivilWorkerUnion() {
        commands = new HashMap<>();

        try {
            jda = JDABuilder.createLight(Environment.getToken())
                    .addEventListeners(new CommandListener(), new ButtonListener(), new ModalListener(), new MenuSelectionListener())
                    .build().awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        List.of(new ProfileCommand(), new SessionCommand(), new ReportCommand(), new ManageCommand(), new PanelCommand()).forEach(command -> {
            this.commands.put(command.getName(), command);
            final var cmd = command.configure(command.toSlashCommand());
            this.jda.upsertCommand(cmd).queue();
        });

        System.out.printf("Registered: %d commands.\n", this.commands.size());
    }

    public static CivilWorkerUnion get() {
        if (instance == null) instance = new CivilWorkerUnion();
        return instance;
    }

    public static void main(String[] args) {
        CivilWorkerUnion.get();
    }

    public <T extends CwuCommand> T getCommandFromName(final String name) throws CommandNotFoundException {
        if (!commands.containsKey(name)) throw new CommandNotFoundException();
        return (T) commands.get(name);
    }

    public <T extends CwuCommand> T getCommandFromId(final String id) throws CommandNotFoundException {
        try {
            return (T) this.commands.entrySet().stream().filter(c -> c.getValue().getID().equals(id)).findFirst().get().getValue();
        } catch (Exception ex) {
            throw new CommandNotFoundException();
        }
    }

    public JDA getJda() {
        return this.jda;
    }
}