package xyz.mcnr.utils.misc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class RestartTask extends BukkitRunnable {
    public long startTime;
    public long restartTime = 14400;
    boolean[] warns = {true, true, true};

    @Override
    public void run() {
        if ((System.currentTimeMillis() - startTime) > ((restartTime - 400) * 1000)) {
            if ((System.currentTimeMillis() - startTime) > (restartTime * 1000)) Bukkit.getServer().shutdown();
            else {
                warn(0, 300, "5 минут");
                warn(1, 60, "1 минуту");
                warn(2, 10, "10 секунд");
            }
        }
    }

    // TODO: переделать (через HashMap<Long, String>?)
    public void warn(int index, long time, String timeMsg) {
        if (warns[index] && (System.currentTimeMillis() - startTime) > ((restartTime - time) * 1000)) {
            warns[index] = false;
            Bukkit.getServer().broadcastMessage("MCNR перезапустится через " + ChatColor.RED + timeMsg);
        }
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
