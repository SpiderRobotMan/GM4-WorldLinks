package me.theminecoder.minecraft.worldlinks.managers;

import com.google.common.collect.Maps;
import me.theminecoder.minecraft.worldlinks.WorldLinks;
import me.theminecoder.minecraft.worldlinks.objects.LinkPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.*;

public class PlayerManager {

    private WorldLinks plugin;

    private Map<UUID, LinkPlayer> playerMap = Maps.newHashMap();

    /**
     * Constructs a new instance of the player manager and registers
     * itself as a listener.
     *
     * @param plugin The plugin
     */
    public PlayerManager(WorldLinks plugin) {
        this.plugin = plugin;
    }

    /**
     * Gets a list of players currently online the server.
     *
     * @return List of players
     */
    public List<LinkPlayer> getOnlinePlayers() {
        List<LinkPlayer> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            players.add(getPlayer(player));
        }
        return players;
    }


    /**
     * Gets a player by their bukkit player object. This will return
     * null if the user is not present on this server.
     *
     * @param player The bukkit user
     * @return The LinkPlayer, or null if not found
     */
    public LinkPlayer getPlayer(Player player) {
        return playerMap.get(player.getUniqueId());
    }

    public Map<UUID, LinkPlayer> getPlayerMap() {
        return playerMap;
    }

}
