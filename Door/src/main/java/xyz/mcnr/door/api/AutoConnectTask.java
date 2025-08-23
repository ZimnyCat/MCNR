package xyz.mcnr.door.api;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.xephi.authme.api.v3.AuthMeApi;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.mcnr.door.Main;

//TODO Проверка лежит сервер или нет.
public final class AutoConnectTask extends BukkitRunnable {
    private final Main host;

    public AutoConnectTask(Main in) {
        host = in;
    }

    /**
     * Закидываем игрока на сервер сразу после логина.
     */
    @Override public void run() {
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (AuthMeApi.getInstance().isAuthenticated(p) && !isTimedOut(p)) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Connect");
                out.writeUTF("surv");

                p.sendPluginMessage(host, "BungeeCord", out.toByteArray());
                host.joinSenders.put(p, System.currentTimeMillis());
            }
        });
    }

    /**
     * Анти спам экзекьютом.
     */
    private boolean isTimedOut(Player player) {
        if (!host.joinSenders.containsKey(player))
            return false;
        final long diff = (System.currentTimeMillis() - host.joinSenders.get(player));
        return diff < 20000;
    }
}
