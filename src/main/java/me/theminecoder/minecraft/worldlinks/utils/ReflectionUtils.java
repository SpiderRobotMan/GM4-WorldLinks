package me.theminecoder.minecraft.worldlinks.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtils {

    private static String VERSION = null;
    private static String NMS_PATH = null;

    /**
     * Gets the version of minecraft running on this server.
     *
     * @return The version
     */
    public static String getVersion() {
        if (VERSION == null) {
            VERSION = Bukkit.getServer().getClass().getPackage().getName().substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf(".") + 1);
        }
        return VERSION; //Cache this for efficiency, never changes.
    }

    /**
     * Gets the NMS path running on this server.
     *
     * @return The NMS path
     */
    public static String getNMSPath() {
        if (NMS_PATH == null) {
            NMS_PATH = "net.minecraft.server." + getVersion() + ".";
        }
        return NMS_PATH; //Cache this for efficiency, never changes.
    }

    /**
     * Gets an NMS class by its name.
     *
     * @param name The name
     * @return The class or null if not found
     */
    public static Class<?> getNMSClass(String name) {
        try {
            return Class.forName(getNMSPath() + name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Constructs a packet using the name and and the parameters to send
     * along with it.
     *
     * @param name   The name
     * @param params List of parameters
     * @return The constructed packet or null if it failed
     */
    public static Object constructPacket(String name, Object... params) {
        try {
            Class<?>[] classes = new Class<?>[params.length];

            //Convert each argument to primitive.
            for (int i = 0; i < params.length; i++) {
                classes[i] = params[i].getClass();

                if (params[i].getClass() == Boolean.class) classes[i] = boolean.class;
                if (params[i].getClass() == Integer.class) classes[i] = int.class;
                if (params[i].getClass() == Double.class) classes[i] = double.class;
                if (params[i].getClass() == Float.class) classes[i] = float.class;
                if (params[i].getClass() == Long.class) classes[i] = long.class;
                if (params[i].getClass() == Short.class) classes[i] = short.class;
                if (params[i].getClass() == Byte.class) classes[i] = byte.class;
                if (params[i].getClass() == Void.class) classes[i] = void.class;
                if (params[i].getClass() == Character.class) classes[i] = char.class;
            }

            Constructor<?> con = getNMSClass(name).getDeclaredConstructor(classes);
            con.setAccessible(true);

            return con.newInstance(params);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets a player's NMS handle.
     *
     * @param player The player
     * @return The handle or null if error
     */
    public static Object getHandle(Player player) {
        try {
            Method m = player.getClass().getMethod("getHandle");
            m.setAccessible(true);

            return m.invoke(player);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets a player's NMS connection.
     *
     * @param handle The player's handle
     * @return The connection or null if error
     */
    public static Object getConnection(Object handle) {
        try {
            Field f = handle.getClass().getDeclaredField("playerConnection");
            f.setAccessible(true);

            return f.get(handle);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sends a constructed packet to the player.
     *
     * @param player The player
     * @param packet The packet
     */
    public static void sendPacket(Player player, Object packet) {
        Object pc = getConnection(getHandle(player));

        try {
            pc.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(pc, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
