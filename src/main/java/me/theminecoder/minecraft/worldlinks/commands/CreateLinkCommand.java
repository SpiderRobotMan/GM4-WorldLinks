package me.theminecoder.minecraft.worldlinks.commands;

import me.theminecoder.minecraft.worldlinks.WorldLinks;
import me.theminecoder.minecraft.worldlinks.gui.LinkEditGUI;
import me.theminecoder.minecraft.worldlinks.objects.Link;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author theminecoder
 */
public class CreateLinkCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Please specify a link id!");
            return true;
        }

        String id = args[0].toLowerCase();
        if (WorldLinks.getInstance().getWorldManager().getWorldLink(id) != null) {
            sender.sendMessage(ChatColor.RED + "Link with id '" + id + "' already exists!");
            return true;
        }

        new LinkEditGUI(new Link(id)).open((Player) sender);
        return false;
    }
}
