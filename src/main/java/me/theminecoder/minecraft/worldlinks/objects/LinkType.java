package me.theminecoder.minecraft.worldlinks.objects;

import org.bukkit.Location;

public enum LinkType {

    ABSOLUTE {
        @Override
        public Location getFixedLocation(Location location) {
            return location;
        }
    };

    public abstract Location getFixedLocation(Location location);

    /**
     * Gets a link type by its name from the config.
     *
     * @param name The name
     * @return The link type, or null if not found
     */
    public static LinkType getByConfigName(String name) {
        for (LinkType type : values()) {
            if (type.toString().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

}
