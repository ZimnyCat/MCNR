package xyz.mcnr.utils.misc;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SocialData {
    private final List<String> ignoreList = new ArrayList<>();
    private final Player player;

    private long lastIgnoreUpdate;
    private String lastRecipient;
    private String lastSender;

    public SocialData(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public String getLastRecipient() {
        return lastRecipient;
    }

    public void setLastRecipient(String lastRecipient) {
        this.lastRecipient = lastRecipient;
    }

    public String getLastSender() {
        return lastSender;
    }

    public void setLastSender(String lastSender) {
        this.lastSender = lastSender;
    }

    public List<String> getIgnoreList() {
        return ignoreList;
    }

    public long getLastIgnoreUpdate() {
        return lastIgnoreUpdate;
    }

    public void setLastIgnoreUpdate(long lastIgnoreUpdate) {
        this.lastIgnoreUpdate = lastIgnoreUpdate;
    }
}
