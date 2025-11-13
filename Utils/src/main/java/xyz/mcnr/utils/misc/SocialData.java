package xyz.mcnr.utils.misc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class SocialData {
    private final List<String> afkMessages = new ArrayList<>();
    private final List<String> ignoreList = new ArrayList<>();
    private final List<String> messageBacklog = new ArrayList<>();
    private final OfflinePlayer player;

    private String lastRecipient;
    private String lastSender;
    private boolean afk;
    private short offlineMsgLimit = 5;

    private boolean anonChat = true;

    private boolean hideJoinDates = false;

    private long lastFileUpdate = 0;

    public SocialData(OfflinePlayer player) {
        this.player = player;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public Player getOnlinePlayer() {
        return Bukkit.getPlayer(player.getName());
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

    public List<String> getMessageBacklog() {
        return messageBacklog;
    }

    public boolean isIgnoring(String name) {
        return ignoreList.contains(name);
    }

    public boolean isFileUpdateNotOK() {
        if (System.currentTimeMillis() - lastFileUpdate < 3000) {
            getOnlinePlayer().sendMessage(ChatColor.RED + "Слишком быстро!");
            lastFileUpdate = System.currentTimeMillis();
            return true;
        }
        lastFileUpdate = System.currentTimeMillis();
        return false;
    }

    public boolean isAfk() {
        return afk;
    }

    public void setAfk(boolean afk) {
        this.afk = afk;
    }

    public List<String> getAfkMessages() {
        return afkMessages;
    }

    public void setAnonChat(boolean anonChat) {
        this.anonChat = anonChat;
    }

    public boolean isAnonChat() {
        return anonChat;
    }

    public void toggleHideJoinDates() {
        hideJoinDates = !hideJoinDates;
    }

    public void setHideJoinDates(boolean hideJoinDates) {
        this.hideJoinDates = hideJoinDates;
    }

    public boolean isHidingJoinDates() {
        return hideJoinDates;
    }

    public short getOfflineMsgLimit() {
        offlineMsgLimit--;
        return offlineMsgLimit;
    }
}
