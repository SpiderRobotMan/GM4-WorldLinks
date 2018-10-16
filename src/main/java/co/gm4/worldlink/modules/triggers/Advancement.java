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

package co.gm4.worldlink.modules.triggers;

import co.gm4.worldlink.WorldLink;
import co.gm4.worldlink.modules.Module;
import co.gm4.worldlink.objects.LinkPlayer;
import co.gm4.worldlink.objects.LinkWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

/**
 * Created by MatrixTunnel on 9/19/2017.
 */
public class Advancement extends Module implements Listener {

    @EventHandler
    public void onUnlock(PlayerAdvancementDoneEvent event) {
        WorldLink.get().getPluginConfig().getLinks().forEach(link -> {
            if ((event.getAdvancement().getKey().getNamespace() + ":"+ event.getAdvancement().getKey().getKey()).equals(link.getUnlockAdvancementKey())) {
                LinkPlayer linkPlayer = WorldLink.get().getPlayerManager().getLinkPlayer(event.getPlayer().getUniqueId());

                if (linkPlayer.getWorlds().stream().noneMatch(linkWorld -> linkWorld.getName().equals(link.getName()))) linkPlayer.getWorlds().add(new LinkWorld(link.getName()));
            }
        });
    }

}
