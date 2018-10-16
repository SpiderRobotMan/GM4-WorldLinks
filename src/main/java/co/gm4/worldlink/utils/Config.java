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
import co.gm4.worldlink.modules.triggers.Advancement;
import co.gm4.worldlink.objects.Link;
import co.gm4.worldlink.objects.LinkLocation;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by MatrixTunnel on 9/10/2017.
 */
@Getter
public class Config {

    private final FileConfiguration config;

    private final String serverName;

    private final String databaseHost, databasePort, databaseDatabase, databaseUsername, databasePassword;

    private final ItemStack selectorItem;
    private final boolean selectorItemHasToMatch;

    private final String defaultSpawnServer;
    private final String defaultSpawnWorld;
    private final String defaultSpawnLocation;

    private final List<Link> links;

    public Config() {
        WorldLink.get().saveDefaultConfig();
        WorldLink.get().reloadConfig();

        config = WorldLink.get().getConfig();

        serverName = config.getString("server.name", "GM4-" + UUID.randomUUID().toString().substring(0, 8));

        databaseHost = config.getString("server.database.host", "127.0.0.1");
        databasePort = config.getString("server.database.port", "3306");
        databaseDatabase = config.getString("server.database.database", "default");
        databaseUsername = config.getString("server.database.username", "root");
        databasePassword = config.getString("server.database.password", "");

        selectorItem = getSelectorItem(config.getString("server.selector.material", "STICK"), (short) config.getInt("server.selector.data", 0), config.getString("server.selector.display_name", "World Selector"), config.getStringList("server.selector.lore"));
        selectorItemHasToMatch = config.getBoolean("server.selector.exact_match");


        if (config.getBoolean("server.default_spawn.enabled", false)) {
            defaultSpawnServer = config.getString("server.default_spawn.server", "%same%");
            defaultSpawnWorld = config.getString("server.default_spawn.world", Bukkit.getWorlds().get(0).getName()).replace("%default%", Bukkit.getWorlds().get(0).getName()); // Replaced %same% in respawn event
            defaultSpawnLocation = config.getString("server.default_spawn.location", "0.0, 0.0, 0.0");
        } else {
            defaultSpawnServer = "";
            defaultSpawnWorld = Bukkit.getWorlds().get(0).getName();
            defaultSpawnLocation = "";
        }

        links = new ArrayList<>();

        config.getConfigurationSection("links").getKeys(false).forEach(s -> {
            Link link = new Link();
            link.setName(s);

            if (config.getString("links." + s + ".triggers.advancement", null) != null) {
                link.setUnlockAdvancementKey(config.getString("links." + s + ".triggers.advancement", null));
                if (WorldLink.get().getModules().stream().noneMatch(module -> module instanceof Advancement)) WorldLink.get().getModules().add(new Advancement());
            }

            if (config.getConfigurationSection("links." + s + ".display_conditions") != null) {
                link.setDisplayAdvancements(config.getStringList("links." + s + ".display_conditions.advancements"));

                link.setDisplayLocation(config.getString("links." + s + ".display_conditions.location"));

                link.setOffhandItem(config.getString("links." + s + ".display_conditions.offhand_item"));
            }

            if (config.getConfigurationSection("links." + s + ".effects") != null) {
                link.setZoomOnClick(config.getBoolean("links." + s + ".effects.zoom", true));
                link.setBlindnessOnClick(config.getBoolean("links." + s + ".effects.blindness", true));
                link.setPortalSoundOnClick(config.getBoolean("links." + s + ".effects.sound", true));

                link.setDisplayType(config.getString("links." + s + ".effects.particles.display.type", "FLAME"));
                String displayOffset = config.getString("links." + s + ".effects.particles.display.offset", "0.0, 0.0, 0.0");
                List<String> displayOffsetList = Arrays.stream(displayOffset.split(", ")).collect(Collectors.toList());
                if (!displayOffsetList.isEmpty() && displayOffsetList.size() == 3) {
                    link.setDisplayOffsetX(Double.parseDouble(displayOffsetList.get(0)));
                    link.setDisplayOffsetY(Double.parseDouble(displayOffsetList.get(1)));
                    link.setDisplayOffsetZ(Double.parseDouble(displayOffsetList.get(2)));
                }
                link.setDisplaySpeed(config.getDouble("links." + s + ".effects.particles.display.speed", 0.0));
                link.setDisplayCount(config.getInt("links." + s + ".effects.particles.display.count", 1));

                if (config.getConfigurationSection("links." + s + ".effects.particles.hover") != null) {
                    link.setHoverType(config.getString("links." + s + ".effects.particles.hover.type", "PORTAL"));
                    String hoverOffset = config.getString("links." + s + ".effects.particles.hover.offset", "0.0, 0.0, 0.0");
                    List<String> hoverOffsetList = Arrays.stream(hoverOffset.split(", ")).collect(Collectors.toList());
                    if (!hoverOffsetList.isEmpty() && hoverOffsetList.size() == 3) {
                        link.setHoverOffsetX(Double.parseDouble(hoverOffsetList.get(0)));
                        link.setHoverOffsetY(Double.parseDouble(hoverOffsetList.get(1)));
                        link.setHoverOffsetZ(Double.parseDouble(hoverOffsetList.get(2)));
                    }
                    link.setHoverSpeed(config.getInt("links." + s + ".effects.particles.hover.speed", 0));
                    link.setHoverCount(config.getInt("links." + s + ".effects.particles.hover.count", 1));
                }
            }

            if (config.getConfigurationSection("links." + s + ".teleportation") != null) {
                link.setResetRespawnLocation(config.getBoolean("links." + s + ".teleportation.clear_respawn", false));

                link.setTeleportType(config.getString("links." + s + ".teleportation.type", "ABSOLUTE"));

                String world = config.getString("links." + s + ".teleportation.location.world", "world");
                double x = config.getDouble("links." + s + ".teleportation.location.x", 0);
                double y = config.getDouble("links." + s + ".teleportation.location.y", 0);
                double z = config.getDouble("links." + s + ".teleportation.location.z", 0);
                float yaw = ((Double) config.getDouble("links." + s + ".teleportation.location.yaw", 0)).floatValue();
                float pitch = ((Double) config.getDouble("links." + s + ".teleportation.location.pitch", 0)).floatValue();
                LinkLocation linkLocation = new LinkLocation(world, x, y, z, yaw, pitch);
                linkLocation.setIgnoreX(!config.contains("links." + s + ".teleportation.location.x"));
                linkLocation.setIgnoreY(!config.contains("links." + s + ".teleportation.location.y"));
                linkLocation.setIgnoreZ(!config.contains("links." + s + ".teleportation.location.z"));
                linkLocation.setIgnoreYaw(!config.contains("links." + s + ".teleportation.location.yaw"));
                linkLocation.setIgnorePitch(!config.contains("links." + s + ".teleportation.location.pitch"));
                link.setTargetLocation(linkLocation);

                if (config.getConfigurationSection("links." + s + ".teleportation.commands") != null) {
                    link.setBeforeCommands(config.getStringList("links." + s + ".teleportation.commands.before"));
                    link.setDuringCommands(config.getStringList("links." + s + ".teleportation.commands.during"));
                    link.setAfterCommands(config.getStringList("links." + s + ".teleportation.commands.after"));
                }
            }

            links.add(link);
        });
    }

    private ItemStack getSelectorItem(String type, short data, String displayName, List<String> lore) {
        ItemStack itemStack = new ItemStack(Material.getMaterial(type), 1, data);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (displayName != null) {
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        }

        if (!lore.isEmpty()) {
            lore.forEach(l -> l = ChatColor.translateAlternateColorCodes('&', l));
            itemMeta.setLore(lore);
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public boolean selectorItemHasToMatch() {
        return selectorItemHasToMatch;
    }

}
