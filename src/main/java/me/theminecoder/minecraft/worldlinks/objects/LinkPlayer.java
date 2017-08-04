package me.theminecoder.minecraft.worldlinks.objects;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;
import me.theminecoder.minecraft.worldlinks.WorldLinks;

import java.util.UUID;

/**
 * @author theminecoder
 */
@DatabaseTable(tableName = "link_players")
public class LinkPlayer extends BaseDaoEnabled {

    @DatabaseField(id = true)
    private UUID uuid;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<LinkUnlock> unlockedLinks;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Link activeLink;

    @DatabaseField
    private LinkLocation oldLocation;

    private transient boolean viewingWorldLinks = false;

    LinkPlayer() {
    }

    public LinkPlayer(UUID uuid) {
        this.uuid = uuid;
        this.setDao(WorldLinks.getInstance().getLinkPlayerDao());
    }

    public UUID getUuid() {
        return uuid;
    }

    public ForeignCollection<LinkUnlock> getUnlockedLinks() {
        return unlockedLinks;
    }

    public void setUnlockedLinks(ForeignCollection<LinkUnlock> unlockedLinks) {
        this.unlockedLinks = unlockedLinks;
    }

    public Link getActiveLink() {
        return activeLink;
    }

    public void setActiveLink(Link activeLink) {
        this.activeLink = activeLink;
    }

    public LinkLocation getOldLocation() {
        return oldLocation;
    }

    public void setOldLocation(LinkLocation oldLocation) {
        this.oldLocation = oldLocation;
    }

    public boolean isViewingWorldLinks() {
        return viewingWorldLinks;
    }

    public void setViewingWorldLinks(boolean viewingWorldLinks) {
        this.viewingWorldLinks = viewingWorldLinks;
    }
}
