package me.theminecoder.minecraft.worldlinks.database;

import me.theminecoder.minecraft.worldlinks.WorldLinks;
import me.theminecoder.minecraft.worldlinks.link.Link;
import me.theminecoder.minecraft.worldlinks.link.LinkType;
import me.theminecoder.minecraft.worldlinks.player.STPlayer;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private String host;
    private String port;
    private String database;
    private String username;
    private String password;

    private WorldLinks plugin;
    private HikariDataSource source;

    /**
     * Creates a new instance of the database class using the settings stored in the
     * plugin configuration.
     *
     * @param plugin Instance of the plugin
     */
    public Database(WorldLinks plugin) {
        this.plugin = plugin;
        this.host = plugin.getConfig().getString("database.host", "127.0.0.1");
        this.port = plugin.getConfig().getString("database.port", "3306");
        this.database = plugin.getConfig().getString("database.database", "test");
        this.username = plugin.getConfig().getString("database.username", "username");
        this.password = plugin.getConfig().getString("database.password", "password");
    }

    /**
     * Connects to the database.
     */
    public void connect() {
        source = new HikariDataSource();

        source.setMaximumPoolSize(10);
        source.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        source.addDataSourceProperty("serverName", this.host);
        source.addDataSourceProperty("port", this.port);
        source.addDataSourceProperty("databaseName", this.database);
        source.addDataSourceProperty("user", this.username);
        source.addDataSourceProperty("password", this.password);

        try {
            this.createUserTable();
        } catch (SQLException e) {
            System.out.println("Failed to create player_data table: " + e.getMessage());
        }

        try {
            this.createUnlockTable();
        } catch (SQLException e) {
            System.out.println("Failed to create unlocked_links table: " + e.getMessage());
        }
    }

    /**
     * Disconnects from the database.
     */
    public void disconnect() {
        try {
            source.close();
        } catch (Exception e) {
            //Failed to close cleanly...
        }
    }

    /**
     * Automatically creates the player_data table.
     */
    public void createUserTable() throws SQLException {
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = source.getConnection();

            ps = connection.prepareStatement("CREATE TABLE `player_data` ("
                    + "`id` int NOT NULL AUTO_INCREMENT,"
                    + "`uuid` VARCHAR(255) NOT NULL UNIQUE,"
                    + "`spawn_type` VARCHAR(255) DEFAULT NULL,"
                    + "`spawn_server` VARCHAR(255) DEFAULT NULL,"
                    + "`spawn_world` VARCHAR(255) DEFAULT NULL,"
                    + "`spawn_x` FLOAT DEFAULT 0.0,"
                    + "`spawn_y` FLOAT DEFAULT 0.0,"
                    + "`spawn_z` FLOAT DEFAULT 0.0,"
                    + "PRIMARY KEY (id));");

            ps.execute();
        } finally {
            if (ps != null) ps.close();
            if (connection != null) connection.close();
        }
    }

    /**
     * Automatically creates the unlocked_links table.
     */
    public void createUnlockTable() throws SQLException {
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = source.getConnection();

            ps = connection.prepareStatement("CREATE TABLE `unlocked_links` ("
                    + "`id` int NOT NULL AUTO_INCREMENT,"
                    + "`uuid` VARCHAR(255) NOT NULL,"
                    + "`name` VARCHAR(255) NOT NULL,"
                    + "`unlocked_at` DATETIME NOT NULL DEFAULT NOW(),"
                    + "PRIMARY KEY (id));");

            ps.execute();
        } finally {
            if (ps != null) ps.close();
            if (connection != null) connection.close();
        }
    }

    /**
     * Loads a player's data from the database by their UUID.
     *
     * @param uuid The player's UUID
     * @return The STPlayer, or null if it failed.
     */
    public STPlayer loadPlayerByUUID(String uuid) throws SQLException {
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = source.getConnection();

            ps = connection.prepareStatement("SELECT * FROM `player_data` WHERE `uuid`=?");
            ps.setString(1, uuid);

            ResultSet set = ps.executeQuery();

            if (set.next()) {
                return new STPlayer(set, loadUnlockedLinks(uuid));
            } else {
                return null;
            }
        } finally {
            if (ps != null) ps.close();
            if (connection != null) connection.close();
        }
    }

    /**
     * Creates data for a player if it does not already exist.
     *
     * @param uuid The player's UUID
     */
    public void createPlayerData(String uuid) throws SQLException {
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = source.getConnection();

            ps = connection.prepareStatement("INSERT INTO `player_data` " +
                    "(`uuid`, `spawn_type`, `spawn_server`, `spawn_world`, `spawn_x`, `spawn_y`, `spawn_z`) VALUES(?,?,?,?,?,?,?)");

            ps.setString(1, uuid);
            ps.setString(2, null);
            ps.setString(3, null);
            ps.setString(4, null);
            ps.setDouble(5, 0.0);
            ps.setDouble(6, 0.0);
            ps.setDouble(7, 0.0);

            ps.execute();
        } finally {
            if (ps != null) ps.close();
            if (connection != null) connection.close();
        }
    }

    /**
     * Updates a player's spawn data in the database.
     *
     * @param uuid        The player's UUID
     * @param spawnServer The server to spawn at
     * @param spawnLoc    The locationt to spawn at
     */
    public void  yerupdateSpawnData(String uuid, LinkType spawnType, String spawnServer, Location spawnLoc) throws SQLException {
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = source.getConnection();

            ps = connection.prepareStatement("UPDATE `player_data` SET `spawn_type`=?,`spawn_server`=?,`spawn_world`=?," +
                    "`spawn_x`=?,`spawn_y`=?,`spawn_z`=? WHERE `uuid`=?");

            System.out.println(spawnType.toString());

            ps.setString(1, spawnType.toString());
            ps.setString(2, spawnServer);
            ps.setString(3, spawnLoc.getWorld().getName());
            ps.setDouble(4, spawnLoc.getX());
            ps.setDouble(5, spawnLoc.getY());
            ps.setDouble(6, spawnLoc.getZ());
            ps.setString(7, uuid);

            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
            if (connection != null) connection.close();
        }
    }

    /**
     * Clears a player's spawn data in the database.
     *
     * @param uuid The player's UUID
     */
    public void clearSpawnData(String uuid) throws SQLException {
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = source.getConnection();

            ps = connection.prepareStatement("UPDATE `player_data` SET `spawn_type`=?,`spawn_server`=?,`spawn_world`=?," +
                    "`spawn_x`=?,`spawn_y`=?,`spawn_z`=? WHERE `uuid`=?");

            ps.setString(1, null);
            ps.setString(2, null);
            ps.setString(3, null);
            ps.setDouble(4, 0.0);
            ps.setDouble(5, 0.0);
            ps.setDouble(6, 0.0);
            ps.setString(7, uuid);

            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
            if (connection != null) connection.close();
        }
    }

    /**
     * Loads a player's unlocked links from the database by their UUID.
     *
     * @param uuid The player's UUID
     * @return A list of unlocked links
     */
    public List<Link> loadUnlockedLinks(String uuid) throws SQLException {
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = source.getConnection();

            ps = connection.prepareStatement("SELECT * FROM `unlocked_links` WHERE `uuid`=?");
            ps.setString(1, uuid);

            ResultSet set = ps.executeQuery();
            List<Link> linkList = new ArrayList<Link>();

            // load links, it's possible some are for other servers, in which case we just
            // ignore them.
            while (set.next()) {
                Link link = plugin.getWorldManager().getWorldLink(set.getString("name"));

                if (link != null) {
                    linkList.add(link);
                }
            }

            return linkList;
        } finally {
            if (ps != null) ps.close();
            if (connection != null) connection.close();
        }
    }

    /**
     * Unlocks a link for a player and stores it in the database.
     *
     * @param uuid The player's UUID
     * @param link The link to unlock
     */
    public void unlockLink(String uuid, Link link) throws SQLException {
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = source.getConnection();

            ps = connection.prepareStatement("INSERT INTO `unlocked_links` (`uuid`, `name`) VALUES(?,?)");

            ps.setString(1, uuid);
            ps.setString(2, link.getName());

            ps.execute();
        } finally {
            if (ps != null) ps.close();
            if (connection != null) connection.close();
        }
    }

}
