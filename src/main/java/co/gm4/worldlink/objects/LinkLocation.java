package co.gm4.worldlink.objects;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MatrixTunnel on 9/20/2017.
 */
@Getter @Setter
public class LinkLocation implements ConfigurationSerializable {

    private String world;
    private double x, y, z;
    private float yaw, pitch;

    boolean ignoreYaw, ignorePitch;

    public LinkLocation(String world, double x, double y, double z, float yaw, float pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public LinkLocation(String world, double x, double y, double z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public LinkLocation(World world, double x, double y, double z, float yaw, float pitch) {
        this.world = world.getName();
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public LinkLocation(World world, double x, double y, double z) {
        this.world = world.getName();
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public LinkLocation(Location location) {
        if (location.getWorld() == null) {
            location.setWorld(Bukkit.getWorlds().get(0));
        }
        this.world = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public Location getLocation() {
        World world = Bukkit.getWorld(this.world);
        if (world == null) world = Bukkit.getWorlds().get(0);
        return new Location(world, x, y, z, yaw, pitch);
    }

    public LinkLocation add(double x, double y, double z) {
        LinkLocation linkLocation = new LinkLocation(world, this.x + x, this.y + y, this.z + z, yaw, pitch);
        linkLocation.setIgnoreYaw(this.ignoreYaw);
        linkLocation.setIgnorePitch(this.ignorePitch);
        return linkLocation;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("world", world);
        map.put("x", x);
        map.put("y", y);
        map.put("z", z);
        map.put("yaw", yaw);
        map.put("pitch", pitch);
        return map;
    }

    public static LinkLocation deserialize(Map<String, Object> map) {
        return new LinkLocation((String) map.get("world"), (Double) map.get("x"), (Double) map.get("y"), (Double) map.get("z"), (Float) map.get("yaw"), (Float) map.get("pitch"));
    }

    public Location getAsLocation() {
        World world = Bukkit.getWorld(getWorld());
        if (world == null) return null;
        return new Location(world, x, y, z, yaw, pitch);
    }

}
