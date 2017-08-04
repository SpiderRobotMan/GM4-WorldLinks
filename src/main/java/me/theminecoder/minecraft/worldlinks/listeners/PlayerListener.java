package me.theminecoder.minecraft.worldlinks.listeners;

import com.google.common.collect.Range;
import me.theminecoder.minecraft.worldlinks.WorldLinks;
import me.theminecoder.minecraft.worldlinks.objects.*;
import me.theminecoder.minecraft.worldlinks.utils.ServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.stream.Collectors;

/**
 * @author theminecoder
 */
public class PlayerListener implements Listener {

    private WorldLinks plugin;

    public PlayerListener(WorldLinks plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        try {
            LinkPlayer player = plugin.getLinkPlayerDao().createIfNotExists(new LinkPlayer(event.getUniqueId()));
            plugin.getPlayerManager().getPlayerMap().put(event.getUniqueId(), player);
        } catch (Exception e) {
            e.printStackTrace();
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(ChatColor.RED + "Failed to load WorldLinks player data.");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final LinkPlayer player = plugin.getPlayerManager().getPlayer(event.getPlayer());

        Link activeLink = player.getActiveLink();
        LinkLocation oldLocation = player.getOldLocation();
        if (activeLink == null) {
            return;
        }

        player.setActiveLink(null);
        player.setOldLocation(null);

        // clear the data from the db in an async thread.
        new BukkitRunnable() {
            public void run() {
                try {
                    player.update();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);

        if(!activeLink.getServer().equalsIgnoreCase(plugin.getServerName())) {
            return;
        }

        // absolute means position them exactly where the coordinates state.
        event.getPlayer().teleport(activeLink.getLinkType().getFixedLocation(oldLocation.getBukkitLocation(), activeLink.getLocation().getBukkitLocation()));
    }

    @EventHandler
    public void onSwitch(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        LinkPlayer linkPlayer = plugin.getPlayer(player);
        ItemStack item = player.getInventory().getItem(event.getNewSlot());

        //Check if the item they switched to is the selector item.
        if (plugin.getSelectorItem().isSimilar(item)) {
            linkPlayer.setViewingWorldLinks(true);
        } else {
            linkPlayer.setViewingWorldLinks(false);
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        LinkPlayer linkPlayer = plugin.getPlayer(player);
        ItemStack item = player.getItemInHand(); //Deprecated but compatible with older versions.

        if (!linkPlayer.isViewingWorldLinks()) {
            return;
        }

        //Check if the item in their hand is the selector item.
        if (!plugin.getSelectorItem().isSimilar(item)) {
            return;
        }

        //Loop through all unlocked links and try and find a match.
        for (Link link : linkPlayer.getUnlockedLinks().stream().map(LinkUnlock::getLink).filter(link -> link.getConditions().stream().allMatch(linkCondition ->
                linkCondition.getType().valid(player, linkPlayer, linkCondition)
        )).collect(Collectors.toList())) {
            if (Range.closed(link.getParticleAngle() - 20, link.getParticleAngle() + 20).contains(new Float(player.getLocation().getYaw()).intValue())) {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    try {
                        plugin.getLinkTravelDao().create(new LinkTravel(linkPlayer, Bukkit.getServerId(), link.getServer(), link));
                        linkPlayer.setActiveLink(link);
                        linkPlayer.setOldLocation(new LinkLocation(player.getLocation()));
                        linkPlayer.update();
                        ServerUtils.sendPlayerToServer(plugin, player, link.getServer());
                    } catch (SQLException e) {
                        player.sendMessage(net.md_5.bungee.api.ChatColor.RED + "Error occurred in transport system, please contact the admins.");
                        e.printStackTrace();
                    }
                });
                return;
            }
        }
    }

}
