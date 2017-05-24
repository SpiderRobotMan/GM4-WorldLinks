package me.theminecoder.minecraft.worldlinks.tasks;

import me.theminecoder.minecraft.worldlinks.WorldLinks;

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
                .filter(player -> true)
                .forEach(player -> {
//                    player.getUnlockedLinks().forEach(link -> link.displayToPlayer(player));
                });
    }
}
