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
            if (event.getAdvancement().getKey().getKey().equals(link.getUnlockAdvancementKey())) {
                LinkPlayer linkPlayer = WorldLink.get().getPlayerManager().getLinkPlayer(event.getPlayer().getUniqueId());
                LinkWorld linkWorld = new LinkWorld(link.getName());

                if (!linkPlayer.getWorlds().contains(linkWorld)) {
                    linkPlayer.getWorlds().add(linkWorld);
                    //TODO message
                }
            }
        });
    }

}
