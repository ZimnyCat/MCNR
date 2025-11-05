package xyz.mcnr.utils.misc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.List;

public class RestartTask extends BukkitRunnable {
    public long startTime;
    public long restartTime = 21600; // 6h
    List<WarnPair> warns = new LinkedList<>(List.of(
            new WarnPair(300L, "5 минут"),
            new WarnPair(60L, "1 минуту"),
            new WarnPair(10L, "10 секунд")
    ));
    List<RestartTimePair> restartTimes = new LinkedList<>(List.of(
            new RestartTimePair(10, 14400000L), // 4h
            new RestartTimePair(15, 10800000L), // 3h
            new RestartTimePair(20, 7200000L),  // 2h
            new RestartTimePair(25, 3600000L)   // 1h
    ));

    @Override
    public void run() {
        if (passed(restartTime * 1000)) {
            Bukkit.getServer().shutdown();
        } else if (!warns.isEmpty() && passed((restartTime - warns.getFirst().seconds) * 1000)) {
            Bukkit.getServer().broadcastMessage("MCNR перезапустится через " + ChatColor.RED + warns.getFirst().msg);
            warns.removeFirst();
        }
    }

    private boolean passed(long time) {
        return System.currentTimeMillis() - startTime > time;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void checkPlayerCount() {
        RestartTimePair pair = restartTimes.getFirst();
        if (Bukkit.getOnlinePlayers().size() >= pair.playerCount && (startTime + restartTime * 1000) - System.currentTimeMillis() > pair.diff) {
                restartTime = (System.currentTimeMillis() - startTime + pair.diff) / 1000;
                restartTimes.removeFirst();
        }
    }

    record WarnPair(Long seconds, String msg) {}

    record RestartTimePair(int playerCount, Long diff) {}
}
