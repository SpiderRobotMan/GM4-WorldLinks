package me.theminecoder.minecraft.worldlinks.objects;

import me.theminecoder.minecraft.worldlinks.WorldLinks;
import me.theminecoder.minecraft.worldlinks.gui.LinkConditionEditGUI;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;

/**
 * @author theminecoder
 */
public enum LinkConditionConfigType {

    STRING {
        @Override
        public void openGUI(Link link, LinkCondition condition, String configId, Player player) {
            new AnvilGUI(WorldLinks.getInstance(), player, "", (player2, string) -> {
                condition.setConfig(configId, string);
                new LinkConditionEditGUI(condition).open(player);
                return null;
            });
        }
    },
    INT {
        @Override
        public void openGUI(Link link, LinkCondition condition, String configId, Player player) {
            new AnvilGUI(WorldLinks.getInstance(), player, "", ((player2, number) -> {
                int finalNumber;
                try {
                    finalNumber = Integer.parseInt(number);
                } catch (NumberFormatException ex) {
                    return ex.getMessage();
                }

                condition.setConfig(configId, finalNumber);
                return number;
            }));
        }
    },
    LOCATION {
        @Override
        public void openGUI(Link link, LinkCondition condition, String configId, Player player) {

        }
    };

    public abstract void openGUI(Link link, LinkCondition condition, String configId, Player player);

}
