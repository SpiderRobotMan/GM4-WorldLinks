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

package co.gm4.worldlink.commands;

import co.gm4.worldlink.WorldLink;
import co.gm4.worldlink.objects.LinkLocation;
import co.gm4.worldlink.objects.LinkLocationType;
import co.gm4.worldlink.objects.LinkPlayer;
import co.gm4.worldlink.objects.LinkWorld;
import co.gm4.worldlink.utils.ServerUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by MatrixTunnel on 10/12/2017.
 */
public class WorldCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = ((Player) sender).getPlayer();
        LinkPlayer linkPlayer = WorldLink.get().getPlayerManager().getLinkPlayer(player.getUniqueId());

        if (args.length > 0) {
            sender.sendMessage(ChatColor.YELLOW + "Sending you to world " + ChatColor.AQUA + args[0] + ChatColor.YELLOW + "!");
            LinkLocation location = new LinkLocation(player.getLocation());
            location.setWorld(args[0]);
            ServerUtils.sendToLinkWorld(linkPlayer, new LinkWorld(args[0]), location, LinkLocationType.ABSOLUTE_SAFE);
            return true;
        }

        sender.sendMessage(ChatColor.RED + "/world <name>");
        return true;
    }

}
