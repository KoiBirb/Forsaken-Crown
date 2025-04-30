package Entitys;

import Handlers.CollisionHandler;
import Handlers.ImageHandler;
import Handlers.Vector2;
import Main.Panels.GamePanel;

import java.awt.*;

import static Main.Panels.GamePanel.keyI;

public class Player extends Entity {

    private boolean canMove;
    int spriteCounter = 0, spriteRow = 0, spriteCol = 0;

    public Player(Vector2 position, int width, int height) {
        super(position, new Vector2(0,0), width,
                height, 4, new Rectangle(30,8,18, 47),
                ImageHandler.loadImage("Assets/Images/Hero/SwordMaster/The SwordMaster/Sword Master Sprite Sheet 90x37.png"));

        canMove = true;
    }

    @Override
    public void update() {
        if (keyI.wPressed || keyI.sPressed || keyI.aPressed || keyI.dPressed) {
            spriteRow = 3;

            if (canMove) {
                velocity = new Vector2(0, 0);

                if (keyI.wPressed && keyI.aPressed) {
                    direction = "up-left";
                    velocity = new Vector2(speed, directionToRad.get(direction), false);
                } else if (keyI.wPressed && keyI.dPressed) {
                    direction = "up-right";
                    velocity = new Vector2(speed, directionToRad.get(direction), false);
                } else if (keyI.sPressed && keyI.aPressed) {
                    direction = "down-left";
                    velocity = new Vector2(speed, directionToRad.get(direction), false);
                } else if (keyI.sPressed && keyI.dPressed) {
                    direction = "down-right";
                    velocity = new Vector2(speed, directionToRad.get(direction), false);
                } else {
                    // Handle single direction movement
                    if (keyI.wPressed) {
                        direction = "up";
                        velocity = new Vector2(0, -speed);
                    }
                    if (keyI.sPressed) {
                        direction = "down";
                        velocity = new Vector2(0, speed);
                    }
                    if (keyI.aPressed) {
                        direction = "left";
                        velocity = new Vector2(-speed, 0);
                    }
                    if (keyI.dPressed) {
                        direction = "right";
                        velocity = new Vector2(speed, 0);
                    }
                }

                isColliding = false;
                CollisionHandler.checkTileCollision(this);

                if (!isColliding) {
                    position.x += velocity.x;
                    position.y += velocity.y;
                }
            }

            spriteCounter++;
            if (spriteCounter > 2) {
                spriteCounter = 0;
                spriteCol++;
                if (spriteCol > 7) {
                    spriteCol = 0;
                }
            }

            super.update();
        }
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
        g2.drawImage(image,
                (int) screenX - 30, (int) screenY - 17,
                (int) (screenX - 30 + 90 * GamePanel.scale), (int) (screenY - 17 + 37 * GamePanel.scale),
                spriteCol * 90, spriteRow * 37,
                (spriteCol + 1) * 90, (spriteRow + 1) * 37, null);

//        g2.drawRect((int) screenX, (int) screenY, (int) (90 * GamePanel.scale), (int) (37 * GamePanel.scale));
    }
}
