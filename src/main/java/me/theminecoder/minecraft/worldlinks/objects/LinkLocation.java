package me.theminecoder.minecraft.worldlinks.objects;

import com.j256.ormlite.field.DataPersister;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.field.types.StringType;
import com.j256.ormlite.support.DatabaseResults;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.SQLException;

public class LinkLocation {

    private String world;
    private double x;
    private double y;
    private double z;

    protected LinkLocation(){
    }

    public LinkLocation(Location location) {
        this(location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
    }

    public LinkLocation(String world, double x, double y, double z) {
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
        return new Location(Bukkit.getWorld(getWorld()), getX(), getY(), getZ());
    }

}
