package xyz.mcnr.utils.misc;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class SpeedTask extends BukkitRunnable {
    public Map<Player, Long> usedTrident = new HashMap<>();
    public Map<Player, Long> dismounted = new HashMap<>();

    @Override
    public void run() {
        update(usedTrident, 1000);
        update(dismounted, 250);
    }

    private void update(Map<Player, Long> map, long timeout) {
        for (Map.Entry<Player, Long> player : map.entrySet()) {
            if ((System.currentTimeMillis() - player.getValue()) > timeout) map.remove(player.getKey());
        }
    }
}
