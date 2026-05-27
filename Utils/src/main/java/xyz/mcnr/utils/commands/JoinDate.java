package xyz.mcnr.utils.commands;

import com.google.gson.Gson;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import xyz.mcnr.utils.Main;
import xyz.mcnr.utils.misc.CommandBase;
import xyz.mcnr.utils.misc.SocialData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    Gson gson = new Gson();

    @Override
    public void run(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof RemoteConsoleCommandSender) {
            sendToRCON(sender, args);
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + usage());
            return;
        }

        SocialData social = Main.social.getSocial(args[0]);

        if (social == null) {
            sender.sendMessage(ChatColor.RED + "Игрок не найден");
            return;
        }

        OfflinePlayer player = social.getPlayer();

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
        sender.sendMessage("Время игры: " + player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 72000 + " ч.");
    }

    private void sendToRCON(CommandSender sender, String[] args) {
        Map<String, Object> response = new HashMap<>();

        if (args.length == 0) {
            response.put("error", "Игрок не указан");
            sender.sendMessage(gson.toJson(response));
            return;
        }

        SocialData social = Main.social.getSocial(args[0]);

        if (social == null) {
            response.put("error", "Игрок не найден");
            sender.sendMessage(gson.toJson(response));
            return;
        }

        OfflinePlayer player = social.getPlayer();

        if (social.isHidingJoinDates()) {
            response.put("error", "Скрыто игроком");
            sender.sendMessage(gson.toJson(response));
            return;
        }

        long first = player.getFirstPlayed();
        long last = player.getLastPlayed();

        response.put("name", args[0]);
        response.put("first", first);
        if (player.isOnline())
            response.put("last", "Online");
        else
            response.put("last", last);
        response.put("playtime", player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 72000);

        sender.sendMessage(gson.toJson(response));
    }

    private String getTimeDiff(long time) {
        long diff = System.currentTimeMillis() - time;

        if (diff < 3600000) return " (" + (int)(diff / 60000f) + " мин. назад)";
        if (diff < 86400000) return " (" + (int)(diff / 3600000f) + " ч. назад)";
        return " (" + (int)(diff / 86400000f) + " д. назад)";
    }
}
