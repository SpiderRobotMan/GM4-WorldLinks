package co.gm4.worldlink.utils;

import co.gm4.worldlink.objects.LinkLocation;
import com.google.common.collect.ImmutableList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by MatrixTunnel on 9/13/2017.
 *
 * @author theminecoder
 */
public class LocationUtils {

    /**
     * Auto generated list of unsafe block to spawn on.
     */
    public static final ImmutableList<Material> UNSAFE_BLOCKS = ImmutableList.copyOf(Arrays.stream(Material.values()).filter(material -> {
        String name = material.name();
        return (material.isTransparent() && !name.contains("LEAVES")) || name.endsWith("LAVA") || name.endsWith("WATER");
    }).collect(Collectors.toList()));

    public static final List<Material> UNBREAKABLE_BLOCKS = Arrays.asList(Material.BEDROCK, Material.COMMAND, Material.COMMAND_CHAIN,
            Material.COMMAND_REPEATING, Material.BARRIER, Material.END_GATEWAY, Material.ENDER_PORTAL_FRAME, Material.ENDER_PORTAL, Material.PORTAL);

    private static boolean isSafe(Location location) {
        return UNSAFE_BLOCKS.contains(location.getBlock().getType()) && UNSAFE_BLOCKS.contains(location.getBlock().getRelative(BlockFace.UP).getType()) && !UNSAFE_BLOCKS.contains(location.getBlock().getRelative(BlockFace.DOWN).getType());

        //if (UNSAFE_BLOCKS.contains(location.getBlock().getRelative(BlockFace.DOWN).getType()) || location.getBlock().getRelative(BlockFace.DOWN).getType() == Material.BEDROCK) {
        //    return false;
        //}

        //if (location.getBlock().getType().isSolid() || !location.getBlock().getType().isTransparent() || location.getBlock().getType().name().endsWith("LAVA")) {

        //}

        //if (
        //        location.getBlock().getRelative(BlockFace.UP).getType().isSolid() ||
        //                !location.getBlock().getRelative(BlockFace.UP).getType().isTransparent() ||
        //                location.getBlock().getRelative(BlockFace.UP).getType().name().endsWith("LAVA") ||
        //                location.getBlock().getType().name().endsWith("WATER")
        //        ) {

        //}


        //return !(UNSAFE_BLOCKS.contains(location.getBlock().getRelative(BlockFace.DOWN).getType()) || location.getBlock().getRelative(BlockFace.DOWN).getType() == Material.BEDROCK) && !(location.getBlock().getType().isSolid() || !location.getBlock().getType().isTransparent() || location.getBlock().getType().name().endsWith("LAVA")) && !(location.getBlock().getRelative(BlockFace.UP).getType().isSolid() || !location.getBlock().getRelative(BlockFace.UP).getType().isTransparent() || location.getBlock().getRelative(BlockFace.UP).getType().name().endsWith("LAVA") || location.getBlock().getType().name().endsWith("WATER"));
    }

    /**
     * Search for a safe location in radius increments to ensure we are as close to the original location as possible.
     *
     * @param location
     * @param maxRadius
     * @param maxYRadius
     * @return
     */
    public static Location getSafeLocation(Location location, int maxRadius, int maxYRadius) {
        location = new Location(location.getWorld(), Math.floor(location.getX()) + 0.5D, Math.floor(location.getY()) + 0.5D, Math.floor(location.getZ()) + 0.5D, location.getYaw(), location.getPitch());
        Block headBlock = location.getBlock().getRelative(BlockFace.UP);
        Block feetBlock = location.getBlock();
        Block belowFeetBlock = location.getBlock().getRelative(BlockFace.DOWN);

        if (isSafe(location)) return location;

        List<Location> safeLocations = new ArrayList<>();
        Location closest = null;

        // Search for nearest safe zone within radius.
        for (int x = -(maxRadius); x <= maxRadius; x++) {
            for (int z = -(maxRadius); z <= maxRadius; z++) {
                for (int y = -(maxYRadius); y <= maxYRadius; y++) {
                    Location search = location.getBlock().getRelative(x, y, z).getLocation();
                    if (isSafe(search)) {
                        search.setYaw(location.getYaw());
                        search.setPitch(location.getPitch());
                        safeLocations.add(search.add(0.5, 0.5, 0.5));
                    }
                }
            }
        }

        // Get the closest safe location
        if (!safeLocations.isEmpty()) {
            for (Location safeLocation : safeLocations) {
                if (closest == null) {
                    closest = safeLocation;
                    continue;
                }
                if (closest.distance(location) > safeLocation.distance(location)) {
                    closest = safeLocation;
                }
            }
            return closest;
        }

        // Search for nearest ground below
        for (int y = -1; y >= -(location.getBlockY()); y--) {
            Location search = location.getBlock().getRelative(0, y, 0).getLocation();

            if (isSafe(search)) {
                search.setYaw(location.getYaw());
                search.setPitch(location.getPitch());
                return search.add(0.5, 0.5, 0.5);
            }
        }

        // Search for nearest ground above
        for (int y = 1; y <= 255 - location.getBlockY(); y++) {
            Location search = location.getBlock().getRelative(0, y, 0).getLocation();

            if (isSafe(search)) {
                search.setYaw(location.getYaw());
                search.setPitch(location.getPitch());
                return search.add(0.5, 0.5, 0.5);
            }
        }

        // Make safe space
        if (UNBREAKABLE_BLOCKS.contains(headBlock.getType())) headBlock.setType(Material.AIR);
        else headBlock.breakNaturally();

        if (UNBREAKABLE_BLOCKS.contains(feetBlock.getType())) feetBlock.setType(Material.AIR);
        else feetBlock.breakNaturally();

        if (UNSAFE_BLOCKS.contains(belowFeetBlock.getType())) belowFeetBlock.setType(Material.DIRT);

        return location;
    }

    public static String locationToString(LinkLocation location) {
        Map<String, Object> serialized = location.serialize();
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (String key : serialized.keySet()) {
            builder.append(key + "=" + serialized.get(key));
            if (i < serialized.size() - 1) {
                builder.append(";");
            }
            i++;
        }
        return builder.toString();
    }

    public static String locationToString(Location location) {
        return locationToString(new LinkLocation(location));
    }

    public static LinkLocation stringToLocation(String loc) {
        System.out.println(loc);
        Map<String, Object> serialized = new HashMap<>();
        for (String param : loc.split(";")) {
            String key = param.split("=")[0];
            Object value;
            if (key.equalsIgnoreCase("x") || key.equalsIgnoreCase("y") || key.equalsIgnoreCase("z")) {
                value = Double.valueOf(param.split("=")[1]);
            } else if (key.equalsIgnoreCase("yaw") || key.equalsIgnoreCase("pitch")) {
                value = Float.valueOf(param.split("=")[1]);
            } else {
                value = param.split("=")[1];
            }
            serialized.put(key, value);
        }
        return LinkLocation.deserialize(serialized);
    }

}
