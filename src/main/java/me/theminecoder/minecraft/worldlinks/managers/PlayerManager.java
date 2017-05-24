package me.theminecoder.minecraft.worldlinks.managers;

import me.theminecoder.minecraft.worldlinks.WorldLinks;
import me.theminecoder.minecraft.worldlinks.objects.LinkPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerManager implements Listener {

    private WorldLinks plugin;

    private Map<String, LinkPlayer> playerMap = new HashMap<String, LinkPlayer>();

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
     * Gets a player by their username. This will return null if the user
     * is not present on this server.
     *
     * @param username The user's username
     * @return The LinkPlayer, or null if not found
     */
    public LinkPlayer getPlayer(String username) {
        return playerMap.get(username.toLowerCase());
    }

    /**
     * Gets a player by their bukkit player object. This will return
     * null if the user is not present on this server.
     *
     * @param player The bukkit user
     * @return The LinkPlayer, or null if not found
     */
    public LinkPlayer getPlayer(Player player) {
        return playerMap.get(player.getName().toLowerCase());
    }

    public Map<String, LinkPlayer> getPlayerMap() {
        return playerMap;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
//        plugin.getPlayerManager().getPlayer(event.getPlayer()).unlockLink(plugin.getWorldManager().getWorldLink("example"));
    }

}
