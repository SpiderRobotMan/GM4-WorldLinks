package me.theminecoder.minecraft.worldlinks.objects;

import org.bukkit.entity.Player;

import java.util.Map;

/**
 * @author theminecoder
 */
public enum LinkConditionType {

    TEST {
        @Override
        public boolean valid(Player player, LinkPlayer linkPlayer, Map<String, Object> config) {
            return true;
        }
    };

    public abstract boolean valid(Player player, LinkPlayer linkPlayer, Map<String, Object> config);

}
