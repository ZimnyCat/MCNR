package xyz.mcnr.utils.misc;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SocialData {
    private final List<String> afkMessages = new ArrayList<>();
    private final List<String> ignoreList = new ArrayList<>();
    private final Player player;

    private String lastRecipient;
    private String lastSender;
    private boolean afk;

    private boolean anonChat = true;

    private boolean hideJoinDates = false;

    private long lastFileUpdate = 0;

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

    public boolean isIgnoring(CommandSender player) {
        return ignoreList.contains(player.getName().toLowerCase(Locale.ROOT));
    }

    public boolean isFileUpdateNotOK() {
        if (System.currentTimeMillis() - lastFileUpdate < 3000) {
            player.sendMessage(ChatColor.RED + "Слишком быстро!");
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
}
