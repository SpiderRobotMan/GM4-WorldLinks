package me.theminecoder.minecraft.worldlinks.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ParticleUtils {

    /**
     * Displays a particle to the player at the specified location using
     * packets.
     *
     * @param player The player
     * @param name   The particle name
     * @param loc    The location to display it
     * @param speed  The speed of the particle
     * @param amount The amount to display
     */
    public static void sendParticle(Player player, String name, Location loc, float speed, int amount) {
        ReflectionUtils.sendPacket(player, ReflectionUtils.constructPacket("PacketPlayOutWorldParticles",
                getParticle(name), true, (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), 0F, 0F, 0F, speed, amount, new int[0]));
    }

    /**
     * Displays a particle to the player at the specified location using
     * packets.
     *
     * @param player The player
     * @param name   The particle name
     * @param loc    The location to display it
     * @param xd     First float data
     * @param yz     Second float data
     * @param zd     Third float data
     * @param speed  The speed of the particle
     * @param amount The amount to display
     */
    public static void sendParticle(Player player, String name, Location loc, float xd, float yz, float zd, float speed, int amount) {
        ReflectionUtils.sendPacket(player, ReflectionUtils.constructPacket("PacketPlayOutWorldParticles",
                getParticle(name), true, (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), xd, yz, zd, speed, amount, new int[0]));
    }

    /**
     * Gets an EnumParticle by its name using reflection.
     *
     * @param name The name
     * @return An object, or null if does not exist
     */
    public static Object getParticle(String name) {
        try {
            return ReflectionUtils.getNMSClass("EnumParticle").getMethod("valueOf", String.class).invoke(null, name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Calculates the position to display the particle relative to a fixed
     * location.
     *
     * @param loc The location
     * @return The location
     */
    public static Location calculatePositionRelativeTo(Location loc, Location pLoc) {
        double x = pLoc.getBlockX() + 0.5D;
        double y = pLoc.getBlockY() + 0.5D;
        double z = pLoc.getBlockZ() + 0.5D;

        //Add the location to the player's existing location.
        return loc.getBlock().getLocation().clone().add(x, y, z);
    }

}
