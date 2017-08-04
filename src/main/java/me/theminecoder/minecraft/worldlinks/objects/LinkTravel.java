package me.theminecoder.minecraft.worldlinks.objects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by theminecoder on 1/6/17.
 */
@DatabaseTable(tableName = "link_travel")
public class LinkTravel {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true)
    private LinkPlayer player;

    @DatabaseField
    private String fromServer;

    @DatabaseField
    private String toServer;

    @DatabaseField(foreign = true)
    private Link link;

    LinkTravel(){}

    public LinkTravel(LinkPlayer player, String fromServer, String toServer, Link link) {
        this.player = player;
        this.fromServer = fromServer;
        this.toServer = toServer;
        this.link = link;
    }

    public int getId() {
        return id;
    }

    public LinkPlayer getPlayer() {
        return player;
    }

    public String getFromServer() {
        return fromServer;
    }

    public String getToServer() {
        return toServer;
    }

    public Link getLink() {
        return link;
    }

}
