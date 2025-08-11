package xyz.mcnr.proxy;

import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Plugin(
        id = "mcnr-proxy",
        name = "MCNR-Proxy",
        version = "1"
)
public class Proxy {
    // MOTD stuff
    File MOTDs = new File("MOTDs.txt");
    MiniMessage mm = MiniMessage.miniMessage();
    List<String> motdList;
    long lastUpdate = 0;

    // protocol stuff
    File minFile = new File("min.txt");
    int minProtocol;


    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws IOException {
        if (!MOTDs.exists()) Files.createFile(MOTDs.toPath());
        update();

        if (!minFile.exists()) Files.createFile(minFile.toPath());
        minProtocol = Integer.parseInt(Files.readAllLines(minFile.toPath()).getFirst());
    }

    @Subscribe
    public void onPing(ProxyPingEvent event) throws IOException {
        update();

        ServerPing.Builder builder = event.getPing().asBuilder();
        builder.description(mm.deserialize("<green>> " + motdList.get(ThreadLocalRandom.current().nextInt(motdList.size()))));
        event.setPing(builder.build());
    }

    @Subscribe
    public void onLogin(LoginEvent event) {
        if (event.getPlayer().getProtocolVersion().getProtocol() < minProtocol)
            event.getPlayer().disconnect(Component.text("Версия не поддерживается"));
    }

    // обновление списка MOTD раз в час
    private void update() throws IOException {
        if (System.currentTimeMillis() - lastUpdate > 3600000) {
            motdList = Files.readAllLines(MOTDs.toPath());
            lastUpdate = System.currentTimeMillis();
        }
    }
}
