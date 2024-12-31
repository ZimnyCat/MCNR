package xyz.mcnr.door;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;

public class Main extends JavaPlugin implements Listener {

    private static final List<String> WHITELIST = List.of("/l", "/reg", "/join", "/change");
    HashMap<Player, Long> joinSenders = new HashMap<>();
    HashMap<Player, Long> kickList = new HashMap<>();
    long startTime;

    @Override
    public void onEnable() {
        // регистрация ивентов и канала BungeeCord Plugin Messaging Channel
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // авторестарт
        startTime = System.currentTimeMillis();
        BukkitRunnable restart = new BukkitRunnable() {
            @Override
            public void run() {
                if ((System.currentTimeMillis() - startTime) > 14400000) getServer().shutdown();
            }
        };
        restart.runTaskTimer(this, 20, 20);

        // кик за афк (60 сек)
        BukkitRunnable kick = new BukkitRunnable() {
            @Override
            public void run() {
                kickList.forEach((player, ms) -> {
                    if (System.currentTimeMillis() - ms > 60000 && player.isOnline())
                        player.kickPlayer("AFK");
                });
            }
        };
        kick.runTaskTimer(this, 20, 20);
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this, "BungeeCord");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("join") && sender instanceof Player) {
            kickList.put((Player) sender, System.currentTimeMillis());
            sender.sendMessage("Подключение...");

            // подключение sender к серверу выживания через канал BungeeCord
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF("surv");

            ((Player) sender).sendPluginMessage(this, "BungeeCord", out.toByteArray());
            joinSenders.put((Player) sender, System.currentTimeMillis());
        }
        return true;
    }

    // задержа для /join
    @EventHandler
    public void cooldown(PlayerCommandPreprocessEvent event) {
        if (!joinSenders.containsKey(event.getPlayer()) || !event.getMessage().toLowerCase().startsWith("/join")) return;

        long diff = (System.currentTimeMillis() - joinSenders.get(event.getPlayer()));
        if (diff < 20000) {
            event.getPlayer().sendMessage(
                    "Слишком часто! Подождите ещё " + ChatColor.RED + String.format("%.0f", 20 - Math.floor(diff/1000f)) + " сек."
            );
            event.setCancelled(true);
        }
    }

    // вайтлист команд
    @EventHandler
    public void enforceWhitelist(PlayerCommandPreprocessEvent event) {
        for (String cmd : WHITELIST) {
            if (event.getMessage().toLowerCase().startsWith(cmd)) return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void enforceWhitelist(PlayerCommandSendEvent event) {
        event.getCommands().removeIf(c -> {
            for (String s : WHITELIST) {
                if (c.toLowerCase().startsWith(s.substring(1)))
                    return false;
            }
            return true;
        });
    }

    @EventHandler
    public void disableChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        kickList.put(event.getPlayer(), System.currentTimeMillis());
    }
}
