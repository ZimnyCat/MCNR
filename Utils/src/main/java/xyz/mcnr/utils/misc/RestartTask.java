package xyz.mcnr.utils.misc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.List;

public class RestartTask extends BukkitRunnable {
    public long startTime;
    public long restartTime = 14400;
    List<WarnPair> warns = new LinkedList<>(List.of(
            new WarnPair(300L, "5 минут"),
            new WarnPair(60L, "1 минуту"),
            new WarnPair(10L, "10 секунд")
    ));

    @Override
    public void run() {
        if (passed(restartTime * 1000)) {
            Bukkit.getServer().shutdown();
        } else if (!warns.isEmpty() && passed((restartTime - warns.getFirst().seconds) * 1000)) {
            Bukkit.getServer().broadcastMessage("MCNR перезапустится через " + ChatColor.RED + warns.getFirst().msg);
            warns.removeFirst();
        }

        if (restartTime == 14400 && !passed(7200000) && Bukkit.getOnlinePlayers().size() >= 15) {
            restartTime = (System.currentTimeMillis() - startTime + 7200000) / 1000;
        }
    }

    private boolean passed(long time) {
        return System.currentTimeMillis() - startTime > time;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    record WarnPair(Long seconds, String msg) {}
}
