/**
 * WorldLink - Multi-Dimensional Survival Server
 * Copyright (C) 2017, 18  Gamemode 4
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package co.gm4.worldlink.listeners;

import co.gm4.worldlink.WorldLink;
import co.gm4.worldlink.managers.DatabaseHandler;
import co.gm4.worldlink.objects.*;
import co.gm4.worldlink.utils.PlayerUtils;
import co.gm4.worldlink.utils.ServerUtils;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by MatrixTunnel on 9/10/2017.
 */
@Getter
public class PlayerListener implements Listener {

    private List<QueuedJoin> queuedJoins = new ArrayList<>();
    private List<UUID> notMoved = new ArrayList<>();

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
        linkPlayer.setLastLoginTime(System.currentTimeMillis());
        linkPlayer.setGettingTransferred(false);

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
        if (!notMoved.contains(event.getPlayer().getUniqueId())) notMoved.add(event.getPlayer().getUniqueId());
        WorldLink.get().getServer().getScheduler().runTaskAsynchronously(WorldLink.get(), () -> {
            try {
                database.savePlayer(event.getPlayer().getUniqueId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if(event.getTarget() instanceof Player && notMoved.contains(event.getTarget().getUniqueId())) {
            event.setTarget(null);
        }
    }

    @EventHandler
    public void onHurt(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && notMoved.contains(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if ((System.currentTimeMillis() - WorldLink.get().getPlayerManager().getLinkPlayer(event.getPlayer()).getLastLoginTime()) < 4000) { // Don't remove them for 80 ticks = 4 seconds TODO Add config option for this
            return;
        }

        if (notMoved.contains(event.getPlayer().getUniqueId())) notMoved.remove(event.getPlayer().getUniqueId());
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        WorldLink.get().getPlayerManager().getLinkPlayer(event.getEntity()).setLastDeathLocation(event.getEntity().getLocation());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        LinkPlayer linkPlayer = WorldLink.get().getPlayerManager().getLinkPlayer(event.getPlayer());
        LinkLocation respawnLocation = WorldLink.get().getDatabaseHandler().getRespawnLocation(event.getPlayer().getUniqueId());
        Location deathLocation = linkPlayer.getLastDeathLocation();
        String defaultSpawn = WorldLink.get().getPluginConfig().getDefaultSpawnLocation();

        WorldLink.get().getLogger().info("Respawning player: " + event.getPlayer().getUniqueId().toString());

        if (respawnLocation != null) {
            if (respawnLocation.getWorld().equals(WorldLink.get().getPluginConfig().getServerName())) return;

            respawnPlayer(linkPlayer.getPlayer());
            ServerUtils.sendToLinkWorld(linkPlayer, new LinkWorld(respawnLocation.getWorld()), respawnLocation, LinkLocationType.ABSOLUTE_SAFE);
        } else if (deathLocation != null && !defaultSpawn.isEmpty()) {

            List<String> defaultSpawnList = Arrays.stream(defaultSpawn.split(", ")).collect(Collectors.toList());
            Location location = event.getRespawnLocation().clone();

            location.setWorld(Bukkit.getWorld(WorldLink.get().getPluginConfig().getDefaultSpawnWorld().replace("%same%", event.getRespawnLocation().getWorld().getName())));

            if (defaultSpawnList.size() >= 3) {
                location.setX(Double.parseDouble(defaultSpawnList.get(0).replace("~", String.valueOf(deathLocation.getX()))));
                location.setY(Double.parseDouble(defaultSpawnList.get(1).replace("~", String.valueOf(deathLocation.getY()))));
                location.setZ(Double.parseDouble(defaultSpawnList.get(2).replace("~", String.valueOf(deathLocation.getZ()))));
            }

            if (defaultSpawnList.size() == 5) {
                location.setYaw(Float.valueOf(defaultSpawnList.get(3).replace("~", String.valueOf(deathLocation.getYaw()))));
                location.setPitch(Float.valueOf(defaultSpawnList.get(4).replace("~", String.valueOf(deathLocation.getPitch()))));
            }

            String serverName = WorldLink.get().getPluginConfig().getDefaultSpawnServer();
            respawnPlayer(linkPlayer.getPlayer());

            if (!serverName.isEmpty() && !serverName.equals("%same%")) {
                ServerUtils.sendToLinkWorld(linkPlayer, new LinkWorld(serverName), new LinkLocation(location), LinkLocationType.ABSOLUTE_SAFE);
            } else {
                event.setRespawnLocation(location);
            }
        }

        // Should spawn at world spawn if nothing is set
    }

    private void respawnPlayer(Player player) {
        player.setExp(0);
        player.setFireTicks(0);
        player.setFallDistance(0f);
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSleep(PlayerBedEnterEvent event) {
        if (!event.isCancelled()) WorldLink.get().getDatabaseHandler().setRespawnLocation(event.getPlayer().getUniqueId(), event.getBed().getLocation());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        WorldLink.get().getServer().getScheduler().runTaskAsynchronously(WorldLink.get(), () -> {
            if (notMoved.contains(event.getPlayer().getUniqueId())) notMoved.remove(event.getPlayer().getUniqueId());
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
