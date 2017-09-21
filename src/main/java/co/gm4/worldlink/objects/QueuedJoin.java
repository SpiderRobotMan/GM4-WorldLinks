package co.gm4.worldlink.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/**
 * Created by MatrixTunnel on 9/20/2017.
 */
@AllArgsConstructor @Getter
public class QueuedJoin {

    private UUID uuid;
    private LinkPlayer linkPlayer;
    private long time;

}
