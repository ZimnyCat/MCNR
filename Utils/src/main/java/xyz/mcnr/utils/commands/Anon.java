package xyz.mcnr.utils.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import xyz.mcnr.utils.Main;
import xyz.mcnr.utils.misc.CommandBase;
import xyz.mcnr.utils.misc.SocialData;

public class Anon extends CommandBase {
    @Override
    public String name() {
        return "anon";
    }

    @Override
    public String usage() {
        return "/anon <сообщение>";
    }

    @Override
    public String description() {
        return "Отправить анонимное сообщение в чат";
    }

    @Override
    public void run(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + usage());
            return;
        }

        String message = String.join(" ", args).trim();
        if (message.startsWith(">")) {
            message = ChatColor.GREEN + message;
        }
        message = "</anon> " + message;

        for (SocialData data : Main.social.getSocials().values()) {
            if (data.getOnlinePlayer() == null) continue;
            if (data.isAnonChat() || data.getPlayer().getName().equals(sender.getName()))
                data.getOnlinePlayer().sendMessage(message);
        }
    }
}
