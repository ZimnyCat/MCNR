package xyz.mcnr.utils.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.mcnr.utils.Main;
import xyz.mcnr.utils.handlers.SocialHandler;
import xyz.mcnr.utils.misc.CommandBase;
import xyz.mcnr.utils.misc.SocialData;

import java.util.ArrayList;

public class Anon extends CommandBase {
    @Override
    public String name() {
        return "anon";
    }

    @Override
    public String usage() {
        return "/anon <сообщения>";
    }

    @Override
    public String description() {
        return "Анонимное сообщение";
    }

    @Override
    public void run(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + usage());
            return;
        }

        String message = String.join(" ", args).trim();
        if (message.startsWith(">")) {
            message = ChatColor.GREEN + message;
        }
        message = "<Anon> " + message;

        ArrayList<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        removeIgnoring(players, sender);

        for (Player p : players) {
            p.sendMessage(message);
        }
    }

    private void removeIgnoring(ArrayList<Player> players, CommandSender sender) {
        SocialHandler socials = Main.social;

        for (SocialData s : socials.getSocials().values()) {
            for (String name : s.getIgnoreList()) {
                if (name.equals(sender.getName())) {
                    players.remove(s.getPlayer());
                    break;
                }
            }
        }

    }
}
