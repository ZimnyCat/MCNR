package xyz.mcnr.utils.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import xyz.mcnr.utils.Main;
import xyz.mcnr.utils.misc.CommandBase;
import xyz.mcnr.utils.misc.SocialData;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JoinDate extends CommandBase {
    @Override
    public String name() {
        return "joindate";
    }

    @Override
    public String usage() {
        return "/joindate <игрок>";
    }

    @Override
    public String description() {
        return "Первый и последний заходы игрока";
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void run(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + usage());
            return;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
        if (player.getFirstPlayed() == 0) {
            sender.sendMessage(ChatColor.RED + "Игрок не найден");
            return;
        }

        SocialData social = Main.social.getSocial(sender.getName());
        if (social.isHidingJoinDates()) {
            sender.sendMessage("\nИнформация о заходах " + player.getName() + " скрыта командой /togglejd");
            return;
        }

        long first = player.getFirstPlayed();
        long last = player.getLastPlayed();

        sender.sendMessage("\n" + player.getName());
        sender.sendMessage("Первый заход: " + sdf.format(new Date(first)) + getTimeDiff(first));
        if (player.isOnline())
            sender.sendMessage("Сейчас на сервере");
        else
            sender.sendMessage("Последний заход: " + sdf.format(new Date(last)) + getTimeDiff(last));
    }

    private String getTimeDiff(long time) {
        long diff = System.currentTimeMillis() - time;

        if (diff < 3600000) return " (" + String.format("%.0f", Math.floor(diff / 60000f)) + " мин. назад)";
        if (diff < 86400000) return " (" + String.format("%.0f", Math.floor(diff / 3600000f)) + " ч. назад)";
        return " (" + String.format("%.0f", Math.floor(diff / 86400000f)) + " д. назад)";
    }
}
