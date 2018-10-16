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

package co.gm4.worldlink.managers;

import co.gm4.worldlink.objects.LinkPlayer;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by MatrixTunnel on 9/15/2017.
 */
public class PlayerManager {

    @Getter private Collection<LinkPlayer> players = new ConcurrentLinkedQueue<>();

    public void addPlayer(LinkPlayer linkPlayer) {
        this.players.add(linkPlayer);
    }

    public void removePlayer(LinkPlayer linkPlayer) {
        this.players.remove(linkPlayer);
    }

    public LinkPlayer getLinkPlayer(Player player) {
        return getLinkPlayer(player.getUniqueId());
    }

    public LinkPlayer getLinkPlayer(UUID uuid) {
        for (LinkPlayer linkPlayer : players) {
            if (linkPlayer.getUuid().toString().equals(uuid.toString())) {
                return linkPlayer;
            }
        }
        return null;
    }

}
