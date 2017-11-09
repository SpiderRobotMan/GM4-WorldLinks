package co.gm4.worldlink.commands;

import co.gm4.worldlink.WorldLink;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by MatrixTunnel on 9/30/2017.
 */
public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        WorldLink.get().reload();
        sender.sendMessage(ChatColor.GREEN + "WorldLink reloaded!");
        return true;
    }

}
