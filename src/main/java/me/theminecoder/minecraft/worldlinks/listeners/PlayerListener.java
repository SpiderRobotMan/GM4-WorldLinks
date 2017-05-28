package me.theminecoder.minecraft.worldlinks.listeners;

import me.theminecoder.minecraft.worldlinks.WorldLinks;
import me.theminecoder.minecraft.worldlinks.objects.Link;
import me.theminecoder.minecraft.worldlinks.objects.LinkPlayer;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;

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
            plugin.getPlayerManager().getPlayerMap().put(event.getName(), player);
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
        if (activeLink == null) {
            return;
        }

        // clear the data from the db in an async thread.
        new BukkitRunnable() {
            public void run() {
                try {
                    player.setActiveLink(null);
                    player.update();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);

        // absolute means position them exactly where the coordinates state.
        event.getPlayer().teleport(activeLink.getLinkType().getFixedLocation(player.getOldLocation().getBukkitLocation(), activeLink.getLocation().getBukkitLocation()));
    }

}
