package me.theminecoder.minecraft.worldlinks.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jackson
 * @version 1.0
 */
public abstract class PagedGUI extends GUI {

    private int page;
    private int headerRows;
    private final int internalSize;

    public PagedGUI(String title, int size) {
        super(title, size);
        this.internalSize = getInvSizeForCount(size - 9);
    }

    protected abstract List<ItemStack> getIcons();

    protected abstract void onPlayerClickIcon(InventoryClickEvent event);

    protected abstract void populateSpecial();

    public void setHeaderRows(int headerRows) {
        this.headerRows = headerRows;
    }

    @Override
    protected final void onPlayerClick(InventoryClickEvent event) {
        List<ItemStack> items = getIcons();
        if (items == null) {
            items = new ArrayList<ItemStack>();
        }
        int pageSize = internalSize - (headerRows * 9);
        int pages = (items.size() / pageSize);
        if (items.size() % pageSize > 0) pages++;

        if (event.getRawSlot() == internalSize + 3 && page > 0) {
            page--;
            this.repopulate();
            return;
        }

        if (event.getRawSlot() == internalSize + 5 && (page + 1 < pages)) {
            page++;
            this.repopulate();
            return;
        }

        this.onPlayerClickIcon(event);
    }

    @Override
    protected final void populate() {
        List<ItemStack> items = getIcons();
        if (items == null) {
            items = new ArrayList<ItemStack>();
        }
        int pageSize = internalSize - (headerRows * 9);
        int pages = (items.size() / pageSize);
        if (items.size() % pageSize > 0) pages++;

        if (page > pages) {
            this.page--;
            this.repopulate();
            return;
        }

        int slot = headerRows * 9;
        for (int i = (page * pageSize); i < items.size(); i++) {
            if (slot > pageSize + (headerRows * 9) - 1) break;
            this.inventory.setItem(slot++, items.get(i));
        }

        if (page > 0) {
            ItemStack arrow = new ItemStack(Material.ARROW);
            ItemMeta arrowMeta = arrow.getItemMeta();
            arrowMeta.setDisplayName(ChatColor.WHITE + "<- Previous Page");
            arrow.setItemMeta(arrowMeta);
            this.inventory.setItem(internalSize + 3, arrow);
        }

        if (page + 1 < pages) {
            ItemStack arrow = new ItemStack(Material.ARROW);
            ItemMeta arrowMeta = arrow.getItemMeta();
            arrowMeta.setDisplayName(ChatColor.WHITE + "Next Page ->");
            arrow.setItemMeta(arrowMeta);
            this.inventory.setItem(internalSize + 5, arrow);
        }

        ItemStack pageNumber = new ItemStack(Material.EMPTY_MAP);
        ItemMeta pageNumberMeta = pageNumber.getItemMeta();
        pageNumberMeta.setDisplayName(ChatColor.WHITE + "Page " + (page + 1) + "/" + pages);
        pageNumber.setItemMeta(pageNumberMeta);
        this.inventory.setItem(internalSize + 4, pageNumber);

        this.populateSpecial();
    }
}
