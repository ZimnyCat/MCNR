package xyz.mcnr.utils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import xyz.mcnr.utils.Main;
import xyz.mcnr.utils.misc.CommandBase;

public class IgnoreList extends CommandBase {
    @Override
    public String name() {
        return "ignorelist";
    }

    @Override
    public String usage() {
        return "/ignorelist";
    }

    @Override
    public String description() {
        return "Список игнорируемых вами игроков";
    }

    @Override
    public void run(CommandSender sender, Command command, String label, String[] args) {
        String list = String.join(", ", Main.social.getSocial(sender.getName()).getIgnoreList());
        sender.sendMessage("Игнор-список: " + list);
    }
}
