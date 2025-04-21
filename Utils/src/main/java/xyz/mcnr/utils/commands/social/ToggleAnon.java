package xyz.mcnr.utils.commands.social;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import xyz.mcnr.utils.Main;
import xyz.mcnr.utils.misc.CommandBase;
import xyz.mcnr.utils.misc.SocialData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ToggleAnon extends CommandBase {
    @Override
    public String name() {
        return "toggleanon";
    }

    @Override
    public String usage() {
        return "/toggleanon";
    }

    @Override
    public String description() {
        return "Включить/выключить анонимный чат";
    }

    @Override
    public void run(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 0) {
            sender.sendMessage(ChatColor.RED + usage());
            return;
        }

        SocialData social = Main.social.getSocial(sender.getName());
        if (System.currentTimeMillis() - social.getLastAnonChatUpdate() < 3000) {
            social.setLastAnonChatUpdate(System.currentTimeMillis()); // Nehui
            sender.sendMessage(ChatColor.RED + "Слишком быстро!");
            return;
        }

        social.setLastAnonChatUpdate(System.currentTimeMillis());

        try {
            if (social.isAnonChat()) {
                disableAnonChat(sender, social);
            } else {
                enableAnonChat(sender, social);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disableAnonChat(CommandSender sender, SocialData social) throws IOException {
        sender.sendMessage("Анонимный чат выключен");
        social.setAnonChat(false);

        Files.createFile(path(sender));
    }

    private void enableAnonChat(CommandSender sender, SocialData social) throws IOException {
        sender.sendMessage("Анонимный чат включен");
        social.setAnonChat(true);

        Files.deleteIfExists(path(sender));
    }

    private Path path(CommandSender sender) {
        Path anons = Main.getPluginFolder().toPath().resolve("anon");
        return anons.resolve(sender.getName() + ".disabled");
    }
}
