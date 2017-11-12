package co.gm4.worldlink.utils;

import co.gm4.worldlink.objects.LinkLocation;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Created by MatrixTunnel on 9/13/2017.
 *
 * @author MatrixTunnel & SpiderRobotMan
 */
public class LocationUtils {

    public static final List<Material> UNBREAKABLE_BLOCKS = Arrays.asList(
        Material.BEDROCK,
        Material.COMMAND,
        Material.COMMAND_CHAIN,
        Material.COMMAND_REPEATING,
        Material.BARRIER,
        Material.END_GATEWAY,
        Material.ENDER_PORTAL_FRAME,
        Material.ENDER_PORTAL,
        Material.PORTAL
    );

    public static final ImmutableList<Material> UNSAFE_WITHIN = ImmutableList.copyOf(Arrays.stream(Material.values()).filter(material -> (material.isSolid() || material.hasGravity() || material.name().endsWith("WATER") || material.name().endsWith("LAVA") || material == Material.FIRE || material == Material.CACTUS)).collect(Collectors.toList()));

    public static final ImmutableList<Material> UNSAFE_UNDER = ImmutableList.copyOf(Arrays.stream(Material.values()).filter(material -> ((material.isTransparent() && !(material.name().contains("LEAVES") || material.name().contains("ICE") || material == Material.SLIME_BLOCK)) || material.name().endsWith("WATER") || material.name().endsWith("LAVA") || material == Material.MAGMA || material == Material.CACTUS)).collect(Collectors.toList()));

    public static final ImmutableList<Material> UNSAFE_FORCE = ImmutableList.copyOf(Arrays.stream(Material.values()).filter(material -> (material.name().contains("LAVA") || material.name().contains("WATER") || material.hasGravity() || UNBREAKABLE_BLOCKS.contains(material))).collect(Collectors.toList()));

    public static boolean isLocationSafe(Location location) {
        Block under = location.getBlock().getRelative(0, -1, 0);
        Block feet = location.getBlock();
        Block head = location.getBlock().getRelative(0, 1, 0);
        Block above = location.getBlock().getRelative(0, 2, 0);
        return !UNSAFE_UNDER.contains(under.getType()) && !UNSAFE_WITHIN.contains(feet.getType()) && !UNSAFE_WITHIN.contains(head.getType()) && !UNSAFE_WITHIN.contains(above.getType());
    }

    public static boolean isLocationSafeForce(Location location) {
        if(location.getBlock().getRelative(0, -1, 0).getType() == Material.BEDROCK) return false;

        for (int x = -1; x <= 1; x++) {
            for(int z = -1; z <= 1; z++) {
                for(int y = 0; y <= 2; y++) {
                    Block check = location.getBlock().getRelative(x, y, z);

                    if(UNSAFE_FORCE.contains(check.getType())) return false;
                }
            }
        }

        return true;
    }

    public static Location getClosestLocation(List<Location> list, Location starting) {
        // Get the closest safe location
        Location closest = null;

        for (Location location : list) {
            if (closest == null) {
                closest = location;
                continue;
            }
            if (closest.distance(starting) > location.distance(starting)) closest = location;
        }
        return closest;
    }

    public static Location getSafeLocation(Location starting, int maxRadius, int maxYRadius) {
        starting.setX(Math.floor(starting.getX()) + 0.5D);
        starting.setY(Math.floor(starting.getY()) + 0.0D);
        starting.setZ(Math.floor(starting.getZ()) + 0.5D);
        if(isLocationSafe(starting)) return starting;

        List<Location> safeLocations = new ArrayList<>();
        List<Location> safeForce = new ArrayList<>();


        // Search for nearest safe zone within radius.
        for (int x = -(maxRadius); x <= maxRadius; x++) {
            for (int z = -(maxRadius); z <= maxRadius; z++) {
                for (int y = -(maxYRadius); y <= maxYRadius; y++) {
                    Location search = new Location(starting.getWorld(), starting.getX() + x, starting.getY() + y, starting.getZ() + z);
                    if (isLocationSafe(search)) {
                        search.setYaw(starting.getYaw());
                        search.setPitch(starting.getPitch());
                        safeLocations.add(search);
                    }
                    if(isLocationSafeForce(search)) {
                        safeForce.add(search);
                    }
                }
            }
        }

        // Get the closest safe location
        if (!safeLocations.isEmpty()) {
            return getClosestLocation(safeLocations, starting);
        }

        safeLocations.clear();

        // Search for nearest vertical ground
        for (int y = 0; y <= 253; y++) {
            Location search = new Location(starting.getWorld(), starting.getX(), y, starting.getZ(), starting.getYaw(), starting.getPitch());

            if (isLocationSafe(search)) {
                safeLocations.add(search);
            }

            if(isLocationSafeForce(search)) {
                safeForce.add(search);
            }
        }

        // Get the closest safe location
        if (!safeLocations.isEmpty()) {
            return getClosestLocation(safeLocations, starting);
        }

        // Make safe space
        if(!safeForce.isEmpty()) {
            starting = getClosestLocation(safeForce, starting);
        }

        Block under = starting.getBlock().getRelative(0, -1, 0);
        Block feet = starting.getBlock();
        Block head = starting.getBlock().getRelative(0, 1, 0);

        if(UNSAFE_UNDER.contains(under.getType())) under.setType(Material.DIRT);

        if(UNSAFE_WITHIN.contains(feet.getType())) feet.breakNaturally();

        if(UNSAFE_WITHIN.contains(head.getType())) head.breakNaturally();

        return starting;
    }

    public static String locationToString(LinkLocation location) {
        Map<String, Object> serialized = location.serialize();
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (String key : serialized.keySet()) {
            builder.append(key).append("=").append(serialized.get(key));
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
        if (loc == null) return null;
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
