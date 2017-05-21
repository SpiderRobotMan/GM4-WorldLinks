package me.theminecoder.minecraft.worldlinks.link;

public enum LinkType {

    ABSOLUTE;

    /**
     * Gets a link type by its name from the config.
     *
     * @param name The name
     * @return The link type, or null if not found
     */
    public static LinkType getByConfigName(String name) {
        for (LinkType type : values()) {
            if (type.toString().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

}
