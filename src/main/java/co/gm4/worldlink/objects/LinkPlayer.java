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

import co.gm4.worldlink.modules.display.Filter;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by MatrixTunnel on 9/14/2017.
 */
@Getter @Setter
public class LinkPlayer {

    private UUID uuid;
    private LinkPlayerData playerData;
    private List<LinkWorld> worlds;

    private Player player;
    private boolean gettingTransferred;
    private LinkLocationType locationType;

    private String advancementsJson;
    private String statsJson;

    private long lastLoginTime;
    private Location lastDeathLocation;

    public LinkPlayer(UUID uuid, LinkPlayerData playerData, List<LinkWorld> worlds) {
        this.uuid = uuid;
        this.playerData = playerData;
        this.worlds = worlds;
    }

    public LinkPlayer(UUID uuid, LinkPlayerData playerData, List<LinkWorld> worlds, String advancementsJson, String statsJson) {
        this.uuid = uuid;
        this.playerData = playerData;
        this.worlds = worlds;
        this.advancementsJson = advancementsJson;
        this.statsJson = statsJson;
    }

    public List<LinkWorld> getFilteredWorlds() {
        List<LinkWorld> filtered = new ArrayList<>();

        if (worlds.isEmpty()) return filtered;

        worlds.stream().filter(linkWorld -> Filter.canDisplay(this, linkWorld)).forEach(filtered::add);
        return filtered;
    }

}
