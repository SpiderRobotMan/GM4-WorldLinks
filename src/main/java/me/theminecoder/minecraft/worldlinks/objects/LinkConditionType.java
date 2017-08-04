package me.theminecoder.minecraft.worldlinks.objects;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * @author theminecoder
 */
public enum LinkConditionType {

    TEST {
        @Override
        public boolean valid(Player player, LinkPlayer linkPlayer, LinkCondition condition) {
            return condition.getConfig("memes").asBoolean(true);
        }
    },
    HEIGHT_VALUE {
        @Override
        public boolean valid(Player player, LinkPlayer linkPlayer, LinkCondition condition) {
            return condition.getConfig("above").asBoolean(false) ?
                    player.getLocation().getY() > condition.getConfig("hieght").asInt() :
                    player.getLocation().getY() < condition.getConfig("hieght").asInt();
        }
    },
    STANDING_ON_BLOCK {
        @Override
        public boolean valid(Player player, LinkPlayer linkPlayer, LinkCondition condition) {
            return player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().name().equalsIgnoreCase(condition.getConfig("material").asString());
        }
    };

    public abstract boolean valid(Player player, LinkPlayer linkPlayer, LinkCondition condition);
//    public abstract Map<String, LinkConditionConfigType> getConfigOptions();

}
