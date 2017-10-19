package co.gm4.worldlink.utils;

import co.gm4.worldlink.WorldLink;
import co.gm4.worldlink.objects.LinkLocationType;
import co.gm4.worldlink.objects.LinkPlayerData;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by MatrixTunnel on 9/10/2017.
 */
public class PlayerUtils {

    public static boolean isViewingLinks(Player player) {
        ItemStack handItem = player.getInventory().getItemInMainHand();
        Config config = WorldLink.get().getPluginConfig();
        ItemStack configItem = config.getSelectorItem();

        if(config.selectorItemHasToMatch()) {
            return handItem.getItemMeta() != null && handItem.getItemMeta().getDisplayName() != null && handItem.getItemMeta().getLore() != null && (handItem.getType() == configItem.getType() && handItem.getDurability() == configItem.getDurability() && handItem.getItemMeta().getDisplayName().equals(configItem.getItemMeta().getDisplayName()) && handItem.getItemMeta().getLore().equals(configItem.getItemMeta().getLore()));
        }

        return configItem.isSimilar(handItem);
    }

    public static void updatePlayer(Player player, LinkPlayerData data, LinkLocationType locationType) {
        if (data == null) return;

        Bukkit.getScheduler().runTask(WorldLink.get(), () -> {
            if (player == null || !player.isOnline()) return;

            try {
                resetPlayer(player);
                player.setGameMode(data.getGamemode());

                player.setVelocity(data.getVelocity());
                Bukkit.getScheduler().runTask(WorldLink.get(), () -> {
                    try {
                        player.getInventory().setContents(data.getInventory());
                    } catch (IOException e) {
                        WorldLink.get().getLogger().severe("Failed to set player's inventory: " + player.getUniqueId().toString());
                        e.printStackTrace();
                    }

                    try {
                        player.getEnderChest().setContents(data.getEnderChest());
                    } catch (IOException e) {
                        WorldLink.get().getLogger().severe("Failed to set player's enderchest inventory: " + player.getUniqueId().toString());
                        e.printStackTrace();
                    }
                });

                if (locationType != null) {
                    if (locationType.isSafe()) {
                        Location location = LocationUtils.getSafeLocation(data.getLocation(), locationType.getMaxRadius(), locationType.getMaxYRadius());
                        player.teleport(location);
                    } else {
                        player.teleport(data.getLocation());
                    }
                } else {
                    player.teleport(data.getLocation());
                }
                WorldLink.get().getLogger().info("Teleporting " + player.getUniqueId().toString() + " to location " + data.getLocation().toString());

                player.setHealth(data.getHealth());
                player.setHealthScale(data.getHealthScale());
                player.setHealthScaled(data.isHealthScaled());

                player.setTotalExperience(data.getTotalExperience());
                player.setLevel(data.getLevel());
                player.setExp(data.getExp());

                Arrays.stream(data.getActivePotionEffects()).forEach(potionEffect -> player.addPotionEffect(potionEffect, true));

                player.setFoodLevel(data.getFoodLevel());
                player.setExhaustion(data.getExhaustion());

                player.setMaximumAir(data.getMaxAir());
                player.setRemainingAir(data.getRemainingAir());
                player.setFireTicks(data.getFireTicks());
                player.setMaximumNoDamageTicks(data.getMaxNoDamageTicks());
                player.setNoDamageTicks(data.getNoDamageTicks());

                player.getInventory().setHeldItemSlot(data.getHeldItemSlot());
                player.updateInventory();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void resetPlayer(Player player) {
        if (player.isOnline()) {
            player.getInventory().clear();
            player.getEnderChest().clear();
            player.getInventory().setArmorContents(new ItemStack[]{new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});

            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());

            player.setTotalExperience(0);
            player.setLevel(0);
            player.setExp(0);

            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        }
    }

    public static boolean hasAdvancement(Player player, String nameSpace) {
        Advancement advancement = null;

        for (Iterator<Advancement> i = Bukkit.getServer().advancementIterator(); i.hasNext();) {
            Advancement adv = i.next();
            if ((adv.getKey().getNamespace() + ":" + adv.getKey().getKey()).equalsIgnoreCase(nameSpace)) {
                advancement = adv;
                break;
            }
        }

        return advancement != null && player.getAdvancementProgress(advancement).isDone();
    }

}
