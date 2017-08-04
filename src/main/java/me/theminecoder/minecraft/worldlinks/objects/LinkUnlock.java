package me.theminecoder.minecraft.worldlinks.objects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author theminecoder
 */
@DatabaseTable(tableName = "link_unlocks")
public class LinkUnlock {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(foreign = true)
    private LinkPlayer player;

    @DatabaseField(foreign = true)
    private Link link;

    LinkUnlock() {
    }

    public LinkUnlock(LinkPlayer player, Link link) {
        this.player = player;
        this.link = link;
    }

    public LinkPlayer getPlayer() {
        return player;
    }

    public Link getLink() {
        return link;
    }
}
