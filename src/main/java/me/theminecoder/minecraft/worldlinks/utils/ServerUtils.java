package me.theminecoder.minecraft.worldlinks.utils;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerUtils {

    /**
     * Sends a player to a specified server via the BungeeCord messaging
     * channel.
     *
     * @param plugin Plugin instance
     * @param player The player
     * @param server The server name
     */
    public static void sendPlayerToServer(Plugin plugin, Player player, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
    }

}
