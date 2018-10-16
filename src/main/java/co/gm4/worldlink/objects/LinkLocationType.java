/**
 * WorldLink - Multi-Dimensional Survival Server
 * Copyright (C) 2017, 18  Gamemode 4
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
