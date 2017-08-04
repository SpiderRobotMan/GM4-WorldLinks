package me.theminecoder.minecraft.worldlinks.gui;

import me.theminecoder.minecraft.worldlinks.WorldLinks;
import me.theminecoder.minecraft.worldlinks.objects.Link;
import me.theminecoder.minecraft.worldlinks.objects.LinkLocation;
import me.theminecoder.minecraft.worldlinks.objects.LinkType;
import net.wesjd.anvilgui.AnvilGUI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author theminecoder
 */
public class LinkEditGUI extends PagedGUI {

    private Link link;

    public LinkEditGUI(Link link) {
        super("Edit Link", 56);
        this.setHeaderRows(2);
        this.link = link;
    }

    @Override
    protected List<ItemStack> getIcons() {
        return IntStream.range(0, 20).mapToObj(condition -> {
            return new ItemStack(Material.REDSTONE);
        }).collect(Collectors.toList());
    }

    @Override
    protected void onPlayerClickIcon(InventoryClickEvent event) {
        if (event.getRawSlot() == 1) {
            if (event.isLeftClick()) {
                this.close();
                new AnvilGUI(WorldLinks.getInstance(), (Player) event.getWhoClicked(), "", (player, string) -> {
                    this.link.setName(string);
                    this.scheduleOpen(new LinkEditGUI(link), player);
                    return null;
                });
                return;
            } else if (event.isRightClick()) {
                this.link.setName(null);
                this.repopulate();
                return;
            }
            return;
        }

        if (event.getRawSlot() == 3) {
            if (event.isLeftClick()) {
                this.close();
                new AnvilGUI(WorldLinks.getInstance(), (Player) event.getWhoClicked(), "", (player, string) -> {
                    this.link.setServer(string);
                    this.scheduleOpen(new LinkEditGUI(link), player);
                    return null;
                });
                return;
            } else if (event.isRightClick()) {
                this.link.setServer(null);
                this.repopulate();
                return;
            }
            return;
        }

        if (event.getRawSlot() == 4) {
            if (event.isLeftClick()) {
                int newOrdinal = this.link.getLinkType().ordinal() + 1;
                if (LinkType.values().length - 1 < newOrdinal) {
                    newOrdinal = 0;
                }

                this.link.setLinkType(LinkType.values()[newOrdinal]);
                this.repopulate();
                return;
            }
            return;
        }

        if (event.getRawSlot() == 5) {
            if (event.isLeftClick()) {
                this.close();
                if (this.link.getLocation() == null) {
                    this.link.setLocation(new LinkLocation());
                }
                this.scheduleOpen(new LinkLocationEditGUI(this.link.getLocation(), new LinkEditGUI(this.link)), (Player) event.getWhoClicked());
                return;
            } else if (event.isRightClick()) {
                this.link.setLocation(null);
                this.repopulate();
                return;
            }
            return;
        }

        if (event.getRawSlot() == 7) {
            if (event.isLeftClick()) {
                this.close();
                new AnvilGUI(WorldLinks.getInstance(), (Player) event.getWhoClicked(), "", (player, number) -> {
                    int finalNumber;
                    try {
                        finalNumber = Integer.parseInt(number);
                        if (finalNumber < 0 || finalNumber > 359) {
                            return "Angle must be between 0 - 359";
                        }
                    } catch (NumberFormatException e) {
                        return "Invalid Number";
                    }
                    this.link.setParticleAngle(finalNumber);
                    new LinkEditGUI(this.link).open(player);
                    return null;
                });
                return;
            } else if (event.isRightClick()) {
                this.link.setParticleAngle(-1);
                this.repopulate();
                return;
            }
            return;
        }
    }

