package co.gm4.worldlink.utils;

import co.gm4.worldlink.WorldLink;
import co.gm4.worldlink.modules.triggers.Advancement;
import co.gm4.worldlink.objects.Link;
import co.gm4.worldlink.objects.LinkLocation;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by MatrixTunnel on 9/10/2017.
 */
@Getter
public class Config {

    private final FileConfiguration config = WorldLink.get().getConfig();

    private final String serverName;

    private final String databaseHost, databasePort, databaseDatabase, databaseUsername, databasePassword;

    private final ItemStack selectorItem;
    private final boolean selectorItemHasToMatch;

    private final List<Link> links;

    public Config() {
        WorldLink.get().saveDefaultConfig();
        WorldLink.get().reloadConfig();

        serverName = config.getString("server.name", "GM4-" + UUID.randomUUID().toString().substring(0, 8));

        databaseHost = config.getString("server.database.host", "127.0.0.1");
        databasePort = config.getString("server.database.port", "3306");
        databaseDatabase = config.getString("server.database.database", "default");
        databaseUsername = config.getString("server.database.username", "root");
        databasePassword = config.getString("server.database.password", "");

        selectorItem = getSelectorItem(config.getString("server.selector.material", "STICK"), config.getString("server.selector.display_name", "World Link"), config.getStringList("server.selector.lore"));
        selectorItemHasToMatch = config.getBoolean("exact_match");

        links = new ArrayList<>();

        config.getConfigurationSection("links").getKeys(false).forEach(s -> {
            Link link = new Link();
            link.setName(s);

            if (config.getString("links." + s + ".triggers.advancement", null) != null) {
                link.setUnlockAdvancementKey(config.getString("links." + s + ".triggers.advancement", null));
                WorldLink.get().getModules().add(new Advancement());
            }

            if (config.getConfigurationSection("links." + s + ".display_conditions") != null) {
                link.setDisplayAdvancements(config.getStringList("links." + s + ".display_conditions.advancements"));
            }

            if (config.getConfigurationSection("links." + s + ".effects") != null) {
                link.setZoomOnClick(config.getBoolean("links." + s + ".effects.zoom", true));

                link.setDisplayType(config.getString("links." + s + ".effects.particles.display.type", "FLAME"));
                link.setDisplaySpeed(config.getInt("links." + s + ".effects.particles.display.speed", 0));
                link.setDisplayCount(config.getInt("links." + s + ".effects.particles.display.count", 1));

                if (config.getConfigurationSection("links." + s + ".effects.particles.hover") != null) {
                    link.setHoverType(config.getString("links." + s + ".effects.particles.hover.type", "PORTAL"));
                    link.setHoverSpeed(config.getInt("links." + s + ".effects.particles.hover.speed", 0));
                    link.setHoverCount(config.getInt("links." + s + ".effects.particles.hover.count", 1));
                }
            }

            if (config.getConfigurationSection("links." + s + ".teleportation") != null) {
                link.setTeleportType(config.getString("links." + s + ".teleportation.type", "ABSOLUTE"));

                String world = config.getString("links." + s + ".teleportation.location.world", "world");
                double x = config.getDouble("links." + s + ".teleportation.location.x", 0);
                double y = config.getDouble("links." + s + ".teleportation.location.y", 0);
                double z = config.getDouble("links." + s + ".teleportation.location.z", 0);
                float yaw = ((Double) config.getDouble("links." + s + ".teleportation.location.yaw", 0)).floatValue();
                float pitch = ((Double) config.getDouble("links." + s + ".teleportation.location.pitch", 0)).floatValue();
                LinkLocation linkLocation = new LinkLocation(world, x, y, z, yaw, pitch);
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

    private ItemStack getSelectorItem(String type, String displayName, List<String> lore) {
        ItemStack itemStack = new ItemStack(Material.getMaterial(type));
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (displayName != null) {
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        }

        if (!lore.isEmpty()) {
            lore.forEach(l -> l = ChatColor.translateAlternateColorCodes('&', l));
            itemMeta.setLore(lore);
        }

        return itemStack;
    }

    public boolean selectorItemHasToMatch() {
        return selectorItemHasToMatch;
    }

}
