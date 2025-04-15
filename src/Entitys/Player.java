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
        // Calculate player's screen position
        double cameraX = position.x - GamePanel.screenWidth / 2;
        double cameraY = position.y - GamePanel.screenHeight / 2;

        cameraX = Math.max(GamePanel.tileMap.roomPosition.x, Math.min(cameraX, GamePanel.tileMap.roomPosition.x + GamePanel.tileMap.roomWidth - GamePanel.screenWidth));
        cameraY = Math.max(GamePanel.tileMap.roomPosition.y, Math.min(cameraY, GamePanel.tileMap.roomPosition.y + GamePanel.tileMap.roomHeight - GamePanel.screenHeight));

        double screenX = position.x - cameraX;
        double screenY = position.y - cameraY;

        g2.setColor(Color.red);
        g2.fillRect((int) screenX, (int) screenY,16, 32);
    }
}
