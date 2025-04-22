package xyz.mcnr.utils.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.mcnr.utils.Main;
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

        for (Player p : getOnlinePlayers()) {
            p.sendMessage(message);
        }
    }

    private ArrayList<Player> getOnlinePlayers() {
        ArrayList<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        for (SocialData data : Main.social.getSocials().values()) {
            if (data.isAnonChat()) continue;
            players.remove(data.getPlayer());
        }

        return players;
    }
}
