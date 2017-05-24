package me.theminecoder.minecraft.worldlinks.objects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author theminecoder
 */
@DatabaseTable(tableName = "link_conditions")
public class LinkCondition {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true)
    private Link link;

    LinkCondition(){
    }

}
