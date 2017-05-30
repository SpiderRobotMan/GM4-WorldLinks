package me.theminecoder.minecraft.worldlinks.objects;

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
    private Map<String, Object> config;

    @DatabaseField
    private boolean unlocksLink = false;

    LinkCondition() {
    }

}
