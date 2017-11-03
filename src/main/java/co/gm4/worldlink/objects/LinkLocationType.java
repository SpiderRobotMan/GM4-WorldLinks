package co.gm4.worldlink.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by MatrixTunnel on 9/13/2017.
 *
 * @author theminecoder
 */
@AllArgsConstructor @Getter
public enum LinkLocationType {

    ABSOLUTE(0, 0) {
        @Override
        public LinkLocation getFixedLocation(LinkLocation oldLocation, LinkLocation newLocation) {
            return newLocation;
        }
    },
    ABSOLUTE_SAFE(5, 5) {
        @Override
        public LinkLocation getFixedLocation(LinkLocation oldLocation, LinkLocation newLocation) {
            return ABSOLUTE.getFixedLocation(oldLocation, newLocation);
        }
    },
    RELATIVE(0, 0) {
        @Override
        public LinkLocation getFixedLocation(LinkLocation oldLocation, LinkLocation newLocation) {
            return newLocation.add(oldLocation.getX(), oldLocation.getY(), oldLocation.getZ());
        }
    },
    RELATIVE_SAFE(5, 5) {
        @Override
        public LinkLocation getFixedLocation(LinkLocation oldLocation, LinkLocation newLocation) {
            return RELATIVE.getFixedLocation(oldLocation, newLocation);
        }
    };

    private int maxRadius;
    private int maxYRadius;

    /**
     * Gets a link type by its name from the config.
     *
     * @param name The name
     * @return The link type, or null if not found
     */
    public static LinkLocationType getByConfigName(String name) {
        for (LinkLocationType type : values()) {
            if (type.toString().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    public boolean isSafe() {
        return this.name().contains("SAFE");
    }

    public abstract LinkLocation getFixedLocation(LinkLocation oldLocation, LinkLocation newLocation);

}
