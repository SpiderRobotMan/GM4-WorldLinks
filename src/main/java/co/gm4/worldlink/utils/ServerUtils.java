package co.gm4.worldlink.utils;

import co.gm4.worldlink.WorldLink;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;

/**
 * Created by MatrixTunnel on 9/10/2017.
 */
public class ServerUtils {

    /**
     * Sends a player to a specified server via the BungeeCord messaging
     * channel.
     *
     * @param player The player
     * @param server The server name
     */
    public static void sendToServer(Player player, String server) {
        if (player != null) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();

            out.writeUTF("Connect");
            out.writeUTF(server);
            player.sendPluginMessage(WorldLink.get(), "BungeeCord", out.toByteArray());
        }
    }

}
