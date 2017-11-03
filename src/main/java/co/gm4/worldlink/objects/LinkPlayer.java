package co.gm4.worldlink.objects;

import co.gm4.worldlink.modules.display.Filter;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by MatrixTunnel on 9/14/2017.
 */
@Getter
public class LinkPlayer {

    private UUID uuid;
    @Setter private LinkPlayerData playerData;
    @Setter private List<LinkWorld> worlds;

    @Setter private Player player;
    @Setter private boolean gettingTransferred;
    @Setter private LinkLocationType locationType;

    @Setter private String advancementsJson;
    @Setter private String statsJson;

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
