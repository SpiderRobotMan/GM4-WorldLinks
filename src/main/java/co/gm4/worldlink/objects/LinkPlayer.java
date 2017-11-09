package co.gm4.worldlink.objects;

import co.gm4.worldlink.modules.display.Filter;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by MatrixTunnel on 9/14/2017.
 */
@Getter @Setter
public class LinkPlayer {

    private UUID uuid;
    private LinkPlayerData playerData;
    private List<LinkWorld> worlds;

    private Player player;
    private boolean gettingTransferred;
    private LinkLocationType locationType;

    private String advancementsJson;
    private String statsJson;

    private long lastLoginTime;
    private Location lastDeathLocation;

    public LinkPlayer(UUID uuid, LinkPlayerData playerData, List<LinkWorld> worlds) {
        this.uuid = uuid;
        this.playerData = playerData;
        this.worlds = worlds;
    }

    public LinkPlayer(UUID uuid, LinkPlayerData playerData, List<LinkWorld> worlds, String advancementsJson, String statsJson) {
        this.uuid = uuid;
        this.playerData = playerData;
        this.worlds = worlds;
        this.advancementsJson = advancementsJson;
        this.statsJson = statsJson;
    }

    public List<LinkWorld> getFilteredWorlds() {
        List<LinkWorld> filtered = new ArrayList<>();

        if (worlds.isEmpty()) return filtered;

        worlds.stream().filter(linkWorld -> Filter.canDisplay(this, linkWorld)).forEach(filtered::add);
        return filtered;
    }

}
