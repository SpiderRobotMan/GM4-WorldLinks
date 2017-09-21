package co.gm4.worldlink.managers;

import co.gm4.worldlink.objects.LinkPlayer;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by MatrixTunnel on 9/15/2017.
 */
public class PlayerManager {

    @Getter private Collection<LinkPlayer> players = new ConcurrentLinkedQueue<>();

    public void addPlayer(LinkPlayer linkPlayer) {
        this.players.add(linkPlayer);
    }

    public void removePlayer(LinkPlayer linkPlayer) {
        this.players.remove(linkPlayer);
    }

    public LinkPlayer getLinkPlayer(Player player) {
        return getLinkPlayer(player.getUniqueId());
    }

    public LinkPlayer getLinkPlayer(UUID uuid) {
        for (LinkPlayer linkPlayer : players) {
            if (linkPlayer.getUuid().toString().equals(uuid.toString())) {
                return linkPlayer;
            }
        }
        return null;
    }

}
