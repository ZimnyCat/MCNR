package xyz.mcnr.utils.misc;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class SpeedTask extends BukkitRunnable {
    public Map<Player, Long> usedTrident = new HashMap<>();

    @Override
    public void run() {
        for (Map.Entry<Player, Long> player : usedTrident.entrySet()) {
            if ((System.currentTimeMillis() - player.getValue()) > 1000) usedTrident.remove(player.getKey());
        }
    }
}
