package co.gm4.worldlink.objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by MatrixTunnel on 9/19/2017.
 */
@NoArgsConstructor @Getter @Setter
public class Link {

    private String name;
    private String unlockAdvancementKey;

    private List<String> displayAdvancements;
    private String displayLocation;
    private String offhandItem;

    private boolean zoomOnClick, blindnessOnClick, portalSoundOnClick;

    private String displayType;
    private double displayOffsetX, displayOffsetY, displayOffsetZ, displaySpeed;
    private int displayCount;

    private String hoverType;
    private double hoverOffsetX, hoverOffsetY, hoverOffsetZ;
    private int hoverCount, hoverSpeed;

    private LinkLocation targetLocation;
    private String teleportType;
    private boolean resetRespawnLocation;

    private List<String> beforeCommands, duringCommands, afterCommands;

}
