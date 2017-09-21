package co.gm4.worldlink.objects;

import co.gm4.worldlink.modules.display.Filter;
import lombok.Getter;
import lombok.Setter;

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

    @Setter private LinkLocationType locationType;

    public LinkPlayer(UUID uuid, LinkPlayerData playerData, List<LinkWorld> worlds) {
        this.uuid = uuid;
        this.playerData = playerData;
        this.worlds = worlds;
    }

    public List<LinkWorld> getFilteredWorlds() {
        List<LinkWorld> filtered = new ArrayList<>();

        if (worlds.isEmpty()) return filtered;

        worlds.stream().filter(linkWorld -> Filter.canDisplay(this, linkWorld)).forEach(filtered::add);
        return filtered;
    }

}
