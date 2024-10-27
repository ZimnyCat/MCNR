package xyz.mcnr.utils.handlers;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.mcnr.utils.Main;
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
        SocialData data = new SocialData(event.getPlayer());
        socials.put(event.getPlayer().getName(), data);
        Path path = Main.getPluginFolder().toPath().resolve(event.getPlayer().getName() + ".txt");
        if (path.toFile().exists()) {
            try {
                for (String s : Files.readAllLines(path)) {
                    data.getIgnoreList().add(s);
                }
            } catch (IOException e) {
            }
        }
    }


    @EventHandler
    private void onQuit(PlayerJoinEvent event) {
        socials.remove(event.getPlayer().getName());
    }

    @EventHandler
    private void onPlayerSendMessage(AsyncPlayerChatEvent event) {
        for (SocialData data : socials.values()) {
            if (data.getIgnoreList().contains(event.getPlayer().getName())) {
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

    public void send(CommandSender sender, CommandSender recipient, String message) {
        message = message.trim();
        if (message.isBlank()) {
            sender.sendMessage("Вы отправили пустое сообщение");
            return;
        }

        SocialData senderSocial = getSocial(sender.getName());
        SocialData recipientSocial = getSocial(recipient.getName());

        if (senderSocial.isIgnoring(recipient)) {
            sender.sendMessage(recipient.getName() + " игнорируется вами");
            return;
        }

        if (recipientSocial.isIgnoring(sender)) {
            sender.sendMessage(recipient.getName() + " игнорирует вас");
            return;
        }

        sender.sendMessage(format(MESSAGE_TEMPLATE, sender.getName(), recipient.getName(), message));
        recipient.sendMessage(format(MESSAGE_TEMPLATE, sender.getName(), recipient.getName(), message));

        senderSocial.setLastRecipient(recipient.getName());
        recipientSocial.setLastSender(sender.getName());
    }

    private static String format(String str, Object... objects) {
        return str.replace('&', ChatColor.COLOR_CHAR).formatted(objects);
    }
}
