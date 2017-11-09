package co.gm4.worldlink.listeners;

import co.gm4.worldlink.WorldLink;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.material.Bed;

/**
 * Created by MatrixTunnel on 11/5/2017.
 */
public class BlockListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.isCancelled() && event.getBlock().getType() == Material.BED_BLOCK) {
            WorldLink.get().getDatabaseHandler().removeRespawnLocations(getBedHeadBlock(event.getBlock()).getLocation());
        }
    }

    private Block getBedHeadBlock(Block block) {
        if (block.getType() != Material.BED_BLOCK) return block;
        Bed bed = (Bed) block.getState().getData();
        if (bed.isHeadOfBed()) return block;
        return block.getRelative(bed.getFacing());
    }

}
