package me.theminecoder.minecraft.worldlinks.gui;

import me.theminecoder.minecraft.worldlinks.WorldLinks;
import me.theminecoder.minecraft.worldlinks.objects.LinkLocation;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

/**
 * @author theminecoder
 */
public class LinkLocationEditGUI extends GUI {

    private LinkLocation location;
    private GUI returnGui;

    public LinkLocationEditGUI(LinkLocation location, GUI returnGUI) {
        super("Edit Location", 9);
        this.location = location;
        this.returnGui = returnGUI;
    }

    @Override
    protected void onPlayerClick(InventoryClickEvent event) {
        if (event.getRawSlot() == 0) {
            if (event.isLeftClick()) {
                this.close();
                new AnvilGUI(WorldLinks.getInstance(), (Player) event.getWhoClicked(), "", (player, string) -> {
                    location.setWorld(string);
                    this.scheduleOpen(new LinkLocationEditGUI(location, returnGui), (Player) event.getWhoClicked());
                    return null;
                });
                return;
            }
            return;
        }

        if (event.getRawSlot() == 1) {
            if (event.isLeftClick()) {
                new AnvilGUI(WorldLinks.getInstance(), (Player) event.getWhoClicked(), "", (player, number) -> {
                    double finalNumber;
                    try {
                        finalNumber = Double.parseDouble(number);
                    } catch (NumberFormatException e) {
                        return "Invalid Number";
                    }
                    this.location.setX(finalNumber);
                    this.scheduleOpen(new LinkLocationEditGUI(location, returnGui), (Player) event.getWhoClicked());
                    return null;
                });
                return;
            }
            return;
        }

        if (event.getRawSlot() == 2) {
            if (event.isLeftClick()) {
                new AnvilGUI(WorldLinks.getInstance(), (Player) event.getWhoClicked(), "", (player, number) -> {
                    double finalNumber;
                    try {
                        finalNumber = Double.parseDouble(number);
                    } catch (NumberFormatException e) {
                        return "Invalid Number";
                    }
                    this.location.setY(finalNumber);
                    this.scheduleOpen(new LinkLocationEditGUI(location, returnGui), (Player) event.getWhoClicked());
                    return null;
                });
                return;
            }
            return;
        }

        if (event.getRawSlot() == 3) {
            if (event.isLeftClick()) {
                new AnvilGUI(WorldLinks.getInstance(), (Player) event.getWhoClicked(), "", (player, number) -> {
                    double finalNumber;
                    try {
                        finalNumber = Double.parseDouble(number);
                    } catch (NumberFormatException e) {
                        return "Invalid Number";
                    }
                    this.location.setZ(finalNumber);
                    this.scheduleOpen(new LinkLocationEditGUI(location, returnGui), (Player) event.getWhoClicked());
                    return null;
                });
                return;
            }
            return;
        }

        if (event.getRawSlot() == 4) {
            if (event.isLeftClick()) {
                new AnvilGUI(WorldLinks.getInstance(), (Player) event.getWhoClicked(), "", (player, number) -> {
                    float finalNumber;
                    try {
                        finalNumber = Float.parseFloat(number);
                    } catch (NumberFormatException e) {
                        return "Invalid Number";
                    }
                    this.location.setYaw(finalNumber);
                    this.scheduleOpen(new LinkLocationEditGUI(location, returnGui), (Player) event.getWhoClicked());
                    return null;
                });
                return;
            }
            return;
        }

        if (event.getRawSlot() == 5) {
            if (event.isLeftClick()) {
                new AnvilGUI(WorldLinks.getInstance(), (Player) event.getWhoClicked(), "", (player, number) -> {
                    float finalNumber;
                    try {
                        finalNumber = Float.parseFloat(number);
                    } catch (NumberFormatException e) {
                        return "Invalid Number";
                    }
                    this.location.setPitch(finalNumber);
                    this.scheduleOpen(new LinkLocationEditGUI(location, returnGui), (Player) event.getWhoClicked());
                    return null;
                });
                return;
            }
            return;
        }

        if (event.getRawSlot() == 8) {
            if (event.isLeftClick()) {
                this.close();
                this.scheduleOpen(returnGui, (Player) event.getWhoClicked());
                return;
            }
            return;
        }
    }

