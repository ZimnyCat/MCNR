package xyz.mcnr.utils.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import xyz.mcnr.utils.Main;
import xyz.mcnr.utils.misc.CommandBase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BugReport extends CommandBase {
    @Override
    public String name() {
        return "bugreport";
    }

    @Override
    public String usage() {
        return "/bugreport <баг-репорт>";
    }

    @Override
    public String description() {
        return "Сообщить о технической ошибке";
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    public void run(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + usage());
            return;
        }

        if (!Main.reporters.check(sender.getName())) {
            sender.sendMessage(ChatColor.RED + "Отправлять репорты можно раз в сутки");
            return;
        }

        try {
            Files.write(Main.reporters.reports.toPath(), (sdf.format(new Date(System.currentTimeMillis())) + " " + String.join(" ", args) + "\n").getBytes(), StandardOpenOption.APPEND);
            Main.reporters.add(sender.getName());
            sender.sendMessage("Репорт отправлен");
        } catch (IOException e) {
        }
    }
}
