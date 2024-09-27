package xyz.mcnr.utils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import xyz.mcnr.utils.misc.CommandBase;
import xyz.mcnr.utils.Main;

public class Restart extends CommandBase {
    @Override
    public String name() {
        return "rst";
    }

    @Override
    public String usage() {
        return "/rst";
    }

    @Override
    public String description() {
        return "Время до перезапуска сервера";
    }

    @Override
    public void run(CommandSender sender, Command command, String lable, String[] args) {
        sender.sendMessage(
                "Через " + String.format("%.0f", Math.floor(((Main.restart.restartTime * 1000) - (System.currentTimeMillis() - Main.restart.startTime))/60000f)) + " мин."
        );
    }
}
