package co.gm4.worldlink.utils;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

/**
 * Created by MatrixTunnel on 9/13/2017.
 *
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

    private static boolean isSafe(Location location) {
        return !(UNSAFE_BLOCKS.contains(location.getBlock().getRelative(BlockFace.DOWN).getType()) || location.getBlock().getRelative(BlockFace.DOWN).getType() == Material.BEDROCK) && !(location.getBlock().getType().isSolid() || !location.getBlock().getType().isTransparent() || location.getBlock().getType().name().endsWith("LAVA")) && !(location.getBlock().getRelative(BlockFace.UP).getType().isSolid() || !location.getBlock().getRelative(BlockFace.UP).getType().isTransparent() || location.getBlock().getRelative(BlockFace.UP).getType().name().endsWith("LAVA") || location.getBlock().getType().name().endsWith("WATER"));

    }

    /**
     * Search for a safe location in radius increments to ensure we are as close to the original location as possible.
     * @param location
     * @param maxRadius
     * @param maxYRadius
     * @return
     */
    public static Location getSafeLocation(Location location, int maxRadius, int maxYRadius) {
        if(isSafe(location)) return location;

        // Search for nearest safe zone within radius.
        for(int x = -(maxRadius); x <= maxRadius; x++) {
            for(int z = -(maxRadius); z <= maxRadius; z++) {
                for(int y = -(maxYRadius); y <= maxYRadius; y++) {
                    Location search = location.getBlock().getRelative(x, y, z).getLocation();

                    if(isSafe(search)) return search;
                }
            }
        }

        // Search for nearest ground below
        for(int y = -1; y >= -(location.getBlockY()); y--) {
            Location search = location.getBlock().getRelative(0, y, 0).getLocation();

            if(isSafe(search)) return search;
        }

        // Search for nearest ground above
        for(int y = 1; y <= 255 - location.getBlockY(); y++) {
            Location search = location.getBlock().getRelative(0, y, 0).getLocation();

            if(isSafe(search)) return search;
        }

        // Make safe space
        location.getBlock().breakNaturally();
        location.getBlock().getRelative(BlockFace.UP).breakNaturally();
        if(UNSAFE_BLOCKS.contains(location.getBlock().getRelative(BlockFace.DOWN).getType())) {
            location.getBlock().getRelative(BlockFace.DOWN).setType(Material.DIRT);
        }

        location.setX(Math.floor(location.getX()) + 0.5D);
        location.setZ(Math.floor(location.getZ()) + 0.5D);
        location.setY(Math.floor(location.getY()) + 0.5D);
        return location;

        /*for (int radius = 0; radius <= maxRadius; radius++) {
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
                            Location loc = baseBlock.getLocation().add(0.5, 1.5, 0.5);
                            loc.setYaw(location.getYaw());
                            loc.setPitch(location.getPitch());
                            return loc;
                        }
                    }
                }
            }
        }
        return location;*/
    }

}
