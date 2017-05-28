package me.theminecoder.minecraft.worldlinks.gui;

import me.theminecoder.minecraft.worldlinks.objects.Link;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author theminecoder
 */
public class LinkConditionsGUI extends PagedGUI {

    private Link link;

    public LinkConditionsGUI() {
        super("Edit Link Conditions", 56);
    }

    @Override
    protected List<ItemStack> getIcons() {
        return link.getConditions().stream().map(linkCondition -> {
            ItemStack stack = new ItemStack(Material.PAPER);
            ItemMeta stackMeta = stack.getItemMeta();
            stackMeta.setDisplayName(ChatColor.WHITE + "" + linkCondition);
            stack.setItemMeta(stackMeta);
            return stack;
        }).collect(Collectors.toList());
    }

    @Override
    protected void onPlayerClickIcon(InventoryClickEvent event) {

    }

    @Override
    protected void populateSpecial() {

    }
}
