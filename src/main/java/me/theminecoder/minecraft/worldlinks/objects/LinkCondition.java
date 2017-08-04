package me.theminecoder.minecraft.worldlinks.objects;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Optional;

/**
 * @author theminecoder
 */
@DatabaseTable(tableName = "link_conditions")
public class LinkCondition {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true)
    private Link link;

    @DatabaseField
    private LinkConditionType type;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<LinkConditionConfigValue> config;

    @DatabaseField
    private boolean unlocksLink = false;

    LinkCondition() {
    }

    public LinkCondition(Link link, LinkConditionType type) {
        this.link = link;
        this.type = type;
    }

    public Link getLink() {
        return link;
    }

    public LinkConditionType getType() {
        return type;
    }

    public LinkConditionConfigValue getConfig(String id) {
        return config.stream().filter(config -> config.getConfigId().equalsIgnoreCase(id)).findFirst().orElse(new LinkConditionConfigValue(this, id, null));
    }

    public void setConfig(String id, Serializable value) {
        config.stream().filter(config -> config.getConfigId().equalsIgnoreCase(id)).findFirst().ifPresent(configValue -> config.remove(configValue));
        config.add(new LinkConditionConfigValue(this, id, value));
    }

    public boolean isUnlocksLink() {
        return unlocksLink;
    }

    public void setUnlocksLink(boolean unlocksLink) {
        this.unlocksLink = unlocksLink;
    }
}
