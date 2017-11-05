package co.gm4.worldlink.managers;

import co.gm4.worldlink.WorldLink;
import co.gm4.worldlink.objects.LinkLocationType;
import co.gm4.worldlink.objects.LinkPlayer;
import co.gm4.worldlink.objects.LinkPlayerData;
import co.gm4.worldlink.objects.LinkWorld;
import co.gm4.worldlink.utils.LocationUtils;
import com.google.gson.Gson;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by MatrixTunnel on 9/10/2017.
 */
@Getter
public class DatabaseHandler {

    private String host;
    private String database;
    private String username;
    private String password;
    private HikariDataSource hikari;

    public DatabaseHandler(String host, String database, String username, String password) {
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;

        init();
    }

    public void init() {
        this.hikari = new HikariDataSource();

        this.getHikari().setMaximumPoolSize(10);
        this.getHikari().setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        this.getHikari().addDataSourceProperty("serverName", this.getHost());
        this.getHikari().addDataSourceProperty("port", "3306");
        this.getHikari().addDataSourceProperty("databaseName", this.getDatabase());
        this.getHikari().addDataSourceProperty("user", this.getUsername());
        this.getHikari().addDataSourceProperty("password", this.getPassword());

        try {
            Connection connection = this.getHikari().getConnection();
            Statement statement = connection.createStatement();

            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS `link_players` "
                            + "("
                            + "`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, "
                            + "`uuid` VARCHAR(36), "
                            + "`playerdata` MEDIUMBLOB NULL DEFAULT NULL, " // MEDIUMBLOB(65535)
                            + "`teleportType` VARCHAR(100) NULL DEFAULT NULL, "
                            + "`unlockedWorlds` VARCHAR(1000) NULL DEFAULT NULL,"
                            + "`advancements` MEDIUMBLOB NULL DEFAULT NULL, "
                            + "`stats` MEDIUMBLOB NULL DEFAULT NULL,"
                            + "`respawnLocation` VARCHAR(32) NULL DEFAULT NULL"
                            + ");");
            // This MySQL user will not have perms for this: "SET GLOBAL max_allowed_packet = 65535;" // 1024 * 64

            statement.close();
            connection.close();
        } catch (SQLException e) {
            WorldLink.get().getLogger().warning("Could not establish connection with the database");
            e.printStackTrace();
            WorldLink.get().getPluginLoader().disablePlugin(WorldLink.get());
        }
    }

    public LinkPlayer getLinkPlayer(UUID uuid) {
        try {
            Connection connection = this.getHikari().getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM `link_players` WHERE `uuid`=?;");
            ps.setString(1, uuid.toString());

            ResultSet res = ps.executeQuery();

            res.next();

            String playerdata = res.getString(3);
            String locType = (res.getString(4) == null ? LinkLocationType.ABSOLUTE.name() : res.getString(4));
            String unlockedWorlds = res.getString(5);

            String advancements = res.getString(6);
            String stats = res.getString(7);

            String respawnLocation = res.getString(8);

            LinkPlayer linkPlayer = new LinkPlayer(uuid, (playerdata == null ? null : new Gson().fromJson(playerdata, LinkPlayerData.class)), stringToWorlds(unlockedWorlds), advancements, stats, respawnLocation);

            if (locType != null && !locType.isEmpty()) {
                LinkLocationType locationType = LinkLocationType.getByConfigName(locType);
                linkPlayer.setLocationType(locationType == null ? LinkLocationType.ABSOLUTE : locationType);
            }
            if (advancements != null && !advancements.isEmpty()) {
                linkPlayer.setAdvancementsJson(advancements);
            }
            if (stats != null && !stats.isEmpty()) {
                linkPlayer.setStatsJson(stats);
            }


            ps.close();
            res.close();
            connection.close();

            return linkPlayer;
        } catch (SQLException e) {
            return null;
        }
    }

    public void registerPlayer(UUID uuid) throws SQLException {
        Connection connection = this.getHikari().getConnection();
        PreparedStatement ps = connection.prepareStatement("INSERT INTO `link_players`(`uuid`) VALUES (?);");
        ps.setString(1, uuid.toString());

        ps.executeUpdate();
        ps.close();
        connection.close();
    }

    public void savePlayer(UUID uuid) throws SQLException {
        try {
            LinkPlayer linkPlayer = WorldLink.get().getPlayerManager().getLinkPlayer(uuid);

            linkPlayer.getPlayer().saveData();

            String worlds = worldsToString(uuid);

            Connection connection = this.getHikari().getConnection();
            PreparedStatement ps = connection.prepareStatement("UPDATE `link_players` SET `playerdata`=?,`teleportType`=?,`unlockedWorlds`=?,`advancements`=?,`stats`=?,`respawnLocation`=? WHERE `uuid`=?;");
            if (linkPlayer.getPlayerData() != null && !linkPlayer.getPlayerData().getAsJson().isEmpty()) {
                ps.setString(1, linkPlayer.getPlayerData().getAsJson());
            } else {
                ps.setNull(1, Types.BLOB);
            }
            if (linkPlayer.getLocationType() != null && !linkPlayer.getLocationType().name().isEmpty()) {
                ps.setString(2, linkPlayer.getLocationType().name());
            } else {
                ps.setNull(2, Types.VARCHAR);
            }
            if (worlds != null && !worlds.isEmpty()) {
                ps.setString(3, worlds);
            } else {
                ps.setNull(3, Types.VARCHAR);
            }
            if (linkPlayer.getAdvancementsJson() != null && !linkPlayer.getAdvancementsJson().isEmpty()) {
                ps.setString(4, linkPlayer.getAdvancementsJson());
            } else {
                ps.setNull(4, Types.BLOB);
            }
            if (linkPlayer.getStatsJson() != null && !linkPlayer.getStatsJson().isEmpty()) {
                ps.setString(5, linkPlayer.getStatsJson());
            } else {
                ps.setNull(5, Types.BLOB);
            }
            if (linkPlayer.getRespawnLocation() != null && !linkPlayer.getRespawnLocation().isEmpty()) {
                ps.setString(6, linkPlayer.getRespawnLocation());
            } else {
                ps.setNull(6, Types.VARCHAR);
            }

            ps.setString(7, uuid.toString());

            ps.executeUpdate();

            ps.close();
            connection.close();

        } catch (SQLException e) {
            WorldLink.get().getLogger().warning("Failed to save player: " + uuid.toString());
            e.printStackTrace();
        }
    }

    public void savePlayerWorlds(UUID uuid) {
        try {
            LinkPlayer linkPlayer = WorldLink.get().getPlayerManager().getLinkPlayer(uuid);
            String worlds = worldsToString(uuid);

            Connection connection = this.getHikari().getConnection();
            PreparedStatement ps = connection.prepareStatement("UPDATE `link_players` SET `unlockedWorlds`=?,`respawnLocation`=? WHERE `uuid`=?;");
            if (worlds != null && !worlds.isEmpty()) {
                ps.setString(1, worlds);
            } else {
                ps.setNull(1, Types.VARCHAR);
            }
            if (linkPlayer.getRespawnLocation() != null && !linkPlayer.getRespawnLocation().isEmpty()) {
                ps.setString(2, linkPlayer.getRespawnLocation());
            } else {
                ps.setNull(2, Types.VARCHAR);
            }
            ps.setString(3, uuid.toString());

            ps.executeUpdate();

            ps.close();
            connection.close();
        } catch (SQLException e) {
            WorldLink.get().getLogger().warning("Failed to save player worlds: " + uuid.toString());
            e.printStackTrace();
        }
    }

    public boolean playerExists(UUID uuid) {
        try {
            Connection connection = this.getHikari().getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT `id` FROM `link_players` WHERE `uuid`=?;");
            ps.setString(1, uuid.toString());

            ResultSet res = ps.executeQuery();
            boolean exists = res.next();

            res.close();
            ps.close();
            connection.close();

            return exists;
        } catch (SQLException e) {
            return false;
        }
    }

    public void clearLinkPlayerDataFromDB(UUID uuid) {
        try {
            Connection connection = this.getHikari().getConnection();
            PreparedStatement ps = connection.prepareStatement("UPDATE `link_players` SET `playerdata`=NULL,`teleportType`=NULL,`advancements`=NULL,`stats`=NULL WHERE `uuid`=?;");
            ps.setString(1, uuid.toString());

            ps.executeUpdate();

            ps.close();
            connection.close();
        } catch (SQLException e) {
            WorldLink.get().getLogger().warning("Failed to clear player data: " + uuid.toString());
            e.printStackTrace();
        }
    }

    public List<LinkWorld> stringToWorlds(String worldsString) {
        List<LinkWorld> worlds = new ArrayList<>();

        if (worldsString == null) {
            return worlds;
        }

        if (!worldsString.trim().equalsIgnoreCase("") && !worldsString.trim().isEmpty()) {
            if (worldsString.contains(";")) {
                Arrays.stream(worldsString.split(";")).forEach(name -> worlds.add(new LinkWorld(name)));
            } else {
                worlds.add(new LinkWorld(worldsString));
            }
        }

        return worlds;
    }

    public String worldsToString(UUID uuid) {
        StringBuilder builder = new StringBuilder();

        List<LinkWorld> linkWorlds = WorldLink.get().getPlayerManager().getLinkPlayer(uuid).getWorlds();

        if (linkWorlds.isEmpty()) return "";

        linkWorlds.forEach(linkWorld -> {
            builder.append(linkWorld.getName());
            builder.append(";");
        });

        return builder.toString().substring(0, builder.toString().length() - 1);
    }

}
