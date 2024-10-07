package xyz.mcnr.utils.commands.social;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.mcnr.utils.Main;
import xyz.mcnr.utils.misc.CommandBase;
import xyz.mcnr.utils.misc.SocialData;

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

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Игрок не в сети");
            return;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        Main.social.send(sender, player, message);
    }

}
