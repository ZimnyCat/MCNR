package xyz.mcnr.utils.handlers;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.mcnr.utils.Main;
import xyz.mcnr.utils.commands.social.AFK;
import xyz.mcnr.utils.misc.SocialData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class SocialHandler implements Listener {
    private static final String MESSAGE_TEMPLATE = "&a[%s -> %s]&f %s";
    private final Map<String, SocialData> socials = new HashMap<>();

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        String name = event.getPlayer().getName();

        SocialData data = new SocialData(event.getPlayer());
        socials.put(name, data);
        Path main = Main.getPluginFolder().toPath();
        Path anons = main.resolve("anon");

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
        } else {
            anons.toFile().mkdir();
        }
    }


    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        socials.remove(event.getPlayer().getName());
    }

    @EventHandler
    private void onPlayerSendMessage(AsyncPlayerChatEvent event) {
        SocialData social = getSocial(event.getPlayer().getName());
        if (social.isAfk()) {
            AFK.exitAFK(event.getPlayer(), social);
        }

        for (SocialData data : socials.values()) {
            if (data.getIgnoreList().contains(event.getPlayer().getName().toLowerCase())) {
                event.getRecipients().remove(data.getPlayer());
            }
        }

        if (event.getMessage().startsWith(">")) {
            event.setMessage(ChatColor.GREEN + event.getMessage());
        }
    }

    public SocialData getSocial(String name) {
        return socials.get(name);
    }

    public Map<String, SocialData> getSocials() {
        return socials;
    }

    public void send(CommandSender sender, CommandSender recipient, String message) {
        message = message.trim();
        if (message.isBlank()) {
            sender.sendMessage(ChatColor.RED + "Вы отправили пустое сообщение");
            return;
        }

        SocialData senderSocial = getSocial(sender.getName());
        SocialData recipientSocial = getSocial(recipient.getName());

        if (senderSocial.isIgnoring(recipient)) {
            sender.sendMessage(ChatColor.RED + recipient.getName() + " игнорируется вами");
            return;
        }

        if (recipientSocial.isIgnoring(sender)) {
            sender.sendMessage(ChatColor.RED + recipient.getName() + " игнорирует вас");
            return;
        }

        String formatted = format(MESSAGE_TEMPLATE, sender.getName(), recipient.getName(), message);

        if (recipientSocial.isAfk()) {
            if (recipientSocial.getAfkMessages().size() < 30) {
                sender.sendMessage(formatted);
                sender.sendMessage(ChatColor.RED + "Игрок " + recipient.getName() + " отошёл. Ваше сообщение будет доставлено позже");
                recipientSocial.getAfkMessages().add(formatted);
            } else {
                sender.sendMessage(ChatColor.RED + "Игрок " + recipient.getName() + " отошёл");
            }
        } else {
            sender.sendMessage(formatted);
            recipient.sendMessage(formatted);
        }

        senderSocial.setLastRecipient(recipient.getName());
        recipientSocial.setLastSender(sender.getName());
    }

    private static String format(String str, Object... objects) {
        return str.replace('&', ChatColor.COLOR_CHAR).formatted(objects);
    }
}
