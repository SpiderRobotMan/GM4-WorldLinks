package me.theminecoder.minecraft.worldlinks.managers;

import me.theminecoder.minecraft.worldlinks.WorldLinks;
import me.theminecoder.minecraft.worldlinks.link.LinkType;
import me.theminecoder.minecraft.worldlinks.player.STPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerManager implements Listener {

    private WorldLinks plugin;

    private Map<String, STPlayer> playerMap = new HashMap<String, STPlayer>();

    /**
     * Constructs a new instance of the player manager and registers
     * itself as a listener.
     *
     * @param plugin The plugin
     */
    public PlayerManager(WorldLinks plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Gets a list of players currently online the server.
     *
     * @return List of players
     */
    public List<STPlayer> getOnlinePlayers() {
        List<STPlayer> players = new ArrayList<>();
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
     * @return The STPlayer, or null if not found
     */
    public STPlayer getPlayer(String username) {
        return playerMap.get(username.toLowerCase());
    }

    /**
     * Gets a player by their bukkit player object. This will return
     * null if the user is not present on this server.
     *
     * @param player The bukkit user
     * @return The STPlayer, or null if not found
     */
    public STPlayer getPlayer(Player player) {
        return playerMap.get(player.getName().toLowerCase());
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        String uuid = event.getUniqueId().toString();
        String name = event.getName();

        try {
            STPlayer player = plugin.getDB().loadPlayerByUUID(uuid);

            //Check if the player was loaded successfully.
            if (player == null) {
                plugin.getDB().createPlayerData(uuid);
                player = new STPlayer(uuid);
            }

            playerMap.put(name.toLowerCase(), player);
        } catch (Exception e) {
            e.printStackTrace();
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage("§cFailed to load WorldLinks player data.");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final STPlayer player = getPlayer(event.getPlayer());

        // did the player come from a transport link?
        if (player.getSpawnServer() == null || !player.getSpawnServer().equals(plugin.getServerName())) {
            System.out.println("nopey");
            return;
        }

        // clear the data from the db in an async thread.
        new BukkitRunnable() {
            public void run() {
                try {
                    plugin.getDB().clearSpawnData(player.getUUID());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);

        // absolute means position them exactly where the coordinates state.
        if (player.getSpawnType() == LinkType.ABSOLUTE) {
            event.getPlayer().teleport(player.getSpawnLocation().getBukkitLocation());
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        plugin.getPlayerManager().getPlayer(event.getPlayer()).unlockLink(plugin.getWorldManager().getWorldLink("example"));
    }

}
