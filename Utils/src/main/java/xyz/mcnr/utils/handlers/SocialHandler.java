package xyz.mcnr.utils.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.mcnr.utils.Main;
import xyz.mcnr.utils.commands.AFK;
import xyz.mcnr.utils.misc.SocialData;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SocialHandler implements Listener {
    private static final String MESSAGE_TEMPLATE = "&a[%s -> %s]&f %s";
    private final Map<String, SocialData> socials = new HashMap<>();

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        SocialData social = getSocial(p.getName());

        if (!social.getMessageBacklog().isEmpty()) {
            p.sendMessage("У вас " + ChatColor.RED + social.getMessageBacklog().size() + ChatColor.RED + " непрочитанных сообщений");
            p.sendMessage("Прочитать — " + ChatColor.RED + "/read");
        }
    }

    @EventHandler
    private void onPlayerSendMessage(AsyncPlayerChatEvent event) {
        SocialData social = getSocial(event.getPlayer().getName());
        if (social.isAfk()) {
            AFK.exitAFK(event.getPlayer(), social);
        }

        for (SocialData data : socials.values()) {
            if (data.getOnlinePlayer() == null) continue;
            if (data.getIgnoreList().contains(event.getPlayer().getName())) {
                event.getRecipients().remove(data.getOnlinePlayer());
            }
        }

        if (event.getMessage().startsWith(">")) {
            event.setMessage(ChatColor.GREEN + event.getMessage());
        }
    }

    public SocialData getSocial(String name) {
        if (socials.containsKey(name)) {
            return socials.get(name);
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        if (player.getFirstPlayed() != 0) {
            addSocial(player);
        }
        return socials.get(name);
    }

    public Map<String, SocialData> getSocials() {
        return socials;
    }

    private void addSocial(OfflinePlayer player) {
        String name = player.getName();
        SocialData data = new SocialData(player);

        Path main = Main.getPluginFolder().toPath();
        Path anons = main.resolve("anon");
        Path jd = main.resolve("jd");
        Path backlog = main.resolve("backlog");

        Path path = main.resolve(name + ".txt");
        if (path.toFile().exists()) {
            try {
                for (String s : Files.readAllLines(path)) {
                    data.getIgnoreList().add(s);
                }
            } catch (IOException e) {
            }
        }

        if (anons.toFile().exists()) {
            Path p = anons.resolve(name + ".disabled");
            data.setAnonChat(!p.toFile().exists());
        } else anons.toFile().mkdir();

        if (jd.toFile().exists()) {
            Path p = jd.resolve(name + ".disabled");
            data.setHideJoinDates(p.toFile().exists());
        } else jd.toFile().mkdir();

        if (backlog.toFile().exists()) {
            Path p = backlog.resolve(name + ".txt");
            if (p.toFile().exists()) {
                try {
                    for (String s : Files.readAllLines(p)) {
                        data.getMessageBacklog().add(s);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else backlog.toFile().mkdir();
        socials.put(name, data);
    }

    public void send(CommandSender sender, String recipient, String message) {
        SocialData recipientSocial = getSocial(recipient);

        if (recipientSocial == null) {
            sender.sendMessage(ChatColor.RED + "Игрок не найден");
            return;
        }

        message = message.trim();
        if (message.isBlank()) {
            sender.sendMessage(ChatColor.RED + "Вы отправили пустое сообщение");
            return;
        }

        SocialData senderSocial = getSocial(sender.getName());

        if (senderSocial.isIgnoring(recipient)) {
            sender.sendMessage(ChatColor.RED + recipient + " игнорируется вами");
            return;
        }

        if (recipientSocial.isIgnoring(sender.getName())) {
            sender.sendMessage(ChatColor.RED + recipient + " игнорирует вас");
            return;
        }

        String formatted = format(MESSAGE_TEMPLATE, sender.getName(), recipient, message);

        if (recipientSocial.getOnlinePlayer() == null) {
            short limit = senderSocial.getOfflineMsgLimit();
            if (limit >= 0) {
                List<String> list = recipientSocial.getMessageBacklog();
                list.add(formatted);
                Path backlog = Main.getPluginFolder().toPath().resolve("backlog").resolve(recipient + ".txt");
                try {
                    Files.writeString(backlog, String.join("\n", list));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                sender.sendMessage(formatted);
                sender.sendMessage("Вы отправили сообщение игроку не в сети. Осталось: " + ChatColor.RED + limit);
            } else sender.sendMessage(ChatColor.RED + "Лимит на сообщения игрокам не в сети временно исчерпан");
            return;
        }

        if (recipientSocial.isAfk()) {
            if (recipientSocial.getAfkMessages().size() < 30) {
                sender.sendMessage(formatted);
                sender.sendMessage(ChatColor.RED + "Игрок " + recipient + " отошёл. Ваше сообщение будет доставлено позже");
                recipientSocial.getAfkMessages().add(formatted);
            } else {
                sender.sendMessage(ChatColor.RED + "Игрок " + recipient + " отошёл");
            }
        } else {
            sender.sendMessage(formatted);
            recipientSocial.getOnlinePlayer().sendMessage(formatted);
        }

        senderSocial.setLastRecipient(recipient);
        recipientSocial.setLastSender(sender.getName());
    }

    private static String format(String str, Object... objects) {
        return str.replace('&', ChatColor.COLOR_CHAR).formatted(objects);
    }
}
