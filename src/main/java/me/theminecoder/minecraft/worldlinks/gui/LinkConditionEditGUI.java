package me.theminecoder.minecraft.worldlinks.gui;

import me.theminecoder.minecraft.worldlinks.objects.LinkCondition;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author theminecoder
 */
public class LinkConditionEditGUI extends PagedGUI {

    private LinkCondition linkCondition;
    private boolean guiOpening;

    public LinkConditionEditGUI(LinkCondition linkCondition) {
        super("Edit Link Condition", 56);
        this.setHeaderRows(1);
        this.linkCondition = linkCondition;
    }

    @Override
    protected List<ItemStack> getIcons() {
        return IntStream.range(0, 20).mapToObj(condition -> {
            return new ItemStack(Material.REDSTONE);
        }).collect(Collectors.toList());
    }

    @Override
    protected void onPlayerClickIcon(InventoryClickEvent event) {

    }

    @Override
    protected void onPlayerCloseInv(Player player) {
        if (!guiOpening) {
            this.scheduleOpen(new LinkEditGUI(linkCondition.getLink()), player);
        }
    }

    @Override
    protected void populateSpecial() {

    }
}
