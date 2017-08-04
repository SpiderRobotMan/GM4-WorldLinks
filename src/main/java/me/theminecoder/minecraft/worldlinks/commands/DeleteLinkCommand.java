package me.theminecoder.minecraft.worldlinks.commands;

import me.theminecoder.minecraft.worldlinks.WorldLinks;
import me.theminecoder.minecraft.worldlinks.gui.ConfirmGUI;
import me.theminecoder.minecraft.worldlinks.objects.Link;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Arrays;

/**
 * @author theminecoder
 */
public class DeleteLinkCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        if (args.length <= 0) {
            sender.sendMessage(ChatColor.RED + "Please specify a link id!");
            return true;
        }

        Link link = WorldLinks.getInstance().getWorldManager().getWorldLink(args[0].toLowerCase());

        if (link == null) {
            sender.sendMessage(ChatColor.RED + "Link with id '" + args[0] + " not found!");
            return true;
        }
        new ConfirmGUI("Delete '" + link.getId() + "'", Arrays.asList(
                "This will remove the link from",
                "the database and all servers",
                "as they refresh."
        ), () -> {
            try {
                WorldLinks.getInstance().getLinkDao().delete(link);
                sender.sendMessage(ChatColor.RED + "Link deleted!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return true;
    }
}
