package xyz.mcnr.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.mcnr.utils.commands.*;
import xyz.mcnr.utils.misc.*;

import java.util.List;

public class Main extends JavaPlugin implements Listener {
    public static RestartTask restart = new RestartTask();
    public static TabTask tab = new TabTask();

    List<CommandBase> commands = List.of(
            new Joins(),
            new Restart()
    );

    // регистрация ивентов, запуск задач авторестарта и обновления таба
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        restart.setStartTime(System.currentTimeMillis());
        restart.runTaskTimer(this, 20, 20);
        tab.runTaskTimer(this, 20, 20);
    }

    // обработка команд плагина
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("help") && sender instanceof Player) help((Player) sender);
        commands.forEach(cmd -> {
            if (command.getName().equalsIgnoreCase(cmd.name())) cmd.run(sender, command, label, args);
        });
        return true;
    }

    // вывод /help при первом заходе
    @EventHandler
    public void firstJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPlayedBefore()) help(event.getPlayer());
    }

    // вывод /help
    public void help(Player player) {
        player.sendMessage(ChatColor.RED + "\nMinecraftNoRules");
        player.sendMessage("Ванильное выживание на карте в 20000 на 20000 блоков без правил и вмешательства администрации");
        player.sendMessage("Игнор в чате - " + ChatColor.RED + "/ignore <игрок>");
        player.sendMessage("Личное сообщение - " + ChatColor.RED + "/msg <игрок> <сообщение>");
        player.sendMessage("Ответ на ЛС - " + ChatColor.RED + "/r <сообщение>");
        commands.forEach(cmd ->
            player.sendMessage(cmd.description() + " - " + ChatColor.RED + cmd.usage())
        );
    }

    // LuckPerms? а может лучше...
    @EventHandler
    public void cancelCommands(PlayerCommandPreprocessEvent event) {
        List.of("/me", "/ver", "/icanhasbukkit", "/about", "/bukkit:", "/?").forEach(element -> {
            if (event.getMessage().toLowerCase().startsWith(element)) event.setCancelled(true);
        });
    }

    // установка сообщений в табе
    @EventHandler
    public void join(PlayerJoinEvent event) {
        event.getPlayer().setPlayerListHeader(ChatColor.RED + "MinecraftNoRules" + ChatColor.WHITE + "\ntg: @mcnrxyz\n");
        tab.update(event.getPlayer());
    }
}
