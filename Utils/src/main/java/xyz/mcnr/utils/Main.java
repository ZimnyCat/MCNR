package xyz.mcnr.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.mcnr.utils.commands.*;
import xyz.mcnr.utils.commands.social.*;
import xyz.mcnr.utils.handlers.SocialHandler;
import xyz.mcnr.utils.misc.*;

import java.io.File;
import java.util.List;

public class Main extends JavaPlugin implements Listener {
    private static final List<String> BLACKLISTED_COMMANDS =
            List.of("/me", "/ver", "/icanhasbukkit", "/about", "/bukkit:", "/?", "/minecraft:");

    public static final RestartTask restart = new RestartTask();
    public static final TabTask tab = new TabTask();
    public static final SocialHandler social = new SocialHandler();
    private static File pluginFolder;

    List<CommandBase> commands = List.of(
            new Ignore(),
            new Whisper(),
            new Reply(),
            new Last(),
            new IgnoreList(),
            new Joins(),
            new Restart(),
            new AFK()
    );

    // регистрация ивентов, запуск задач авторестарта и обновления таба
    @Override
    public void onEnable() {
        pluginFolder = getDataFolder();
        pluginFolder.mkdirs();
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(social, this);

        restart.setStartTime(System.currentTimeMillis());
        restart.runTaskTimer(this, 20, 20);
        tab.runTaskTimer(this, 20, 20);
    }

    // обработка команд плагина
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("help")) {
            sender.sendMessage("\n");
            commands.forEach(cmd ->
                    sender.sendMessage(cmd.description() + " — " + ChatColor.RED + cmd.usage())
            );
        }
        commands.forEach(cmd -> {
            if (command.getName().equalsIgnoreCase(cmd.name())) cmd.run(sender, command, label, args);
        });
        return true;
    }

    // вывод /help при первом заходе
    @EventHandler
    public void firstJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            player.sendMessage(ChatColor.RED + "\nMinecraftNoRules");
            player.sendMessage("Ванильное выживание на карте в 20000 на 20000 блоков без правил и вмешательства администрации");
            player.sendMessage("Команды сервера — " + ChatColor.RED + "/help");
        }
    }

    // LuckPerms? а может лучше...
    @EventHandler
    public void cancelCommands(PlayerCommandPreprocessEvent event) {
        BLACKLISTED_COMMANDS.forEach(element -> {
            if (event.getMessage().toLowerCase().startsWith(element)) event.setCancelled(true);
        });
    }

    @EventHandler
    public void cancelCommands(PlayerCommandSendEvent event) {
        event.getCommands().removeIf(c -> {
            for (String s : BLACKLISTED_COMMANDS) {
                if (c.toLowerCase().startsWith(s.substring(1)))
                    return true;
            }
            return false;
        });
    }

    // установка сообщений в табе
    @EventHandler
    public void join(PlayerJoinEvent event) {
        event.getPlayer().setPlayerListHeader(ChatColor.RED + "MinecraftNoRules" + ChatColor.WHITE + "\ntg: @mcnrxyz\n");
        tab.update(event.getPlayer());
    }

    // потолок скорости
    @EventHandler
    public void speedCap(PlayerMoveEvent event) {
        if (event.getTo() == null) return;

        double x = event.getTo().getX() - event.getFrom().getX();
        double z = event.getTo().getZ() - event.getFrom().getZ();
        double distance = Math.sqrt(x*x + z*z);
        
        if (distance > 2.5) event.setCancelled(true);
    }

    public static File getPluginFolder() {
        return pluginFolder;
    }
}
