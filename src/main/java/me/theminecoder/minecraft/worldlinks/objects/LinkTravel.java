package me.theminecoder.minecraft.worldlinks.objects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

/**
 * Created by theminecoder on 1/6/17.
 */
@DatabaseTable(tableName = "link_travel")
public class LinkTravel {

    @DatabaseField(generatedId = true, id = true)
    private int id;

    @DatabaseField
    private UUID player;

    @DatabaseField
    private String fromServer;

    @DatabaseField
    private String toServer;

    @DatabaseField(foreign = true)
    private Link link;

    public LinkTravel(UUID player, String fromServer, String toServer, Link link) {
        this.player = player;
        this.fromServer = fromServer;
        this.toServer = toServer;
        this.link = link;
    }

    public int getId() {
        return id;
    }

    public UUID getPlayer() {
        return player;
    }

    public void setPlayer(UUID player) {
        this.player = player;
    }

    public String getFromServer() {
        return fromServer;
    }

    public void setFromServer(String fromServer) {
        this.fromServer = fromServer;
    }

    public String getToServer() {
        return toServer;
    }

    public void setToServer(String toServer) {
        this.toServer = toServer;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }
}
