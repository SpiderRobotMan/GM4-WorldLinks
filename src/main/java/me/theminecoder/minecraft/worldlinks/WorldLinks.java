package me.theminecoder.minecraft.worldlinks;

import me.theminecoder.minecraft.worldlinks.database.Database;
import me.theminecoder.minecraft.worldlinks.managers.PlayerManager;
import me.theminecoder.minecraft.worldlinks.managers.WorldManager;
import me.theminecoder.minecraft.worldlinks.player.STPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class WorldLinks extends JavaPlugin {

    private static WorldLinks instance;

    private PlayerManager playerManager;
    private WorldManager worldManager;

    private String serverName;
    private Database database;

    /**
     * Gets an instance of the running plugin.
     *
     * @return The plugin
     */
    public static WorldLinks getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.serverName = getConfig().getString("server.name", "error");
        this.playerManager = new PlayerManager(this);
        this.worldManager = new WorldManager(this);

        this.database = new Database(this);
        this.database.connect();

        new BukkitRunnable() {
            public void run() {
                getWorldManager().displayWorldLinks();
            }
        }.runTaskTimer(this, 0L, 1L);

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    /**
     * Gets the name of this server from the configuration.
     *
     * @return The name
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * Gets the database instance.
     *
     * @return The database
     */
    public Database getDB() {
        return database;
    }

    /**
     * Gets the player manager instance.
     *
     * @return The player manager
     */
    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    /**
     * Gets the world manager instance.
     *
     * @return The world manager
     */
    public WorldManager getWorldManager() {
        return worldManager;
    }

    /**
     * Gets a STPlayer by their Bukkit player object.
     *
     * @param player The player
     * @return STPlayer or null if not online
     */
    public STPlayer getPlayer(Player player) {
        return getPlayerManager().getPlayer(player);
    }

    /**
     * Gets a STPlayer by their username.
     *
     * @param username The username
     * @return STPlayer or null if not online
     */
    public STPlayer getPlayer(String username) {
        return getPlayerManager().getPlayer(username);
    }

}
