/*
 * CheckpointManager.java
 * Leo Bogaert
 * Jun 4, 2025,
 * Manages and spawns checkpoints
 */
package Map.Checkpoints;

import Handlers.Vector2;

import java.awt.*;
import java.util.ArrayList;

public class CheckpointManager {

    private final ArrayList<Checkpoint> checkpoints;

    /**
     * Creates checkpoints
     */
    public CheckpointManager() {
        checkpoints = new ArrayList<>();

        checkpoints.add(new Checkpoint(new Vector2(615,470),4));
        checkpoints.add(new Checkpoint(new Vector2(1330,1070),10));
        checkpoints.add(new Checkpoint(new Vector2(2895,1045),8));
    }

    /**
     * Updates all checkpoints
     */
    public void update() {
        for (int i = 0; i < checkpoints.size(); i++) {
            checkpoints.get(i).update();
        }
    }

    /**
     * Draws all checkpoints
     * @param g2 graphics 2D object to draw on
     */
    public void draw(Graphics2D g2) {
        for (int i = 0; i < checkpoints.size(); i++) {
            checkpoints.get(i).draw(g2);
        }
    }
}
