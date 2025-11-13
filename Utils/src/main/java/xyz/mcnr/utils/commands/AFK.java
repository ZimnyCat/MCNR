package xyz.mcnr.utils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import xyz.mcnr.utils.Main;
import xyz.mcnr.utils.misc.CommandBase;
import xyz.mcnr.utils.misc.SocialData;

import java.util.List;

public class AFK extends CommandBase {
    @Override
    public String name() {
        return "afk";
    }

    @Override
    public String usage() {
        return "/afk";
    }

    @Override
    public String description() {
        return "Включить/выключить статус AFK";
    }

    @Override
    public void run(CommandSender sender, Command command, String label, String[] args) {
        SocialData social = Main.social.getSocial(sender.getName());
        social.setAfk(!social.isAfk());

        if (social.isAfk()) {
            sender.sendMessage("Вы отошли");
            return;
        }

        exitAFK(sender, social);
    }

    public static void exitAFK(CommandSender target, SocialData social) {
        List<String> msgs = social.getAfkMessages();
        target.sendMessage("Вы вернулись");
        if (!msgs.isEmpty()) {
            target.sendMessage("Новые сообщения:");
            msgs.forEach(target::sendMessage);
            msgs.clear();
        }
        social.setAfk(false);
    }
}
