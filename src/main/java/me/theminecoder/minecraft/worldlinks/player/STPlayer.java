package me.theminecoder.minecraft.worldlinks.player;

import me.theminecoder.minecraft.worldlinks.WorldLinks;
import me.theminecoder.minecraft.worldlinks.link.Link;
import me.theminecoder.minecraft.worldlinks.link.LinkLocation;
import me.theminecoder.minecraft.worldlinks.link.LinkType;
import me.theminecoder.minecraft.worldlinks.utils.ServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class STPlayer {

    private String uuid;
    private String spawnServer = null;
    private LinkLocation spawnLoc = null;
    private LinkType spawnType = null;

    private List<Link> unlockedLinks = new ArrayList<Link>();

    /**
     * Constructs a new STPlayer object for a player.
     *
     * @param uuid The player's UUID
     */
    public STPlayer(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Constructs a new STPlayer object from a ResultSet fetched from
     * the database.
     *
     * @param set The ResultSet to load from
     * @throws SQLException Thrown if data is invalid
     */
    public STPlayer(ResultSet set, List<Link> unlockedLinks) throws SQLException {
        this.uuid = set.getString("uuid");
        this.unlockedLinks = unlockedLinks;
        this.spawnType = LinkType.getByConfigName(set.getString("spawn_type"));

        // are they transporting from another server?
        if (this.spawnType != null) {
            double x = set.getDouble("spawn_x");
            double y = set.getDouble("spawn_y");
            double z = set.getDouble("spawn_z");

            this.spawnServer = set.getString("spawn_server");
            this.spawnLoc = new LinkLocation(set.getString("spawn_world"), x, y, z);
        }
    }

    /**
     * Gets the UUID of the player.
     *
     * @return The UUID
     */
    public String getUUID() {
        return uuid;
    }

    /**
     * Gets the bukkit player instance.
     *
     * @return The player's uuid
     */
    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(UUID.fromString(getUUID()));
    }

    /**
     * Gets the server the player is supposed to be transported to, if applicable.
     *
     * @return The server
     */
    public String getSpawnServer() {
        return spawnServer;
    }

    /**
     * Gets the location data if the player is being transported.
     *
     * @return The location
     */
    public LinkLocation getSpawnLocation() {
        return spawnLoc;
    }

    /**
     * Gets the link type data if the player is being transported.
     *
     * @return The link type
     */
    public LinkType getSpawnType() {
        return spawnType;
    }

    /**
     * Gets a list of links the player has unlocked.
     *
     * @return List of unlocked links
     */
    public List<Link> getUnlockedLinks() {
        return unlockedLinks;
    }

    /**
     * Gets whether a player has unlocked a specific link.
     *
     * @param link The link to check
     * @return True if unlocked, otherwise false
     */
    public boolean hasUnlocked(Link link) {
        return unlockedLinks.contains(link);
    }

    /**
     * Unlocks a link for the player.
     *
     * @param link The link to unlock
     */
    public void unlockLink(final Link link) {
        if (hasUnlocked(link)) {
            return; // they have already unlocked this link.
        }

        // run the database update in an async task.
        new BukkitRunnable() {
            public void run() {
                try {
                    WorldLinks.getInstance().getDB().unlockLink(getUUID(), link);
                    unlockedLinks.add(link);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(WorldLinks.getInstance());
    }

    /**
     * Transports the player through a link and into a new server.
     *
     * @param link The link to transport through
     */
    public void transport(Link link) {
        if (!hasUnlocked(link)) {
            return; // they can not use links they haven't unlocked.
        }

        Location newLoc = null;

        if (link.getType() == LinkType.ABSOLUTE) {
            newLoc = link.getLocationData().getBukkitLocation();
        }

        final Player player = getBukkitPlayer();
        final LinkType type = link.getType();
        final String server = link.getServer();
        final Location location = newLoc;

        player.sendMessage("Transporting you to " + link.getName() + "...");

        // run the database update in an async task.
        new BukkitRunnable() {
            public void run() {
                try {
                    WorldLinks.getInstance().getDB().updateSpawnData(getUUID(), type, server, location);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                // transport them to the new server...
                ServerUtils.sendPlayerToServer(WorldLinks.getInstance(), player, server);
            }
        }.runTaskAsynchronously(WorldLinks.getInstance());
    }

}