    @Override
    protected void populateSpecial() {
        {
            ItemStack stack = new ItemStack(Material.EYE_OF_ENDER);
            ItemMeta itemMeta = stack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Link ID");
            itemMeta.setLore(Arrays.asList(ChatColor.WHITE + link.getId()));
            stack.setItemMeta(itemMeta);
            this.inventory.setItem(0, stack);
        }

        {
            ItemStack stack = new ItemStack(Material.NAME_TAG);
            ItemMeta itemMeta = stack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Link Name");
            itemMeta.setLore(Stream.concat(
                    Stream.of(
                            link.getName() != null && link.getName().trim().length() > 0 ?
                                    ChatColor.WHITE + link.getName() :
                                    ChatColor.RED + "No name set!",
                            "",
                            ChatColor.YELLOW + "" + ChatColor.BOLD + "Click" + ChatColor.WHITE + " to edit"
                    ),
                    link.getName() != null && link.getName().trim().length() > 0 ?
                            Stream.of(ChatColor.YELLOW + "" + ChatColor.BOLD + "Right click" + ChatColor.WHITE + " to remove") :
                            Stream.empty()
            ).collect(Collectors.toList()));
            stack.setItemMeta(itemMeta);
            this.inventory.setItem(1, stack);
        }

        {
            ItemStack stack = new ItemStack(Material.ENDER_PORTAL_FRAME);
            ItemMeta itemMeta = stack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Link Server");
            itemMeta.setLore(Stream.concat(
                    Stream.of(
                            link.getServer() != null && link.getServer().trim().length() > 0 ?
                                    ChatColor.WHITE + link.getServer() :
                                    ChatColor.RED + "No server set!",
                            "",
                            ChatColor.YELLOW + "" + ChatColor.BOLD + "Click" + ChatColor.WHITE + " to edit"
                    ),
                    link.getServer() != null && link.getServer().trim().length() > 0 ?
                            Stream.of(ChatColor.YELLOW + "" + ChatColor.BOLD + "Right click" + ChatColor.WHITE + " to remove") :
                            Stream.empty()
            ).collect(Collectors.toList()));
            stack.setItemMeta(itemMeta);
            this.inventory.setItem(3, stack);
        }

        {
            if (link.getLinkType() == null) {
                link.setLinkType(LinkType.ABSOLUTE);
            }

            ItemStack stack = new ItemStack(Material.ENDER_PEARL);
            ItemMeta itemMeta = stack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Link Type");
            itemMeta.setLore(Arrays.asList(
                    ChatColor.WHITE + StringUtils.capitalize(link.getLinkType().name()),
                    "",
                    ChatColor.YELLOW + "" + ChatColor.BOLD + "Click" + ChatColor.WHITE + " to cycle"
            ));
            stack.setItemMeta(itemMeta);
            this.inventory.setItem(4, stack);
        }

        {
            ItemStack stack = new ItemStack(Material.EMPTY_MAP);
            ItemMeta itemMeta = stack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Link Location");
            itemMeta.setLore(Stream.concat(
                    Stream.concat(
                            link.getLocation() != null ?
                                    Stream.of(
                                            ChatColor.GRAY + "World: " + ChatColor.WHITE + link.getLocation().getWorld(),
                                            ChatColor.GRAY + "X: " + ChatColor.WHITE + link.getLocation().getX(),
                                            ChatColor.GRAY + "Y: " + ChatColor.WHITE + link.getLocation().getY(),
                                            ChatColor.GRAY + "Z: " + ChatColor.WHITE + link.getLocation().getZ(),
                                            ChatColor.GRAY + "Yaw: " + ChatColor.WHITE + link.getLocation().getYaw(),
                                            ChatColor.GRAY + "Pitch: " + ChatColor.WHITE + link.getLocation().getPitch()
                                    ) :
                                    Stream.of(ChatColor.RED + "No location set!"),
                            Stream.of(
                                    "",
                                    ChatColor.YELLOW + "" + ChatColor.BOLD + "Click" + ChatColor.WHITE + " to edit"
                            )),
                    link.getLocation() != null ?
                            Stream.of(ChatColor.YELLOW + "" + ChatColor.BOLD + "Right click" + ChatColor.WHITE + " to remove") :
                            Stream.empty()
            ).collect(Collectors.toList()));
            stack.setItemMeta(itemMeta);
            this.inventory.setItem(5, stack);
        }

        {
            ItemStack stack = new ItemStack(Material.COMPASS);
            ItemMeta itemMeta = stack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Link Particle Angle");
            itemMeta.setLore(Stream.concat(
                    Stream.of(
                            link.getParticleAngle() > -1 ?
                                    ChatColor.WHITE + "" + link.getParticleAngle() :
                                    ChatColor.RED + "No angle set!",
                            "",
                            ChatColor.YELLOW + "" + ChatColor.BOLD + "Click" + ChatColor.WHITE + " to edit"
                    ),
                    link.getParticleAngle() > -1 ?
                            Stream.of(ChatColor.YELLOW + "" + ChatColor.BOLD + "Right click" + ChatColor.WHITE + " to remove") :
                            Stream.empty()
            ).collect(Collectors.toList()));
            stack.setItemMeta(itemMeta);
            this.inventory.setItem(7, stack);
        }

        {
            ItemStack stack = new ItemStack(Material.EMERALD_BLOCK);
            ItemMeta itemMeta = stack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Save Link");
            itemMeta.setLore(canSave() ? Arrays.asList(
                    "",
                    ChatColor.YELLOW + "" + ChatColor.BOLD + "Click" + ChatColor.WHITE + " to save"
            ) : Stream.of(
                    "Cannot save until all fields", "have been filed in!"
            ).map(line -> ChatColor.RED + line).collect(Collectors.toList()));
            stack.setItemMeta(itemMeta);
            this.inventory.setItem(8, stack);
        }

    }

    @SuppressWarnings("RedundantIfStatement")
    private boolean canSave() {
        if (link.getName() == null || link.getName().trim().length() <= 0) return false;
        if (link.getServer() == null || link.getServer().trim().length() <= 0) return false;
        if (link.getLocation() == null) return false;
        if (link.getParticleAngle() <= -1) return false;
        return true;
    }
}
