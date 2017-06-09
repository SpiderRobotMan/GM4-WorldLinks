package me.theminecoder.minecraft.worldlinks.objects;

import com.google.common.collect.Maps;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Map;

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

    @DatabaseField
    private Map<String, Object> config = Maps.newHashMap();

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

    public Map<String, Object> getConfig() {
        return config;
    }

    public boolean isUnlocksLink() {
        return unlocksLink;
    }

    public void setUnlocksLink(boolean unlocksLink) {
        this.unlocksLink = unlocksLink;
    }
}
