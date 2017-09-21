package co.gm4.worldlink;

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

import java.util.ArrayList;
import java.util.List;

@Getter
public final class WorldLink extends JavaPlugin {

    private static WorldLink instance;
    private static String serverName;

    private List<Module> modules;

    private Config pluginConfig;
    private DisplayTask displayTask;
    private PlayerManager playerManager;
    private DatabaseHandler databaseHandler;



    @Override
    public void onEnable() {
        instance = this;

        modules = new ArrayList<>();

        pluginConfig = new Config();
        serverName = pluginConfig.getServerName();

        Bukkit.getPluginManager().registerEvents(displayTask = new DisplayTask(), this);
        displayTask.run();

        playerManager = new PlayerManager();
        databaseHandler = new DatabaseHandler();

        modules.forEach(module -> Bukkit.getPluginManager().registerEvents((Listener) module,  this));

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> WorldLink.get().getDatabaseHandler().savePlayer(player.getUniqueId()));

        modules.forEach(module -> HandlerList.unregisterAll((Listener) module));
    }

    public void reload() {
        pluginConfig = new Config();
    }

    public static WorldLink get() {
        return instance;
    }

}
