package co.gm4.worldlink.utils;

import co.gm4.worldlink.WorldLink;
import co.gm4.worldlink.objects.*;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by MatrixTunnel on 9/10/2017.
 */
public class ServerUtils {

    private static Map<UUID, BukkitTask> playerLeftTasks = new HashMap<>();

    /**
     * Sends a player to a specified server via the BungeeCord messaging
     * channel.
     *
     * @param player The player
     * @param server The server name
     */
    public static void sendToServer(Player player, String server) {
        if (player != null) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();

            out.writeUTF("Connect");
            out.writeUTF(server);
            player.sendPluginMessage(WorldLink.get(), "BungeeCord", out.toByteArray());
        }
    }

    public static void sendToLink(Link link, LinkPlayer linkPlayer, LinkWorld linkWorld, LinkLocation linkLocation) {
        Player player = linkPlayer.getPlayer();

        linkPlayer.setGettingTransferred(true);

        if (!link.getBeforeCommands().isEmpty()) link.getBeforeCommands().forEach(s -> runCommand(link, linkWorld, linkPlayer, s));
        LinkPlayerData playerData = new LinkPlayerData(player);

        LinkLocationType locationType = LinkLocationType.getByConfigName(link.getTeleportType());

        if (locationType != null) {
            playerData.setLocation(locationType.getFixedLocation(new LinkLocation(player.getLocation()), link.getTargetLocation()));
            WorldLink.get().getPlayerManager().getLinkPlayer(player).setLocationType(locationType);
        }

        if (linkLocation != null) playerData.setLocation(linkLocation);

        WorldLink.get().getPlayerManager().getLinkPlayer(player).setPlayerData(playerData);
        WorldLink.get().getPlayerManager().getLinkPlayer(player).setLocationType(locationType);

        saveData(linkPlayer);

        try {
            WorldLink.get().getDatabaseHandler().savePlayer(player.getUniqueId());
        } catch (SQLException e) {
            player.sendMessage(ChatColor.RED + "You're too heavy for the universe to pick you up! Empty your inventory to travel.");
            WorldLink.get().getLogger().warning("Player's inventory it too large to travel: " + player.getUniqueId().toString());
            e.printStackTrace();
            return;
        }

        if (!link.getDuringCommands().isEmpty()) link.getDuringCommands().forEach(s -> runCommand(link, linkWorld, linkPlayer, s));

        // Do these effects after savePlayer so they don't save with the player data
        if (link.isZoomOnClick()) player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 255));
        if (link.isBlindnessOnClick()) player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 15));
        //if (link.isPortalSoundOnClick()) player.playSound(player.getEyeLocation(), Sound.BLOCK_PORTAL_TRIGGER);

        sendToServer(player, link.getName());
        if (!link.getAfterCommands().isEmpty()) {
            BukkitTask playerLeftTask = Bukkit.getScheduler().runTaskTimer(WorldLink.get(), () -> {
                if (Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).noneMatch(uuid -> uuid.equals(player.getUniqueId()))) {
                    link.getAfterCommands().forEach(s -> runCommand(link, linkWorld, linkPlayer, s));
                    playerLeftTasks.get(linkPlayer.getUuid()).cancel();
                }
            }, 1L, 0L);

            playerLeftTasks.put(linkPlayer.getUuid(), playerLeftTask);
        }
    }

    private static boolean runCommand(Link link, LinkWorld linkWorld, LinkPlayer linkPlayer, String command) {
        return !command.isEmpty() && Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", linkPlayer.getPlayer().getName()).replace("%world%", linkWorld.getName()));

    }

    private static void saveData(LinkPlayer player) {
        try {
            savePlayerStats(player.getPlayer());
        } catch (Exception e) {
            e.printStackTrace();
        }

        File advancements = new File(Bukkit.getWorlds().get(0).getName() + "/advancements/" + player.getPlayer().getUniqueId().toString() + ".json");
        if (advancements.exists()) {
            player.setAdvancementsJson(getFileContents(advancements));
        } else {
            player.setAdvancementsJson(null);
        }
        File stats = new File(Bukkit.getWorlds().get(0).getName() + "/stats/" + player.getPlayer().getUniqueId().toString() + ".json");
        if (stats.exists()) {
            player.setStatsJson(getFileContents(stats));
        } else {
            player.setStatsJson(null);
        }
    }

    private static String getFileContents(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder stringBuilder = new StringBuilder();
            reader.lines().forEach(stringBuilder::append);
            return stringBuilder.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void savePlayerStats(Player player) throws Exception {
        Object server = Reflection.getNMSClass("MinecraftServer").getMethod("getServer").invoke(null);
        Object playerList = server.getClass().getMethod("getPlayerList").invoke(server);
        Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);

        Method savePlayerFileMethod = playerList.getClass().getSuperclass().getDeclaredMethod("savePlayerFile", Reflection.getNMSClass("EntityPlayer"));

        savePlayerFileMethod.setAccessible(true);
        savePlayerFileMethod.invoke(playerList, entityPlayer);
        savePlayerFileMethod.setAccessible(false);
    }

}
