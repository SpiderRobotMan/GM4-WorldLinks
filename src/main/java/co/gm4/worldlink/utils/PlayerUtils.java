package co.gm4.worldlink.utils;

import co.gm4.worldlink.WorldLink;
import co.gm4.worldlink.objects.LinkLocationType;
import co.gm4.worldlink.objects.LinkPlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by MatrixTunnel on 9/10/2017.
 */
public class PlayerUtils {

    public static boolean isViewingLinks(Player player) {
        ItemStack handItem = player.getInventory().getItemInMainHand();
        Config config = WorldLink.get().getPluginConfig();
        ItemStack configItem = config.getSelectorItem();

        return handItem != null && configItem != null && isHoldingWorldLinkItem(handItem);
    }

    public static boolean isHoldingWorldLinkItem(ItemStack handItem) {
        Config config = WorldLink.get().getPluginConfig();
        ItemStack configItem = config.getSelectorItem();

        if (handItem == null || configItem == null) return false;

        // Not adding other metadata because it's messing things up (maybe add back later)
        // It looks a little messy but I'll fix that later ¯\_(ツ)_/¯
        return (config.selectorItemHasToMatch() ? handItem.hasItemMeta() && configItem.hasItemMeta() && ( // Equals
                (configItem.getItemMeta().hasDisplayName() && handItem.getItemMeta().hasDisplayName() &&
                        handItem.getItemMeta().getDisplayName().equals(configItem.getItemMeta().getDisplayName())) && //Display name
                        (configItem.getItemMeta().hasLore() && handItem.getItemMeta().hasLore() &&
                                handItem.getItemMeta().getLore().equals(configItem.getItemMeta().getLore())) && //Lore
                        handItem.getType().equals(configItem.getType()) //Type
        ) : handItem.hasItemMeta() && configItem.hasItemMeta() && ( // Similar
                (configItem.getItemMeta().hasDisplayName() && handItem.getItemMeta().hasDisplayName() &&
                        handItem.getItemMeta().getDisplayName().equals(configItem.getItemMeta().getDisplayName())) || //Display name
                        (configItem.getItemMeta().hasLore() && handItem.getItemMeta().hasLore() &&
                                handItem.getItemMeta().getLore().equals(configItem.getItemMeta().getLore())) || //Lore
                        handItem.getType().equals(configItem.getType()) //Type
        ));

        //return (config.selectorItemHasToMatch() ? handItem.isSimilar(configItem) :
        //        handItem.hasItemMeta() && configItem.hasItemMeta() && (
        //                (configItem.getItemMeta().hasDisplayName() && handItem.getItemMeta().hasDisplayName() && handItem.getItemMeta().getDisplayName().equals(configItem.getItemMeta().getDisplayName())) ||
        //                (configItem.getItemMeta().hasLore() && handItem.getItemMeta().hasLore() && handItem.getItemMeta().getLore().equals(configItem.getItemMeta().getLore())) ||
        //                handItem.getType().equals(configItem.getType())
        //        ));
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
                player.setFallDistance(data.getFallDistance());

                player.setFlying(data.isFlying());
                player.setGliding(data.isGliding());
                player.getScoreboardTags().addAll(data.getScoreboardTags());

                player.getInventory().setHeldItemSlot(data.getHeldItemSlot());
                player.updateInventory();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void resetPlayer(Player player) {
        if (player.isOnline()) {
            player.getScoreboardTags().clear();
            player.getInventory().clear();
            player.getEnderChest().clear();
            player.getInventory().setArmorContents(new ItemStack[]{new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});

            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            player.setFireTicks(0);

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
