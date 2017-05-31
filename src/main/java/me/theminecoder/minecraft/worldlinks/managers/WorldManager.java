package me.theminecoder.minecraft.worldlinks.managers;

import me.theminecoder.minecraft.worldlinks.WorldLinks;
import me.theminecoder.minecraft.worldlinks.link.Link;
import me.theminecoder.minecraft.worldlinks.objects.LinkTravel;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        this.loadWorldLinks();
    }

    /**
     * Loads the world links from the configuration.
     */
    public void loadWorldLinks() {
        ConfigurationSection linkConfig = plugin.getConfig().getConfigurationSection("links");

        //Check that there are valid links to process.
        if (linkConfig == null || linkConfig.getKeys(false).isEmpty()) {
            plugin.getLogger().info("No world links were found in the configuration.");
            return;
        }

        //Loop through each link found and process it.
        for (String key : linkConfig.getKeys(false)) {
            if (!linkConfig.isConfigurationSection(key)) {
                plugin.getLogger().info("Failed to load link with ID '" + key + "': Not a configuration section.");
                continue;
            }

            Link link = null;

            try {
                link = new Link(linkConfig.getConfigurationSection(key));
            } catch (InvalidConfigurationException e) {
                plugin.getLogger().info("Failed to load link with ID '" + key + "': " + e.getMessage());
                continue;
            }

            //Prevent two links with the same name being registered.
            if (getWorldLink(link.getName()) != null) {
                plugin.getLogger().info("Failed to load link with ID '" + key + "': A link with the name '" + link.getName() + "' already exists.");
                continue;
            }

            worldLinks.add(link);
            plugin.getLogger().info("Successfully loaded link: " + link.getName());
        }

        plugin.getLogger().info("Finished loading links.");
        plugin.getLogger().info("A total of " + worldLinks.size() + " links were loaded.");
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
     * @param name The name
     * @return A link, or null if not found
     */
    public Link getWorldLink(String name) {
        for (Link link : getWorldLinks()) {
            if (link.getName().equalsIgnoreCase(name)) {
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

    @EventHandler
    public void onSwitch(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItem(event.getNewSlot());

        //Check if the item they switched to is the selector item.
        if (isSelectorItem(item)) {
            showWorldLinks(player, true);
        } else {
            showWorldLinks(player, false);
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand(); //Deprecated but compatible with older versions.

        //Check if the item in their hand is the selector item.
        if (!isSelectorItem(item)) {
            return;
        }

        List<Block> blocks = player.getLineOfSight((Set<Material>) null, 8);

        //Loop through all results blocks and attempt to match them.
        for (Block block : blocks) {
            Location blockLoc = block.getLocation().add(0.5D, 0.5D, 0.5D);

            //Loop through all world links and try and find a match.
            for (Link link : getWorldLinks()) {
                Location fixedLoc = link.calculatePositionRelativeTo(player.getEyeLocation());

                //Check if there was a match.
                if (fixedLoc.distance(blockLoc) <= 0.1) {
                    plugin.getLinkTravelDao().create(new LinkTravel(player.getUniqueId(), Bukkit.getServerId(), link.getServer(), link))
                    plugin.getPlayerManager().getPlayer(player).transport(link);
                    return;
                }
            }

            //Check if we've hit a dead end.
            if (block.getType() != Material.AIR) {
                return;
            }
        }
    }

}
