package Map.Checkpoints;

import Handlers.Vector2;

import java.awt.*;
import java.util.ArrayList;

public class CheckpointManager {

    private ArrayList<Checkpoint> checkpoints;

    public CheckpointManager() {
        checkpoints = new ArrayList<>();

        checkpoints.add(new Checkpoint(new Vector2(615,470),4));
        checkpoints.add(new Checkpoint(new Vector2(2900,1045),8));
    }

    public void update() {
        for (int i = 0; i < checkpoints.size(); i++) {
            checkpoints.get(i).update();
        }
    }

    public void draw(Graphics2D g2) {
        for (int i = 0; i < checkpoints.size(); i++) {
            checkpoints.get(i).draw(g2);
        }
    }
}
