package xyz.mcnr.utils.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import xyz.mcnr.utils.Main;
import xyz.mcnr.utils.misc.CommandBase;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Read extends CommandBase {
    @Override
    public String name() {
        return "read";
    }

    @Override
    public String usage() {
        return "/read";
    }

    @Override
    public String description() {
        return "Непрочитанные сообщения";
    }

    @Override
    public void run(CommandSender sender, Command command, String label, String[] args) {
        List<String> backlog = Main.social.getSocial(sender.getName()).getMessageBacklog();

        if (backlog.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Непрочитанных сообщений нет");
            return;
        }

        for (String msg : backlog) sender.sendMessage(msg);

        backlog.clear();
        try {
            Files.deleteIfExists(Main.getPluginFolder().toPath().resolve("backlog").resolve(sender.getName() + ".txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
