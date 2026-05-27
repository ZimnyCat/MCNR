package xyz.mcnr.utils.commands;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import xyz.mcnr.utils.Main;
import xyz.mcnr.utils.misc.CommandBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Stats extends CommandBase {
    @Override
    public String name() {
        return "stats";
    }

    @Override
    public String usage() {
        return "/stats";
    }

    @Override
    public String description() {
        return "Статистика сервера";
    }

    Gson gson = new Gson();

    @Override
    public void run(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof RemoteConsoleCommandSender) {
            sendToRCON(sender);
            return;
        }

        sender.sendMessage("Статистика сервера");
        sender.sendMessage("Игроков за всё время: " + Bukkit.getOfflinePlayers().length);
        sender.sendMessage("Размер карты: " + Math.round(Main.size / (1024.0 * 1024.0 * 1024.0) * 10.0) / 10.0 + " GB");
        sender.sendMessage("Последний перезапуск: " + String.format("%.0f мин. назад", (System.currentTimeMillis() - Main.restart.startTime) / 60000f));
        sender.sendMessage(
                "До следующего перезапуска: " + String.format("%.0f мин.", Math.floor(((Main.restart.restartTime * 1000) - (System.currentTimeMillis() - Main.restart.startTime))/60000f))
        );
    }

    private void sendToRCON(CommandSender sender) {
        Map<String, Object> response = new HashMap<>();
        List<String> players = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> players.add(player.getName()));

        response.put("total", Bukkit.getOfflinePlayers().length);
        response.put("online", Bukkit.getOnlinePlayers().size());
        response.put("onlinePlayers", players);
        response.put("size", Math.round(Main.size / (1024.0 * 1024.0 * 1024.0) * 10.0) / 10.0);
        response.put("uptime", (int)((System.currentTimeMillis() - Main.restart.startTime) / 60000f));
        response.put("restart", (int)(((Main.restart.restartTime * 1000) - (System.currentTimeMillis() - Main.restart.startTime))/60000f));
        sender.sendMessage(gson.toJson(response));
    }
}
