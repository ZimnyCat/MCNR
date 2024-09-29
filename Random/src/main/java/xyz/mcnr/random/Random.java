package xyz.mcnr.random;

import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Plugin(
        id = "mcnr-random",
        name = "MCNR-Random",
        version = "1"
)
public class Random {
    File MOTDs = new File("MOTDs.txt");
    MiniMessage mm = MiniMessage.miniMessage();

    List<String> motdList;
    long lastUpdate = 0;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws IOException {
        if (!MOTDs.exists()) Files.createFile(MOTDs.toPath());
        update();
    }

    @Subscribe
    public void onPing(ProxyPingEvent event) throws IOException {
        update();

        ServerPing.Builder builder = event.getPing().asBuilder();
        builder.description(mm.deserialize("<green>> " + motdList.get(ThreadLocalRandom.current().nextInt(motdList.size()))));
        event.setPing(builder.build());
    }

    // обновление списка MOTD раз в час
    private void update() throws IOException {
        if (System.currentTimeMillis() - lastUpdate > 3600000) {
            motdList = Files.readAllLines(MOTDs.toPath());
            lastUpdate = System.currentTimeMillis();
        }
    }
}
