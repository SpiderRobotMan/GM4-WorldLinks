package me.theminecoder.minecraft.worldlinks.objects;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author theminecoder
 */
@DatabaseTable(tableName = "link_links")
public class Link extends BaseDaoEnabled {

    @DatabaseField(id = true)
    private String id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String server;

    @DatabaseField
    private String particle;

    @DatabaseField
    private LinkType linkType;

    @DatabaseField
    private LinkLocation location;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<LinkCondition> conditions;

    Link(){
    }

    public Link(String id, String name, String server, String particle, LinkType linkType, LinkLocation location) {
        this.id = id;
        this.name = name;
        this.server = server;
        this.particle = particle;
        this.linkType = linkType;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getParticle() {
        return particle;
    }

    public void setParticle(String particle) {
        this.particle = particle;
    }

    public LinkType getLinkType() {
        return linkType;
    }

    public void setLinkType(LinkType linkType) {
        this.linkType = linkType;
    }

    public LinkLocation getLocation() {
        return location;
    }

    public void setLocation(LinkLocation location) {
        this.location = location;
    }

    public ForeignCollection<LinkCondition> getConditions() {
        return conditions;
    }

}
