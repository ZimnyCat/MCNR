package xyz.mcnr.proxy;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Plugin(
        id = "mcnr-proxy",
        name = "MCNR-Proxy",
        version = "3"
)
public class Proxy {
    ProxyServer server;

    // MOTD stuff
    File MOTDs = new File("MOTDs.txt");
    MiniMessage mm = MiniMessage.miniMessage();
    List<String> motdList;
    long lastUpdate = 0;

    // protocol stuff
    File minFile = new File("min.txt");
    int minProtocol;

    @Inject
    public Proxy(ProxyServer server) {
        this.server = server;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws IOException {
        if (!MOTDs.exists()) Files.createFile(MOTDs.toPath());
        update();

        if (!minFile.exists()) Files.createFile(minFile.toPath());
        minProtocol = Integer.parseInt(Files.readAllLines(minFile.toPath()).getFirst());

        // удаление старых логов раз в день
        server.getScheduler().buildTask(this, () -> {
            File logs = new File("logs");
            File[] logFiles = logs.listFiles();

            for (File file : logFiles) {
                if (file.lastModified() < (System.currentTimeMillis() - 864000000)) {
                    file.delete();
                }
            }
        }).repeat(1, TimeUnit.DAYS).schedule();
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
        byte connections = 0;
        InetAddress playerIP = event.getPlayer().getRemoteAddress().getAddress();

        for (Player p : server.getAllPlayers())
            if (p.getRemoteAddress().getAddress().equals(playerIP))
                connections++;

        if (connections > 2)
            event.setResult(ResultedEvent.ComponentResult.denied(Component.text("Слишком много подключений")));

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
