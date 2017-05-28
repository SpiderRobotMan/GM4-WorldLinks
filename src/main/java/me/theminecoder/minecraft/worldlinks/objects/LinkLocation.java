package me.theminecoder.minecraft.worldlinks.objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LinkLocation {

    private String world;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    protected LinkLocation() {
    }

    public LinkLocation(Location location) {
        this(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public LinkLocation(String world, double x, double y, double z) {
        this(world, x, y, z, 0, 0);
    }

    public LinkLocation(String world, double x, double y, double z, float yaw, float pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getWorld() {
        return world;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Location getBukkitLocation() {
        return this.getBukkitLocation(Bukkit.getWorld(getWorld()));
    }

    public Location getBukkitLocation(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }

}
