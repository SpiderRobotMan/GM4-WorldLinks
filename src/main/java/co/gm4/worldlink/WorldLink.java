package co.gm4.worldlink;

import co.gm4.worldlink.commands.ReloadCommand;
import co.gm4.worldlink.commands.WorldCommand;
import co.gm4.worldlink.listeners.BlockListener;
import co.gm4.worldlink.listeners.PlayerListener;
import co.gm4.worldlink.managers.DatabaseHandler;
import co.gm4.worldlink.managers.PlayerManager;
import co.gm4.worldlink.modules.Module;
import co.gm4.worldlink.utils.Config;
import co.gm4.worldlink.utils.DisplayTask;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Getter
public final class WorldLink extends JavaPlugin {

    private static WorldLink instance;
    private static String serverName;

    private List<Module> modules;

    private Config pluginConfig;

    private DisplayTask displayTask;
    private BukkitTask displayTaskRunnable;
    private PlayerManager playerManager;
    private DatabaseHandler databaseHandler;

    @Override
    public void onEnable() {
        instance = this;

        modules = new ArrayList<>();

        try {
            init();
        } catch (Exception e) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        playerManager = new PlayerManager();

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);

        getCommand("world").setExecutor(new WorldCommand());
        getCommand("wlreload").setExecutor(new ReloadCommand()); //TODO Change this junk command
    }

    private void init() throws Exception {
        pluginConfig = new Config();
        serverName = pluginConfig.getServerName();

        Bukkit.getPluginManager().registerEvents(displayTask = new DisplayTask(), this);
        displayTaskRunnable = Bukkit.getScheduler().runTaskTimer(WorldLink.get(), displayTask, 0L, 3L);

        databaseHandler = new DatabaseHandler(
                pluginConfig.getDatabaseHost(),
                pluginConfig.getDatabaseDatabase(),
                pluginConfig.getDatabaseUsername(),
                pluginConfig.getDatabasePassword()
        );

        modules.forEach(module -> Bukkit.getPluginManager().registerEvents((Listener) module, this));

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    public void reload() throws Exception {
        onDisable();

        init();
    }

    @Override
    public void onDisable() {
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this, "BungeeCord");

        displayTaskRunnable.cancel();

        Bukkit.getOnlinePlayers().forEach(player -> {
            try {
                databaseHandler.savePlayer(player.getUniqueId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        databaseHandler.getHikari().close();

        modules.forEach(module -> HandlerList.unregisterAll((Listener) module));
        modules.clear();
    }

    public static WorldLink get() {
        return instance;
    }

}
