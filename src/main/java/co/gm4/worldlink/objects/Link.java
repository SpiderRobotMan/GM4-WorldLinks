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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by MatrixTunnel on 9/19/2017.
 */
@NoArgsConstructor @Getter @Setter
public class Link {

    private String name;
    private String unlockAdvancementKey;

    private List<String> displayAdvancements;
    private String displayLocation;
    private String offhandItem;

    private boolean zoomOnClick, blindnessOnClick, portalSoundOnClick;

    private String displayType;
    private double displayOffsetX, displayOffsetY, displayOffsetZ, displaySpeed;
    private int displayCount;

    private String hoverType;
    private double hoverOffsetX, hoverOffsetY, hoverOffsetZ;
    private int hoverCount, hoverSpeed;

    private LinkLocation targetLocation;
    private String teleportType;
    private boolean resetRespawnLocation;

    private List<String> beforeCommands, duringCommands, afterCommands;

}
