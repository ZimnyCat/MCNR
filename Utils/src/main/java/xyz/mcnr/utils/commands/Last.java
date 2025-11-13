package xyz.mcnr.utils.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import xyz.mcnr.utils.Main;
import xyz.mcnr.utils.misc.CommandBase;
import xyz.mcnr.utils.misc.SocialData;

public class Last extends CommandBase {
    @Override
    public String name() {
        return "last";
    }

    @Override
    public String usage() {
        return "/last <сообщение>";
    }

    @Override
    public String description() {
        return "ЛС последнему получателю";
    }

    @Override
    public void run(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + usage());
            return;
        }

        SocialData social = Main.social.getSocial(sender.getName());
        String last = social.getLastRecipient();
        if (last == null) {
            sender.sendMessage(ChatColor.RED + "Вы ещё никому не писали");
            return;
        }

        Main.social.send(sender, last, String.join(" ", args));
    }

}
