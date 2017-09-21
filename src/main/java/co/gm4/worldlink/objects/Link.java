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

    private boolean zoomOnClick;
    private String soundOnClick;


    private String displayType, displayOffset;
    private int displayCount, displaySpeed;

    private String hoverType, hoverOffset;
    private int hoverCount, hoverSpeed;

    private LinkLocation targetLocation;
    private String teleportType;

    private List<String> beforeCommands, duringCommands, afterCommands;

}
