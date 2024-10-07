package xyz.mcnr.utils.commands.social;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.mcnr.utils.Main;
import xyz.mcnr.utils.misc.CommandBase;
import xyz.mcnr.utils.misc.SocialData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class Ignore extends CommandBase {

    @Override
    public String name() {
        return "ignore";
    }

    @Override
    public String usage() {
        return "/ignore <игрок>";
    }

    @Override
    public String description() {
        return "Скрывать/показывать сообщения игрока";
    }

    @Override
    public void run(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + usage());
            return;
        }

        String name = args[0].toLowerCase(Locale.ROOT);
        Player player = Bukkit.getPlayer(name);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Игрок не в сети");
            return;
        }

        SocialData social = Main.social.getSocial(sender.getName());
        if (System.currentTimeMillis() - social.getLastIgnoreUpdate() < 3000) {
            sender.sendMessage(ChatColor.RED + "Подождите перед использованием этой команды");
            return;
        }

        List<String> list = social.getIgnoreList();
        social.setLastIgnoreUpdate(System.currentTimeMillis());
        if (list.contains(name)) {
            list.remove(name);
            sender.sendMessage("Показываем сообщения от игрока %s".formatted(args[0]));
        } else {
            list.add(name);
            sender.sendMessage("Скрываем сообщения от игрока %s".formatted(args[0]));
        }

        Path path = Path.of(Main.getPluginFolder().getPath(), sender.getName() + ".txt");
        try {
            Files.writeString(path, String.join("\n", list));
        } catch (IOException e) {
        }
    }
}
