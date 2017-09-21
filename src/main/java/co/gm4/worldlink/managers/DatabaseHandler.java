package co.gm4.worldlink.managers;

import co.gm4.worldlink.WorldLink;
import co.gm4.worldlink.objects.LinkLocationType;
import co.gm4.worldlink.objects.LinkPlayer;
import co.gm4.worldlink.objects.LinkPlayerData;
import co.gm4.worldlink.objects.LinkWorld;
import co.gm4.worldlink.utils.Config;
import com.google.gson.Gson;
import com.huskehhh.mysql.mysql.MySQL;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by MatrixTunnel on 9/10/2017.
 */
public class DatabaseHandler {

    private MySQL mySQL;
    private Connection connection;
    private Statement statement;

    public DatabaseHandler() {
        Config config = WorldLink.get().getPluginConfig();

        mySQL = new MySQL(
                config.getDatabaseHost(),
                config.getDatabasePort(),
                config.getDatabaseDatabase(),
                config.getDatabaseUsername(),
                config.getDatabasePassword()
        );

        try {
            connection = mySQL.openConnection();
            statement = connection.createStatement();
        } catch (Exception e) {
            WorldLink.get().getLogger().warning("Could not establish connection with the database");
            e.printStackTrace();
            WorldLink.get().getPluginLoader().disablePlugin(WorldLink.get());
            return;
        }
        init();
    }

    public void init() {
        update("CREATE TABLE IF NOT EXISTS `link_players` (`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, `uuid` VARCHAR(36), `playerdata` VARCHAR(20400) NULL DEFAULT NULL, `teleportType` VARCHAR(100) NULL DEFAULT NULL, `unlockedWorlds` VARCHAR(1000) NULL DEFAULT NULL);");
        update("SET GLOBAL max_allowed_packet = 65536;"); // 1024 * 64
    }

    public LinkPlayer getLinkPlayer(UUID uuid) {
        ResultSet res = query("SELECT * FROM `link_players` WHERE `uuid`='" + uuid.toString() + "'");
        try {
            res.next();

            String playerdata = res.getString(3);
            String locType = (res.getString(4) == null ? LinkLocationType.ABSOLUTE.name() : res.getString(4));
            String unlockedWorlds = res.getString(5);

            LinkPlayer linkPlayer = new LinkPlayer(uuid, (playerdata == null ? null : new Gson().fromJson(playerdata, LinkPlayerData.class)), stringToWorlds(unlockedWorlds));

            if (locType != null && !locType.isEmpty()) {
                LinkLocationType locationType = LinkLocationType.valueOf(locType);
                if (locationType == null) locationType = LinkLocationType.ABSOLUTE;
                linkPlayer.setLocationType(locationType);
            }

            return linkPlayer;
        } catch (SQLException e) {
            return null;
        }
    }

    public void registerPlayer(UUID uuid) {
        update("INSERT INTO `link_players`(`uuid`) VALUES ('" + uuid.toString() + "')");
    }

    public void savePlayer(UUID uuid) {
        LinkPlayer linkPlayer = WorldLink.get().getPlayerManager().getLinkPlayer(uuid);
        String worlds = worldsToString(uuid);
        update("UPDATE `link_players` SET `playerdata`=" + (linkPlayer.getPlayerData() == null ? "NULL" : "'" + linkPlayer.getPlayerData().getAsJson() + "'") + ",`teleportType`=" + (linkPlayer.getLocationType() == null ? "NULL" : "'" + linkPlayer.getLocationType().name() + "'") + ",`unlockedWorlds`=" + (worlds.isEmpty() ? "NULL" : "'" + worlds + "'") + " WHERE `uuid`='" + uuid.toString() + "'");
    }

    public void savePlayerWorlds(UUID uuid){
        LinkPlayer linkPlayer = WorldLink.get().getPlayerManager().getLinkPlayer(uuid);
        String worlds = worldsToString(uuid);
        update("UPDATE `link_players` SET `unlockedWorlds`=" + (worlds.isEmpty() ? "NULL" : "'" + worlds + "'") + " WHERE `uuid`='" + uuid.toString() + "'");

    }

    public boolean playerExists(UUID uuid) {
        ResultSet res = query("SELECT `id` FROM `link_players` WHERE `uuid`='" + uuid.toString() + "'");
        try {
            return res.next();
        } catch (Exception e){
            return false;
        }
    }

    public void clearLinkPlayerDataFromDB(UUID uuid) {
        update("UPDATE `link_players` SET `playerdata`=NULL,`teleportType`=NULL WHERE `uuid`='" + uuid.toString() + "'");
    }

    public ResultSet query(String sql) {
        try {
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int update(String sql) {
        try {
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
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
