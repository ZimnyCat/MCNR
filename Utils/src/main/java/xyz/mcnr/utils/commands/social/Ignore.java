package xyz.mcnr.utils.commands.social;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import xyz.mcnr.utils.Main;
import xyz.mcnr.utils.misc.CommandBase;
import xyz.mcnr.utils.misc.SocialData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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

        if (Bukkit.getOfflinePlayer(args[0]).getFirstPlayed() == 0) {
            sender.sendMessage(ChatColor.RED + "Игрок не найден");
            return;
        }

        SocialData social = Main.social.getSocial(sender.getName());

        if (social.isFileUpdateNotOK()) {
            return;
        }

        List<String> list = social.getIgnoreList();
        if (list.contains(args[0])) {
            list.remove(args[0]);
            sender.sendMessage("Сообщения от %s показываются".formatted(args[0]));
        } else {
            list.add(args[0]);
            sender.sendMessage("Сообщения от %s скрыты".formatted(args[0]));
        }

        Path path = Path.of(Main.getPluginFolder().getPath(), sender.getName() + ".txt");
        try {
            Files.writeString(path, String.join("\n", list));
        } catch (IOException e) {
        }
    }
}
