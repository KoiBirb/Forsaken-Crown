package Entitys;

import Handlers.CollisionHandler;
import Handlers.Vector2;
import Main.Panels.GamePanel;

import java.awt.*;

import static Main.Panels.GamePanel.keyI;

public class Player extends Entity {

    private boolean canMove;

    public Player(Vector2 position, int width, int height) {
        super(position, new Vector2(0,0), width, height, 0, 5, 10);

        canMove = true;
    }

    @Override
    public void update() {

//        CollisionHandler.checkTileCollision(this);

        if (canMove && !isColliding) {
            if (keyI.wPressed) {
                direction = "up";
                position.y -= speed;
            }
            if (keyI.sPressed) {
                direction = "down";
                position.y += speed;
            }
            if (keyI.aPressed){
                direction = "left";
                position.x -= speed;
            }
            if (keyI.dPressed) {
                direction = "right";
                position.x += speed;
            }
        }

        super.update();
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }


    @Override
    public void draw(Graphics2D g2) {

        Vector2 cameraPos = GamePanel.tileMap.getCameraPos();

        double screenX = position.x - cameraPos.x;
        double screenY = position.y - cameraPos.y;

        g2.setColor(Color.red);
        g2.fillRect((int) screenX, (int) screenY,width, height);

        g2.setColor(Color.blue);
        g2.fillRect(solidArea.x, solidArea.y, solidArea.width, solidArea.height);
    }
}
