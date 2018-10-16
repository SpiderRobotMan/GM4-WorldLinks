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

package co.gm4.worldlink.utils;

import co.gm4.worldlink.WorldLink;
import co.gm4.worldlink.objects.Link;
import co.gm4.worldlink.objects.LinkPlayer;
import co.gm4.worldlink.objects.LinkWorld;
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
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Created by MatrixTunnel on 9/12/2017.
 */
public class DisplayTask implements Listener, Runnable {

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

                        Link linkDisplay = null;

                        for (Link link : WorldLink.get().getPluginConfig().getLinks()) {
                            if (link.getName().equals(worlds.get(i).getName())) {
                                linkDisplay = link;
                                break;
                            }
                        }

                        if (linkDisplay != null) {
                            player.spawnParticle(Particle.valueOf(linkDisplay.getDisplayType()), player.getEyeLocation().clone().add(point), linkDisplay.getDisplayCount(), linkDisplay.getDisplayOffsetX(), linkDisplay.getDisplayOffsetY(), linkDisplay.getDisplayOffsetZ(), linkDisplay.getDisplaySpeed());

                            if (player.getLocation().getDirection().distance(point) < 0.17) { // Player is hovering over the particle
                                if (linkDisplay.getHoverType() != null)
                                    player.spawnParticle(Particle.valueOf(linkDisplay.getHoverType()), player.getEyeLocation().clone().add(point), linkDisplay.getHoverCount(), linkDisplay.getHoverOffsetX(), linkDisplay.getHoverOffsetY(), linkDisplay.getHoverOffsetZ(), linkDisplay.getHoverSpeed());

                                if (player.isOp()) { //TODO Change for perms and stuff
                                    player.sendTitle("", ChatColor.AQUA + worlds.get(i).getName(), 0, 4, 2);
                                }

                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.YELLOW + "Right click to travel"));
                            }
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
            event.setCancelled(true);
            List<LinkWorld> worlds = linkPlayer.getFilteredWorlds();

            for (int i = 0; i < worlds.size(); i++) {
                double angle = (2 * Math.PI * i / worlds.size());
                Vector point = new Vector(Math.cos(angle) * 1, -0.4, Math.sin(angle) * 1);

                if (player.getLocation().getDirection().distance(point) < 0.17) {

                    for (Link link : WorldLink.get().getPluginConfig().getLinks()) {
                        if (link.getName().equals(worlds.get(i).getName())) {
                            event.setCancelled(true);
                            ServerUtils.sendToLink(link, linkPlayer, worlds.get(i), null);
                            break;
                        }
                    }
                }
            }
        }
    }

}
