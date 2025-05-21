package xyz.mcnr.utils;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.mcnr.utils.commands.*;
import xyz.mcnr.utils.commands.social.*;
import xyz.mcnr.utils.handlers.CrashHandler;
import xyz.mcnr.utils.handlers.ReportersHandler;
import xyz.mcnr.utils.handlers.SocialHandler;
import xyz.mcnr.utils.misc.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main extends JavaPlugin implements Listener {
    private static final List<String> BLACKLISTED_COMMANDS =
            List.of("/me", "/ver", "/icanhasbukkit", "/about", "/bukkit:", "/?", "/minecraft:");

    public static final RestartTask restart = new RestartTask();
    public static final TabTask tab = new TabTask();
    public static final SpeedTask speed = new SpeedTask();

    public static final SocialHandler social = new SocialHandler();
    public static final ReportersHandler reporters = new ReportersHandler();
    public static final CrashHandler antiCrash = new CrashHandler();

    private static File pluginFolder;

    List<CommandBase> commands = List.of(
            new Ignore(),
            new Whisper(),
            new Reply(),
            new Last(),
            new AFK(),
            new IgnoreList(),
            new Report(),
            new JoinDate(),
            new Restart(),
            new Anon(),
            new ToggleAnon()
    );

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().kickOnPacketException(true); // анти хуесос система
    }

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
        speed.runTaskTimer(this, 5, 5);

        try {
            reporters.load();
        } catch (IOException e) {
        }

        PacketEvents.getAPI().getEventManager().registerListener(new CrashHandler(), PacketListenerPriority.LOWEST);
        PacketEvents.getAPI().init();
    }

    @Override
    public void onDisable() {
        try {
            reporters.save();
        } catch (IOException e) {
        }
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
            player.sendMessage("Анархическое ванильное выживание на карте в 30000 на 30000 блоков");
            player.sendMessage("Команды сервера — " + ChatColor.RED + "/help");
        }

        restart.checkPlayerCount();
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
            if (c.contains(":"))
                return true;

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
        double distance = Math.hypot(x, z);

        double maxSpeed = 2.5;
        if (speed.usedTrident.containsKey(event.getPlayer())) maxSpeed = 3.2;
        if (event.getPlayer().getVehicle() instanceof AbstractHorse horse) {
            maxSpeed = 0.9;

            PotionEffect speed = horse.getPotionEffect(PotionEffectType.SPEED);
            if (speed != null) {
                maxSpeed *= 1 + 0.2f * (speed.getAmplifier() + 1);
            }
        }

        if (distance > maxSpeed) {
            if (event.getPlayer().getVehicle() != null) {
                speed.dismounted.put(event.getPlayer(), System.currentTimeMillis());
                event.getPlayer().leaveVehicle();
            }

            event.setCancelled(true);
        }
    }

    @EventHandler private void onMount(EntityMountEvent event) {
        if (event.getEntity() instanceof Player player && speed.dismounted.containsKey(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void trident(PlayerRiptideEvent event) {
        speed.usedTrident.put(event.getPlayer(), System.currentTimeMillis());
    }

    public static File getPluginFolder() {
        return pluginFolder;
    }
}
