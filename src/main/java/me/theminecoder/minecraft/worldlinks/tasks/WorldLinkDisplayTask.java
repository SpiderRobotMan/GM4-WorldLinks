package me.theminecoder.minecraft.worldlinks.tasks;

import com.google.common.collect.Range;
import me.theminecoder.minecraft.worldlinks.WorldLinks;
import me.theminecoder.minecraft.worldlinks.objects.LinkPlayer;
import me.theminecoder.minecraft.worldlinks.objects.LinkUnlock;
import me.theminecoder.minecraft.worldlinks.utils.ParticleUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * @author theminecoder
 */
public class WorldLinkDisplayTask implements Runnable {

    private WorldLinks plugin;

    public WorldLinkDisplayTask(WorldLinks plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.getPlayerManager().getOnlinePlayers().stream()
                .filter(LinkPlayer::isViewingWorldLinks)
                .forEach(player -> {
                    player.getUnlockedLinks().stream()
                            .map(LinkUnlock::getLink)
                            .filter(link -> !link.getConditions().stream().anyMatch(linkCondition ->
                                    !linkCondition.getType().valid(Bukkit.getPlayer(player.getUuid()), player, linkCondition)
                            ))
                            .forEach(link -> {
                                Player bukkitPlayer = Bukkit.getPlayer(player.getUuid());
                                Location location = bukkitPlayer.getEyeLocation().getBlock().getLocation().add(0.5, 0.5, 0.5);
                                location.add(bukkitPlayer.getLocation().clone().getDirection().normalize().add(new Vector(1, 0, 1)));
                                ParticleUtils.sendParticle(bukkitPlayer, "REDSTONE", location, 1, 1);

                                if (Range.closed(link.getParticleAngle() - 20, link.getParticleAngle() + 20).contains(new Float(bukkitPlayer.getLocation().getYaw()).intValue())) {
                                    bukkitPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.YELLOW + "Right click to travel to " + ChatColor.AQUA + link.getName()));
                                }
                            });
                });
    }
}
