package Entitys;

import Handlers.Vector2;
import Main.KeyInput;
import Main.Panels.GamePanel;

import java.awt.*;

public class Player extends Entity {

    public Vector2 screenPosition;
    private KeyInput keyI;

    public Player(Vector2 position, int width, int height, KeyInput keyI) {
        super(position, new Vector2(0,0), width, height);
        this.keyI = keyI;

        screenPosition = new Vector2(GamePanel.screenWidth /2 - 16, GamePanel.screenHeight - 64);
    }

    @Override
    public void update() {
        super.update();

        if (keyI.wPressed) {
            position.y -= 2;
        }

        if (keyI.sPressed) {
            position.y += 2;
        }

        if (keyI.aPressed) {
            position.x -= 2;
        }

        if (keyI.dPressed) {
            position.x += 2;
        }
    }

    @Override
    public void draw(Graphics2D g2) {

        g2.drawRect((int) screenPosition.x, (int) screenPosition.y, 16, 32);
    }
}
