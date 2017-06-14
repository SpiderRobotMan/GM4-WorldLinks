package me.theminecoder.minecraft.worldlinks;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DataPersisterManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;
import me.theminecoder.minecraft.worldlinks.listeners.PlayerListener;
import me.theminecoder.minecraft.worldlinks.managers.PlayerManager;
import me.theminecoder.minecraft.worldlinks.managers.WorldManager;
import me.theminecoder.minecraft.worldlinks.objects.Link;
import me.theminecoder.minecraft.worldlinks.objects.LinkLocation;
import me.theminecoder.minecraft.worldlinks.objects.LinkPlayer;
import me.theminecoder.minecraft.worldlinks.objects.LinkTravel;
import me.theminecoder.minecraft.worldlinks.tasks.WorldLinkDisplayerTask;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Stream;

public class WorldLinks extends JavaPlugin {

    private static WorldLinks instance;


    private PlayerManager playerManager;
    private WorldManager worldManager;

    private String serverName;
    private final ItemStack selectorItem = new ItemStack(Material.STICK);

    private Dao<Link, String> linkDao;
    private Dao<LinkPlayer, UUID> linkPlayerDao;
    private Dao<LinkTravel, Integer> linkTravelDao;

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

        ItemMeta selectorMeta = selectorItem.getItemMeta();
        selectorMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', getConfig().getString("selector.identifiable_lore", "World Selector")));
        selectorItem.setItemMeta(selectorMeta);

        try {
            JdbcConnectionSource source = new JdbcPooledConnectionSource("jdbc:mysql://"
                    + getConfig().getString("database.host", "127.0.0.1")
                    + getConfig().getString("database.port", "3306")
                    + "/"
                    + getConfig().getString("database.database", "test"),
                    getConfig().getString("database.username", "username"),
                    getConfig().getString("database.password", "password")
            );

            DataPersisterManager.registerDataPersisters(LinkLocation.Persister.getInstance());

            TableUtils.createTableIfNotExists(source, Link.class);
            TableUtils.createTableIfNotExists(source, me.theminecoder.minecraft.worldlinks.objects.LinkPlayer.class);

            linkDao = DaoManager.createDao(source, Link.class);
            linkPlayerDao = DaoManager.createDao(source, LinkPlayer.class);
            linkTravelDao = DaoManager.createDao(source, LinkTravel.class);
        } catch (SQLException e) {
            this.getLogger().log(Level.SEVERE, "Could not connect to the database!", e);
            return;
        }

        this.serverName = getConfig().getString("server.name", "error");
        this.playerManager = new PlayerManager(this);
        this.worldManager = new WorldManager(this);

        Stream.of(
                worldManager,
                new PlayerListener(this)
        ).forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));

        this.getServer().getScheduler().runTaskTimer(this, new WorldLinkDisplayerTask(this), 0, 0);

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
     * Gets a LinkPlayer by their Bukkit player object.
     *
     * @param player The player
     * @return LinkPlayer or null if not online
     */
    public LinkPlayer getPlayer(Player player) {
        return getPlayerManager().getPlayer(player);
    }

    /**
     * Gets a LinkPlayer by their username.
     *
     * @param username The username
     * @return LinkPlayer or null if not online
     */
    public LinkPlayer getPlayer(String username) {
        return getPlayerManager().getPlayer(username);
    }

    public Dao<Link, String> getLinkDao() {
        return linkDao;
    }

    public Dao<LinkPlayer, UUID> getLinkPlayerDao() {
        return linkPlayerDao;
    }

    public Dao<LinkTravel, Integer> getLinkTravelDao() {
        return linkTravelDao;
    }

    public ItemStack getSelectorItem() {
        return selectorItem;
    }
}
