package me.theminecoder.minecraft.worldlinks.link;

import me.theminecoder.minecraft.worldlinks.player.LinkPlayer;
import me.theminecoder.minecraft.worldlinks.utils.ParticleUtils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

public class Link {

    private String name;
    private String display;
    private String server;
    private String particle;
    private float[] particleData;
    private LinkLocation particleLoc;
    private LinkLocation locData;
    private LinkType type;

    /**
     * Constructs a new link object from an element in the config.
     *
     * @param config The configuration section
     * @throws InvalidConfigurationException Thrown if the config is invalid
     */
    public Link(ConfigurationSection config) throws InvalidConfigurationException {
        this.name = config.getString("name", null);
        this.display = config.getString("display", null);
        this.server = config.getString("server", null);
        this.particle = config.getString("particle.type", null);
        this.type = LinkType.getByConfigName(config.getString("type", ""));

        //Check the name provided is valid.
        if (this.name == null) {
            throw new InvalidConfigurationException("The 'name' parameter was not provided.");
        }

        //Check the display name provided is valid.
        if (this.display == null) {
            throw new InvalidConfigurationException("The 'display' parameter was not provided.");
        }

        //Check the server provided is valid.
        if (this.server == null) {
            throw new InvalidConfigurationException("The 'server' parameter was not provided.");
        }

        //Check the particle data is valid.
        if (this.particle == null || !config.isConfigurationSection("particle.data")) {
            throw new InvalidConfigurationException("Invalid particle data was provided.");
        }

        float r = (float) config.getDouble("particle.data.r", 0.01);
        float g = (float) config.getDouble("particle.data.g", 0.01);
        float b = (float) config.getDouble("particle.data.b", 0.01);

        if (r <= 0) r = 0.01F;
        if (g <= 0) g = 0.01F;
        if (b <= 0) b = 0.01F;

        this.particleData = new float[]{r, g, b};

        //Check the link type provided is valid.
        if (this.type == null) {
            throw new InvalidConfigurationException("Invalid link type was provided.");
        }

        //We save the location with the default world to make bukkit happy however we will ignore this.
        this.particleLoc = new LinkLocation(config.getConfigurationSection("particle.location"));

        if (this.type == LinkType.ABSOLUTE) {
            this.locData = new LinkLocation(config.getConfigurationSection("location"));
        }
    }

    /**
     * Gets the unique name of the link.
     *
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the display name of the link.
     *
     * @return The display name
     */
    public String getDisplayName() {
        return display;
    }

    /**
     * Gets the server name that this link transports to. Must be the
     * same as in the BungeeCord configuration.
     *
     * @return The server name
     */
    public String getServer() {
        return server;
    }

    /**
     * Gets the type of the link.
     *
     * @return The type
     */
    public LinkType getType() {
        return type;
    }

    /**
     * Gets the location data assosciated with this link.
     *
     * @return The location data
     */
    public LinkLocation getLocationData() {
        return locData;
    }

    /**
     * Calculates the position to display the link relative to a fixed
     * location.
     *
     * @param loc The location
     * @return The location
     */
    public Location calculatePositionRelativeTo(Location loc) {
        Location pLoc = particleLoc.getBukkitLocation();

        double x = pLoc.getBlockX() + 0.5D;
        double y = pLoc.getBlockY() + 0.5D;
        double z = pLoc.getBlockZ() + 0.5D;

        //Add the location to the player's existing location.
        return loc.getBlock().getLocation().clone().add(x, y, z);
    }

    /**
     * Displays the link particles to a player.
     *
     * @param player The player
     */
    public void displayToPlayer(LinkPlayer player) {
        ParticleUtils.sendParticle(
                player.getBukkitPlayer(),
                this.particle,
                calculatePositionRelativeTo(player.getBukkitPlayer().getEyeLocation()),
                this.particleData[0], this.particleData[1], this.particleData[2],
                1,
                0
        );
    }

}
