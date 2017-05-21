package me.theminecoder.minecraft.worldlinks.link;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

public class LinkLocation {

    private String world;
    private double x;
    private double y;
    private double z;

    public LinkLocation(String world, double x, double y, double z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public LinkLocation(ConfigurationSection config) throws InvalidConfigurationException {
        if (!config.contains("x") || !config.contains("y") || !config.contains("z")) {
            throw new InvalidConfigurationException("Could not read invalid location data.");
        }

        this.world = config.getString("world", "world");
        this.x = config.getDouble("x");
        this.y = config.getDouble("y");
        this.z = config.getDouble("z");
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
        return new Location(Bukkit.getWorld(getWorld()), getX(), getY(), getZ());
    }

}
