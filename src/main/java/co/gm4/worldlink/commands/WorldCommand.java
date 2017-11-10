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
