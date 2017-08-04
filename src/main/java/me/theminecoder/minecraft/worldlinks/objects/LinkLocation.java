package me.theminecoder.minecraft.worldlinks.objects;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.Serializable;
import java.sql.SQLException;

public class LinkLocation {

    private String world;
    private double x;
    private double y;
    private double z;
    private float yaw = 0;
    private float pitch = 0;

    public LinkLocation(Location location) {
        this(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public LinkLocation() {
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

    public void setWorld(String world) {
        this.world = world;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public Location getBukkitLocation() {
        return this.getBukkitLocation(Bukkit.getWorld(getWorld()));
    }

    public Location getBukkitLocation(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static class Persister extends StringType {
        private static final Persister instance = new Persister();

        public static Persister getInstance() {
            return instance;
        }

        private Persister() {
            super(SqlType.STRING, new Class[]{LinkLocation.class});
        }

        @Override
        public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
            LinkLocation loc = (LinkLocation) javaObject;
            return loc.world + ":" + loc.x + ":" + loc.y + ":" + loc.z + ":" + loc.yaw + ":" + loc.pitch;
        }

        @Override
        public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
            String[] split = ((String) sqlArg).split(":");
            return new LinkLocation(split[0], Double.valueOf(split[1]), Double.valueOf(split[2]), Double.valueOf(split[3]), Float.valueOf(4), Float.valueOf(5));
        }
    }

}
