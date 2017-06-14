package me.theminecoder.minecraft.worldlinks.tasks;

import me.theminecoder.minecraft.worldlinks.WorldLinks;
import me.theminecoder.minecraft.worldlinks.objects.LinkPlayer;
import org.bukkit.Bukkit;

/**
 * @author theminecoder
 */
public class WorldLinkDisplayerTask implements Runnable {

    private WorldLinks plugin;

    public WorldLinkDisplayerTask(WorldLinks plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.getPlayerManager().getOnlinePlayers().stream()
                .filter(LinkPlayer::isViewingWorldLinks)
                .forEach(player -> player.getUnlockedLinks().stream()
                        .filter(link -> !link.getConditions().stream().anyMatch(linkCondition ->
                                !linkCondition.getType().valid(Bukkit.getPlayer(player.getUuid()), player, linkCondition.getConfig())
                        ))
                        .forEach(link -> link.displayToPlayer(player)));
    }
}
