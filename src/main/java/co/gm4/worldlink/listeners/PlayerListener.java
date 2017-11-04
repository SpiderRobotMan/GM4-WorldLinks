package co.gm4.worldlink.listeners;

import co.gm4.worldlink.WorldLink;
import co.gm4.worldlink.managers.DatabaseHandler;
import co.gm4.worldlink.objects.*;
import co.gm4.worldlink.utils.PlayerUtils;
import co.gm4.worldlink.utils.ServerUtils;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by MatrixTunnel on 9/10/2017.
 */
public class PlayerListener implements Listener {

    @Getter private List<QueuedJoin> queuedJoins = new ArrayList<>();

    public PlayerListener() {
        //empty queued joins when the connection didn't follow through for an unknown reason.
        WorldLink.get().getServer().getScheduler().runTaskTimerAsynchronously(WorldLink.get(), () -> {
            List<QueuedJoin> toRemove = new ArrayList<>();
            for (QueuedJoin queuedJoin : queuedJoins) {
                if (System.currentTimeMillis() - queuedJoin.getTime() > 10 * 1000) {
                    toRemove.add(queuedJoin);
                }
            }
            queuedJoins.removeAll(toRemove);
        }, 100L, 20 * 10); // 10 seconds
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        if (!event.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) return;
        DatabaseHandler database = WorldLink.get().getDatabaseHandler();
        QueuedJoin queuedJoin;

        try {
            if (!database.playerExists(event.getUniqueId())) {
                database.registerPlayer(event.getUniqueId());
            }

            LinkPlayer linkPlayer = database.getLinkPlayer(event.getUniqueId());
            queuedJoin = new QueuedJoin(event.getUniqueId(), linkPlayer, System.currentTimeMillis());
            setStatData(linkPlayer);
        } catch (SQLException e) {
            WorldLink.get().getLogger().warning("Failed to register player: " + event.getUniqueId().toString());
            e.printStackTrace();
            return;
        } catch (Exception e) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(org.bukkit.ChatColor.RED + "Failed to load LinkPlayer data.");

            Bukkit.getLogger().severe("Failed to load LinkPlayer data: " + event.getUniqueId());
            e.printStackTrace();
            return;
        }

        queuedJoins.add(queuedJoin);
    }

    @EventHandler
    public void onJoin(PlayerLoginEvent event) {
        DatabaseHandler database = WorldLink.get().getDatabaseHandler();
        QueuedJoin queuedJoin = getQueuedLinkPlayer(event.getPlayer().getUniqueId());

        if (queuedJoin == null) {
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(ChatColor.RED + "Unable to load LinkPlayer data. Please try again");
            Bukkit.getLogger().severe("Unable to LinkPlayer data: " + event.getPlayer().getName());
            return;
        }

        LinkPlayer linkPlayer = queuedJoin.getLinkPlayer();
        linkPlayer.setPlayer(event.getPlayer());

        WorldLink.get().getPlayerManager().addPlayer(linkPlayer);

        if (linkPlayer.getPlayerData() != null) {
            try {
                PlayerUtils.updatePlayer(event.getPlayer(), linkPlayer.getPlayerData(), linkPlayer.getLocationType());
                linkPlayer.setPlayerData(null);
                linkPlayer.setLocationType(null);
                linkPlayer.setAdvancementsJson(null);
                linkPlayer.setStatsJson(null);
            } catch (Exception e) {
                event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                event.setKickMessage(ChatColor.RED + "Unable to load LinkPlayer data. Please try again");
                WorldLink.get().getLogger().severe("Could not set player data on join!");
                e.printStackTrace();
                return;
            }
        }

        queuedJoins.remove(queuedJoin);
        WorldLink.get().getServer().getScheduler().runTaskAsynchronously(WorldLink.get(), () -> {
            try {
                database.savePlayer(event.getPlayer().getUniqueId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        LinkPlayer linkPlayer = WorldLink.get().getPlayerManager().getLinkPlayer(player.getUniqueId());
        List<Link> links = WorldLink.get().getPluginConfig().getLinks();

        if (player.getBedSpawnLocation() != null && !player.getBedSpawnLocation().getWorld().getName().equals(WorldLink.get().getPluginConfig().getServerName())) {
            Link link = links.stream().filter(link1 -> link1.getName().equals(player.getBedSpawnLocation().getWorld().getName())).findFirst().orElse(null);

            if (link != null) {
                ServerUtils.sendToLink(link, linkPlayer, new LinkWorld(link.getName()), new LinkLocation(player.getBedSpawnLocation())); // Override the normal location
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        WorldLink.get().getServer().getScheduler().runTaskAsynchronously(WorldLink.get(), () -> {
            if (WorldLink.get().getPlayerManager().getLinkPlayer(event.getPlayer()).getPlayerData() == null) WorldLink.get().getDatabaseHandler().savePlayerWorlds(event.getPlayer().getUniqueId()); // If the player data is not null, it means that the player worlds were already saved into the database.
            WorldLink.get().getPlayerManager().removePlayer(WorldLink.get().getPlayerManager().getLinkPlayer(event.getPlayer().getUniqueId()));
        });

    }

    private QueuedJoin getQueuedLinkPlayer(UUID uuid) {
        for (QueuedJoin queuedJoin : queuedJoins) {
            if (uuid.toString().equals(queuedJoin.getUuid().toString())) {
                return queuedJoin;
            }
        }
        return null;
    }

    private void writeFile(File file, String input) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file);
            writer.write(input);
            writer.close();
        } catch (IOException e) {
            WorldLink.get().getLogger().warning("Could not write file " + file.getName());
            e.printStackTrace();
        }
    }

    private void setStatData(LinkPlayer linkPlayer) {
        if (linkPlayer.getAdvancementsJson() != null) {
            writeFile(new File(Bukkit.getWorlds().get(0).getName() + "/advancements/" + linkPlayer.getUuid().toString() + ".json"), linkPlayer.getAdvancementsJson());
        }
        if (linkPlayer.getStatsJson() != null) {
            writeFile(new File(Bukkit.getWorlds().get(0).getName() + "/stats/" + linkPlayer.getUuid().toString() + ".json"), linkPlayer.getStatsJson());
        }
    }

}
