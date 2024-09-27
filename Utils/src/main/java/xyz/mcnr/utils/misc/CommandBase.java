package xyz.mcnr.utils.misc;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class CommandBase {
    // сама команда
    public abstract String name();
    // команда и аргументы для /help
    public abstract String usage();
    // описание для /help
    public abstract String description();

    public abstract void run(CommandSender sender, Command command, String lable, String[] args);
}
