package me.theminecoder.minecraft.worldlinks.gui;

import me.theminecoder.minecraft.worldlinks.objects.Link;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * @author theminecoder
 */
public class LinkEditGUI extends GUI {

    private Link link;

    public LinkEditGUI(Link link) {
        super("Edit Link", 56);
        this.link = link;
    }

    @Override
    protected void onPlayerClick(InventoryClickEvent event) {

    }

    @Override
    protected void populate() {

    }
}
