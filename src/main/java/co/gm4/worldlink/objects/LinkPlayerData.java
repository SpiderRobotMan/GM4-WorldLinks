package co.gm4.worldlink.objects;

import co.gm4.worldlink.utils.Serialization;
import com.google.gson.Gson;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.Map;

/**
 * Created by MatrixTunnel on 9/10/2017.
 */
@Getter
public class LinkPlayerData {

    private final GameMode gamemode;
    private Map<String, Object> location;
    private final Map<String, Object> velocity;
    private final String inventory;
    private final String enderchest;
    private final String potionEffects;
    private final double health, healthScale;
    private final boolean isHealthScaled;
    private final int totalExperience, level;
    private final float exp;
    private final int foodLevel;
    private final float exhaustion;
    private final int maxAir, remainingAir;
    private final int fireTicks;
    private final int maxNoDamageTicks, noDamageTicks;
    private final int heldItemSlot;

    public LinkPlayerData(Player player) {
        this.gamemode = player.getGameMode();
        this.location = player.getLocation().serialize();
        this.velocity = player.getVelocity().serialize();
        this.inventory = Serialization.itemStackArrayToBase64(player.getInventory().getContents());
        this.enderchest = Serialization.itemStackArrayToBase64(player.getEnderChest().getContents());
        this.potionEffects = Serialization.potionEffectCollectionToBase64(player.getActivePotionEffects());
        this.health = player.getHealth();
        this.healthScale = player.getHealthScale();
        this.isHealthScaled = player.isHealthScaled();
        this.totalExperience = player.getTotalExperience();
        this.level = player.getLevel();
        this.exp = player.getExp();
        this.foodLevel = player.getFoodLevel();
        this.exhaustion = player.getExhaustion();
        this.maxAir = player.getMaximumAir();
        this.remainingAir = player.getRemainingAir();
        this.fireTicks = player.getFireTicks();
        this.maxNoDamageTicks = player.getMaximumNoDamageTicks();
        this.noDamageTicks = player.getNoDamageTicks();
        this.heldItemSlot = player.getInventory().getHeldItemSlot();
    }

    public Location getLocation() {
        return Location.deserialize(location);
    }

    public void setLocation(LinkLocation location){
        Map<String, Object> map = location.serialize();
        if (location.isIgnoreX()) map.put("x", this.location.get("x"));
        if (location.isIgnoreY()) map.put("y", this.location.get("y"));
        if (location.isIgnoreZ()) map.put("z", this.location.get("z"));
        if (location.isIgnoreYaw()) map.put("yaw", this.location.get("yaw"));
        if (location.isIgnorePitch()) map.put("pitch", this.location.get("pitch"));
        this.location = map;
    }

    public void setLocation(Location location){
        this.location = location.serialize();
    }

    public Vector getVelocity() {
        return Vector.deserialize(velocity);
    }

    public ItemStack[] getInventory() throws IOException {
        return Serialization.itemStackArrayFromBase64(inventory);
    }

    public ItemStack[] getEnderChest() throws IOException {
        return Serialization.itemStackArrayFromBase64(enderchest);
    }

    public PotionEffect[] getActivePotionEffects() throws IOException {
        return Serialization.potionEffectArrayFromBase64(potionEffects);
    }

    public String getAsJson(){
        return new Gson().toJson(this);
    }

    //private Map<String, Object> potionEffectsToMap(Collection<PotionEffect> potionEffects) {
    //    Map<String, Object> effects = new HashMap<>();
    //    potionEffects.forEach(potionEffect -> effects.putAll(potionEffect.serialize()));
    //    return effects;
    //}

}
