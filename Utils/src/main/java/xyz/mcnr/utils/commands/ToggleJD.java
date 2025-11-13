package xyz.mcnr.utils.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import xyz.mcnr.utils.Main;
import xyz.mcnr.utils.misc.CommandBase;
import xyz.mcnr.utils.misc.SocialData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ToggleJD extends CommandBase {
    @Override
    public String name() {
        return "togglejd";
    }

    @Override
    public String usage() {
        return "/togglejd";
    }

    @Override
    public String description() {
        return "Скрыть/раскрыть информацию о ваших заходах в /joindate";
    }

    @Override
    public void run(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 0) {
            sender.sendMessage(ChatColor.RED + usage());
            return;
        }

        SocialData social = Main.social.getSocial(sender.getName());
        if (social.isFileUpdateNotOK()) {
            return;
        }

        try {
            if (social.isHidingJoinDates()) {
                Files.deleteIfExists(path(sender));
                sender.sendMessage("Информация о ваших заходах показывается");
            } else {
                Files.createFile(path(sender));
                sender.sendMessage("Информация о ваших заходах скрыта");
            }
            social.toggleHideJoinDates();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path path(CommandSender sender) {
        Path jd = Main.getPluginFolder().toPath().resolve("jd");
        return jd.resolve(sender.getName() + ".disabled");
    }
}
