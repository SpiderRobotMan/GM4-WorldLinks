package me.theminecoder.minecraft.worldlinks.objects;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

/**
 * @author theminecoder
 */
@DatabaseTable(tableName = "link_players")
public class LinkPlayer extends BaseDaoEnabled {

    @DatabaseField(id = true)
    private UUID uuid;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<Link> unlockedLinks;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Link activeLink;

    LinkPlayer(){
    }

    public LinkPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public ForeignCollection<Link> getUnlockedLinks() {
        return unlockedLinks;
    }

    public void setUnlockedLinks(ForeignCollection<Link> unlockedLinks) {
        this.unlockedLinks = unlockedLinks;
    }

    public Link getActiveLink() {
        return activeLink;
    }

    public void setActiveLink(Link activeLink) {
        this.activeLink = activeLink;
    }
}
