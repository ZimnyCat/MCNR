package xyz.mcnr.utils.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import xyz.mcnr.utils.Main;
import xyz.mcnr.utils.misc.CommandBase;

import java.util.Arrays;

public class Whisper extends CommandBase {
    @Override
    public String name() {
        return "w";
    }

    @Override
    public String usage() {
        return "/w <игрок> <сообщение>";
    }

    @Override
    public String description() {
        return "Личное сообщение";
    }

    @Override
    public void run(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + usage());
            return;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        Main.social.send(sender, args[0], message);
    }

}
