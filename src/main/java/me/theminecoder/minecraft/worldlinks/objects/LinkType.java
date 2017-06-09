package me.theminecoder.minecraft.worldlinks.objects;

import me.theminecoder.minecraft.worldlinks.utils.LocationUtils;
import org.bukkit.Location;

public enum LinkType {

    ABSOLUTE {
        @Override
        public Location getFixedLocation(Location oldLocation, Location newLocation) {
            return newLocation;
        }
    },
    ABSOLUTE_SAFE {
        @Override
        public Location getFixedLocation(Location oldLocation, Location newLocation) {
            return LocationUtils.getSafeLocation(newLocation, 5, 3);
        }
    },
    RELATIVE {
        @Override
        public Location getFixedLocation(Location oldLocation, Location newLocation) {
            return newLocation.add(oldLocation.getX(), oldLocation.getY(), oldLocation.getZ());
        }
    },
    RELATIVE_SAFE {
        @Override
        public Location getFixedLocation(Location oldLocation, Location newLocation) {
            return LocationUtils.getSafeLocation(newLocation.add(oldLocation.getX(), oldLocation.getY(), oldLocation.getZ()), 30, 7);
        }
    },
    LOCAL {
        @Override
        public Location getFixedLocation(Location oldLocation, Location newLocation) {
            return new Location(newLocation.getWorld(), oldLocation.getX(), oldLocation.getY(), oldLocation.getZ(), oldLocation.getYaw(), oldLocation.getPitch());
        }
    },
    LOCAL_SAFE {
        @Override
        public Location getFixedLocation(Location oldLocation, Location newLocation) {
            return LocationUtils.getSafeLocation(newLocation, 5, 3);
        }
    };

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

    public abstract Location getFixedLocation(Location oldLocation, Location newLocation);

}
