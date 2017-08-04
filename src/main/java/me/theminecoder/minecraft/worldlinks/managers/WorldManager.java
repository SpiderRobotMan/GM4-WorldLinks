package me.theminecoder.minecraft.worldlinks.managers;

import me.theminecoder.minecraft.worldlinks.WorldLinks;
import me.theminecoder.minecraft.worldlinks.objects.Link;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WorldManager implements Listener {

    private WorldLinks plugin;

    private List<Link> worldLinks = new ArrayList<Link>();
    private List<String> showLinks = new ArrayList<String>();

    /**
     * Constructs a new instance of the player manager and registers
     * itself as a listener.
     *
     * @param plugin The plugin
     */
    public WorldManager(WorldLinks plugin) {
        this.plugin = plugin;
        try {
            this.reloadWorldLinks();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads the world links from the configuration.
     */
    public void reloadWorldLinks() throws SQLException {
        this.worldLinks = plugin.getLinkDao().queryForAll();
        plugin.getLogger().info("Loaded " + this.worldLinks.size() + " links!");
    }

    /**
     * Gets a list of registered world links.
     *
     * @return List of links
     */
    public List<Link> getWorldLinks() {
        return new ArrayList<Link>(worldLinks);
    }

    /**
     * Gets a registered world link by its name.
     *
     * @param id The ID
     * @return A link, or null if not found
     */
    public Link getWorldLink(String id) {
        for (Link link : getWorldLinks()) {
            if (link.getId().equalsIgnoreCase(id)) {
                return link;
            }
        }
        return null;
    }

    /**
     * Gets whether a player is currently viewing the world links.
     *
     * @param player The player
     * @return True if enabled, otherwise false
     */
    public boolean isViewingWorldLinks(Player player) {
        return showLinks.contains(player.getName());
    }

    /**
     * Toggles whether the world links should be visible to the player.
     *
     * @param player The player
     */
    public void showWorldLinks(Player player, boolean enabled) {
        if (enabled && !showLinks.contains(player.getName())) {
            showLinks.add(player.getName());
        } else if (!enabled) {
            showLinks.remove(player.getName());
        }
    }

    /**
     * Checks whether an item is the world selector item.
     *
     * @param item The item to check
     * @return True if match, otherwise false
     */
    public boolean isSelectorItem(ItemStack item) {
        if (item != null && item.getType() == Material.STICK) {
            return true;
        }
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();

        //Check if the item has any lore values.
        if (!meta.hasLore() || meta.getLore().isEmpty()) {
            return false;
        }

        String identifiableLore = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("selector.identifiable_lore", "World Selector"));

        //Loop through the lore and check for a match.
        for (String line : meta.getLore()) {
            if (line.equals(identifiableLore)) {
                return true;
            }
        }

        return false;
    }

}
