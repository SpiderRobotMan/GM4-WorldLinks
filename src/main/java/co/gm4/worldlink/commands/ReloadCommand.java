package co.gm4.worldlink.commands;

import co.gm4.worldlink.WorldLink;
import org.bukkit.Bukkit;
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
        try {
            WorldLink.get().reload();
        } catch (Exception ignore) {
            Bukkit.getPluginManager().disablePlugin(WorldLink.get());
            sender.sendMessage(ChatColor.RED + "Failed to reload config! Disabling plugin to prevent problems"); //TODO Change this
            return true;
        }

        sender.sendMessage(ChatColor.GREEN + "WorldLink reloaded!");
        return true;
    }

}
