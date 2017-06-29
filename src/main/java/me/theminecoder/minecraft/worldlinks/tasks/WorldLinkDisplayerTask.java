package me.theminecoder.minecraft.worldlinks.tasks;

import me.theminecoder.minecraft.worldlinks.WorldLinks;
import me.theminecoder.minecraft.worldlinks.objects.LinkPlayer;
import me.theminecoder.minecraft.worldlinks.utils.ParticleUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

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
                .forEach(player -> {
                    player.getUnlockedLinks().stream()
                            .filter(link -> !link.getConditions().stream().anyMatch(linkCondition ->
                                    !linkCondition.getType().valid(Bukkit.getPlayer(player.getUuid()), player, linkCondition.getConfig())
                            ))
                            .forEach(link -> {
                                Player bukkitPlayer = Bukkit.getPlayer(player.getUuid());
                                Location location = bukkitPlayer.getEyeLocation().getBlock().getLocation().add(0.5, 0.5, 0.5);
                                location.add(bukkitPlayer.getLocation().clone().getDirection().normalize().add(new Vector(1, 0, 1)));
                                ParticleUtils.sendParticle(bukkitPlayer, "REDSTONE", location, 1, 1);
                            });
                });
    }
}
