package co.gm4.worldlink.modules.display;

import co.gm4.worldlink.WorldLink;
import co.gm4.worldlink.objects.Link;
import co.gm4.worldlink.objects.LinkPlayer;
import co.gm4.worldlink.objects.LinkWorld;
import co.gm4.worldlink.utils.Config;
import co.gm4.worldlink.utils.PlayerUtils;
import org.bukkit.Bukkit;

/**
 * Created by MatrixTunnel on 9/20/2017.
 */
public class Filter {

    public static boolean canDisplay(LinkPlayer linkPlayer, LinkWorld linkWorld) {
        Config config = WorldLink.get().getPluginConfig();
        boolean canDisplay = true;

        for (Link link : config.getLinks()) {
            if (link.getName().equals(linkWorld.getName())) {
                if (!link.getDisplayAdvancements().isEmpty() && !(link.getDisplayAdvancements().size() == 1 && link.getDisplayAdvancements().get(0).equals("name/space_here"))) {
                    canDisplay = link.getDisplayAdvancements().stream().allMatch(s -> PlayerUtils.hasAdvancement(Bukkit.getPlayer(linkPlayer.getUuid()), s));
                }


            }
        }

        return canDisplay;
    }

}
