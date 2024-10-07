package xyz.mcnr.utils.misc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Locale;

public class TabTask extends BukkitRunnable {
    long last = 0;
    String footer = "";

    @Override
    public void run() {
        footer = "\n" + Bukkit.getOnlinePlayers().size() + "/30\nTPS: " + String.format(Locale.US, "%.2f", getTPS()) + "   ping: ";
        last = System.currentTimeMillis();

        Bukkit.getOnlinePlayers().forEach(this::update);
    }

    public void update(Player p) {
        p.setPlayerListFooter(footer + p.getPing());
    }

    private float getTPS() {
        float tps = 20 / ((float) (System.currentTimeMillis() - last) / 1000);
        return Math.clamp(tps, 0, 20);
    }
}
