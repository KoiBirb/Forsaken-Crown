package Entitys;

import Handlers.Vector2;
import Main.Panels.GamePanel;

import java.awt.*;

import static Main.Panels.GamePanel.keyI;

public class Player extends Entity {

    public Vector2 screenPosition;

    public Player(Vector2 position, int width, int height) {
        super(position, new Vector2(0,0), width, height);

        screenPosition = new Vector2(GamePanel.screenWidth /2 - 16, GamePanel.screenHeight - 128);
    }

    @Override
    public void update() {
        super.update();

        if (keyI.wPressed) {
            position.y -= 5;
        }

        if (keyI.sPressed) {
            position.y += 5;
        }

        if (keyI.aPressed) {
            position.x -= 5;
        }

        if (keyI.dPressed) {
            position.x += 5;
        }
    }

    @Override
    public void draw(Graphics2D g2) {

        Vector2 cameraPos = GamePanel.tileMap.getCameraPos();

        double screenX = position.x - cameraPos.x;
        double screenY = position.y - cameraPos.y;

        g2.setColor(Color.red);
        g2.fillRect((int) screenX, (int) screenY,16, 32);
    }
}
