package co.gm4.worldlink.utils;

import co.gm4.worldlink.WorldLink;
import co.gm4.worldlink.objects.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MatrixTunnel on 9/12/2017.
 */
public class DisplayTask implements Listener, Runnable {

    private BukkitTask playerLeftTask;

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (PlayerUtils.isViewingLinks(player)) {
                LinkPlayer linkPlayer = WorldLink.get().getPlayerManager().getLinkPlayer(player.getUniqueId());

                if (linkPlayer == null) continue;

                List<LinkWorld> worlds = linkPlayer.getFilteredWorlds();

                if (worlds.size() > 0) {
                    for (int i = 0; i < worlds.size(); i++) {
                        double angle = (2 * Math.PI * i / worlds.size());
                        Vector point = new Vector(Math.cos(angle) * 1, -0.4, Math.sin(angle) * 1);

                        //boolean found = false;
                        Particle hoverParticle = null;
                        int hoverSpeed = 1;
                        int hoverCount = 0;

                        for (Link link : WorldLink.get().getPluginConfig().getLinks()) {
                            if (link.getName().equals(worlds.get(i).getName())) {
                                //found = true;
                                player.spawnParticle(Particle.valueOf(link.getDisplayType()), player.getEyeLocation().clone().add(point), link.getDisplayCount(), 0.0, 0.0, 0.0, link.getDisplaySpeed());
                                if (link.getHoverType() != null) {
                                    hoverParticle = Particle.valueOf(link.getHoverType());
                                    hoverCount = link.getHoverCount();
                                    hoverSpeed = link.getHoverSpeed();
                                }
                                break;
                            }
                        }

                        //if (!found) {
                        //    player.spawnParticle(Particle.SMOKE_NORMAL, player.getEyeLocation().clone().add(point), 1, 0.0, 0.0, 0.0, 0);
                        //}

                        if (player.getLocation().getDirection().distance(point) < 0.17) {
                            if (player.isOp()) player.sendTitle("", ChatColor.AQUA + worlds.get(i).getName(), 0, 4, 2); //TODO Change for perms and stuff (event worlds too)
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.YELLOW + "Right click to travel"));
                            if (hoverParticle != null)
                                player.spawnParticle(hoverParticle, player.getEyeLocation().clone().add(point), hoverCount, 0.0, 0.0, 0.0, hoverSpeed);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        LinkPlayer linkPlayer = WorldLink.get().getPlayerManager().getLinkPlayer(player.getUniqueId());

        if (linkPlayer.isGettingTransferred() || event.getHand() == EquipmentSlot.OFF_HAND || !PlayerUtils.isViewingLinks(event.getPlayer())) return;

        if (event.getAction().name().contains("RIGHT_CLICK")) {
            List<LinkWorld> worlds = linkPlayer.getFilteredWorlds();

            for (int i = 0; i < worlds.size(); i++) {
                double angle = (2 * Math.PI * i / worlds.size());
                Vector point = new Vector(Math.cos(angle) * 1, -0.4, Math.sin(angle) * 1);

                if (player.getLocation().getDirection().distance(point) < 0.17) {

                    for (Link link : WorldLink.get().getPluginConfig().getLinks()) {
                        if (link.getName().equals(worlds.get(i).getName())) {
                            linkPlayer.setGettingTransferred(true);
                            event.setCancelled(true);

                            int I = i;
                            new ArrayList<>(link.getBeforeCommands()).forEach(s -> runCommand(link, worlds.get(I), linkPlayer, s));
                            LinkPlayerData playerData = new LinkPlayerData(player);

                            LinkLocationType locationType = LinkLocationType.getByConfigName(link.getTeleportType());

                            if (locationType != null) {
                                playerData.setLocation(locationType.getFixedLocation(LinkLocation.deserialize(event.getPlayer().getLocation().serialize()), link.getTargetLocation()));
                                WorldLink.get().getPlayerManager().getLinkPlayer(player).setLocationType(locationType);
                            }

                            WorldLink.get().getPlayerManager().getLinkPlayer(player).setPlayerData(playerData);
                            WorldLink.get().getPlayerManager().getLinkPlayer(player).setLocationType(locationType);

                            new ArrayList<>(link.getDuringCommands()).forEach(s -> runCommand(link, worlds.get(I), linkPlayer, s));

                            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 15)); //TODO Add toggle effect to config
                            if (link.isZoomOnClick()) player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 255));

                            try {
                                WorldLink.get().getDatabaseHandler().savePlayer(player.getUniqueId());
                            } catch (SQLException e) {
                                player.sendMessage(ChatColor.RED + "You're too heavy for the teleporter to pick you up! Empty your inventory to travel.");
                                WorldLink.get().getLogger().warning("Player's inventory it too large to travel: " + player.getUniqueId().toString());
                                e.printStackTrace();
                                break;
                            }

                            ServerUtils.sendToServer(player, link.getName());
                            playerLeftTask = Bukkit.getScheduler().runTaskTimer(WorldLink.get(), () -> {
                                if (!player.isOnline()) {
                                    new ArrayList<>(link.getAfterCommands()).forEach(s -> runCommand(link, worlds.get(I), linkPlayer, s));
                                    playerLeftTask.cancel();
                                }
                            }, 3L, 3L);
                            break;
                        }
                    }
                }
            }
        }
    }

    private boolean runCommand(Link link, LinkWorld linkWorld, LinkPlayer linkPlayer, String command) {
        boolean run = false;

        if(!command.equals("")) {
            run = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", linkPlayer.getPlayer().getName()).replace("%world%", linkWorld.getName()));
        }

        return run;
    }
}