    @Override
    protected void populate() {
        {
            ItemStack stack = new ItemStack(Material.ENDER_PORTAL_FRAME);
            ItemMeta itemMeta = stack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "World");
            itemMeta.setLore(Arrays.asList(
                    location.getWorld() != null && location.getWorld().trim().length() > 0 ?
                            ChatColor.WHITE + location.getWorld() :
                            ChatColor.RED + "No world set!",
                    "",
                    ChatColor.YELLOW + "" + ChatColor.BOLD + "Click" + ChatColor.WHITE + " to edit"
            ));
            stack.setItemMeta(itemMeta);
            this.inventory.setItem(0, stack);
        }

        {
            ItemStack stack = new ItemStack(Material.COMPASS);
            ItemMeta itemMeta = stack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "X");
            itemMeta.setLore(Arrays.asList(
                    ChatColor.WHITE + "" + location.getX(),
                    "",
                    ChatColor.YELLOW + "" + ChatColor.BOLD + "Click" + ChatColor.WHITE + " to edit"
            ));
            stack.setItemMeta(itemMeta);
            this.inventory.setItem(1, stack);
        }

        {
            ItemStack stack = new ItemStack(Material.COMPASS);
            ItemMeta itemMeta = stack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Y");
            itemMeta.setLore(Arrays.asList(
                    ChatColor.WHITE + "" + location.getY(),
                    "",
                    ChatColor.YELLOW + "" + ChatColor.BOLD + "Click" + ChatColor.WHITE + " to edit"
            ));
            stack.setItemMeta(itemMeta);
            this.inventory.setItem(2, stack);
        }

        {
            ItemStack stack = new ItemStack(Material.COMPASS);
            ItemMeta itemMeta = stack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Z");
            itemMeta.setLore(Arrays.asList(
                    ChatColor.WHITE + "" + location.getZ(),
                    "",
                    ChatColor.YELLOW + "" + ChatColor.BOLD + "Click" + ChatColor.WHITE + " to edit"
            ));
            stack.setItemMeta(itemMeta);
            this.inventory.setItem(3, stack);
        }

        {
            ItemStack stack = new ItemStack(Material.COMPASS);
            ItemMeta itemMeta = stack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Yaw");
            itemMeta.setLore(Arrays.asList(
                    ChatColor.WHITE + "" + location.getYaw(),
                    "",
                    ChatColor.YELLOW + "" + ChatColor.BOLD + "Click" + ChatColor.WHITE + " to edit"
            ));
            stack.setItemMeta(itemMeta);
            this.inventory.setItem(4, stack);
        }

        {
            ItemStack stack = new ItemStack(Material.COMPASS);
            ItemMeta itemMeta = stack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Pitch");
            itemMeta.setLore(Arrays.asList(
                    ChatColor.WHITE + "" + location.getPitch(),
                    "",
                    ChatColor.YELLOW + "" + ChatColor.BOLD + "Click" + ChatColor.WHITE + " to edit"
            ));
            stack.setItemMeta(itemMeta);
            this.inventory.setItem(5, stack);
        }

        {
            ItemStack stack = new ItemStack(Material.EMERALD_BLOCK);
            ItemMeta itemMeta = stack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Save Location");
            itemMeta.setLore(Arrays.asList(
                    "",
                    ChatColor.YELLOW + "" + ChatColor.BOLD + "Click" + ChatColor.WHITE + " to save!"
            ));
            stack.setItemMeta(itemMeta);
            this.inventory.setItem(8, stack);
        }
    }
}
