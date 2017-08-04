package me.theminecoder.minecraft.worldlinks.objects;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * @author theminecoder
 */
@DatabaseTable(tableName = "link_condition_config_values")
public class LinkConditionConfigValue {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true)
    private LinkCondition linkCondition;

    @DatabaseField
    private String configId;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private Serializable value;

    LinkConditionConfigValue(){}

    LinkConditionConfigValue(LinkCondition linkCondition, String configId, Serializable value) {
        this.linkCondition = linkCondition;
        this.configId = configId;
        this.value = value;
    }

    public String getConfigId() {
        return configId;
    }

    public Object value() {
        return value;
    }

    public Object value(Object defaultValue) {
        return value != null ? value : defaultValue;
    }

    public <T> T value(Class<T> clazz) {
        return value != null ? clazz.cast(value) : null;
    }

    public <T> T value(Class<T> clazz, T defaultValue) {
        return value != null ? clazz.cast(value) : defaultValue;
    }

    public String asString() {
        return (String) value;
    }

    public String asString(String defaultValue) {
        return value != null ? (String) value : defaultValue;
    }

    public int asInt() {
        return (int) value;
    }

    public int asInt(int defaultValue) {
        return value != null ? (int) value : defaultValue;
    }

    public float asFloat() {
        return (float) value;
    }

    public float asFloat(float defaultValue) {
        return value != null ? (float) value : defaultValue;
    }

    public double asDouble() {
        return (double) value;
    }

    public double asDouble(double defaultValue) {
        return value != null ? (double) value : defaultValue;
    }

    public long asLong() {
        return (long) value;
    }

    public long asLong(long defaultValue) {
        return value != null ? (long) value : defaultValue;
    }

    public short asShort() {
        return (short) value;
    }

    public short asShort(short defaultValue) {
        return value != null ? (short) value : defaultValue;
    }

    public byte asByte() {
        return (byte) value;
    }

    public byte asByte(byte defaultValue) {
        return value != null ? (byte) value : defaultValue;
    }

    public boolean asBoolean() {
        return (boolean) value;
    }

    public boolean asBoolean(boolean defaultValue) {
        return value != null ? (boolean) value : defaultValue;
    }

}
