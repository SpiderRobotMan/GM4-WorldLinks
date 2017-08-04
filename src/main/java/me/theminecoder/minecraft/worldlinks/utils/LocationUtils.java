package me.theminecoder.minecraft.worldlinks.utils;

import com.google.common.collect.ImmutableList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author theminecoder
 */
public class LocationUtils {

    /**
     * Auto generated list of unsafe block to spawn on/in.
     */
    public static final ImmutableList<Material> UNSAFE_BLOCKS = ImmutableList.copyOf(Arrays.stream(Material.values()).filter(material -> {
        String name = material.name();
        return (material.isTransparent() && !(name.contains("LEAVES") || material == Material.TORCH)) || name.endsWith("LAVA") || name.endsWith("WATER");
    }).collect(Collectors.toList()));

    /**
     * Search for a safe location in radius increments to ensure we are as close to the original location as possible.
     * @param location
     * @param maxRadius
     * @param maxYRadius
     * @return
     */
    public static Location getSafeLocation(Location location, int maxRadius, int maxYRadius) {
        for (int radius = 0; radius <= maxRadius; radius++) {
            for (int x = -(radius); x <= radius; x++) {
                for (int z = -(radius); z <= radius; z++) {
                    if (!(x == radius || x == -(radius) || z == radius || z == -(radius))) continue;
                    for (int yRadius = 0; yRadius <= maxYRadius; yRadius++) {
                        for (int y = -(yRadius); y <= yRadius; y++) {
                            if (!(y == yRadius || y == -(yRadius))) continue;

                            Block baseBlock = location.getBlock().getRelative(x, y, z);
                            if (UNSAFE_BLOCKS.contains(baseBlock.getRelative(BlockFace.DOWN).getType())) continue;
                            if (UNSAFE_BLOCKS.contains(baseBlock.getType())) continue;
                            if (baseBlock.getRelative(BlockFace.UP).getType() != Material.AIR) continue;
                            return baseBlock.getLocation().add(0.5, 0.5, 0.5);
                        }
                    }
                }
            }
        }
        return location;
    }

}
