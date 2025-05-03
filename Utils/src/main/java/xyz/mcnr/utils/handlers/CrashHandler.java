package xyz.mcnr.utils.handlers;


import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSelectBundleItem;

public class CrashHandler implements PacketListener {
    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.SELECT_BUNDLE_ITEM) {
            handleBundleCrash(event);
        }
    }

    private void handleBundleCrash(PacketReceiveEvent event) {
        WrapperPlayClientSelectBundleItem packet = new WrapperPlayClientSelectBundleItem(event);

        if (packet.getSelectedItemIndex() < -1) {
            event.setCancelled(true); // Poshel nahui.
        }
    }
}
