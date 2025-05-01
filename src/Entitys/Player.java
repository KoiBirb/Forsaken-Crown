package Entitys;

import Handlers.CollisionHandler;
import Handlers.ImageHandler;
import Handlers.Vector2;
import Main.Panels.GamePanel;

import java.awt.*;
import java.awt.geom.AffineTransform;

import static Main.Panels.GamePanel.keyI;

public class Player extends Entity {

    private boolean canMove;
    int spriteCounter = 0, spriteRow = 0, spriteCol = 0;

    final double terminalVelocity = 5; // Maximum falling speed
    final double jumpStrength = -4.5;

    private long jumpKeyPressStartTime = 0;

    public Player(Vector2 position, int width, int height) {
        super(position, new Vector2(0,0), width,
                height, 4, new Rectangle(30,8,18, 47),
                ImageHandler.loadImage("Assets/Images/Hero/SwordMaster/The SwordMaster/Sword Master Sprite Sheet 90x37.png"));

        canMove = true;
    }

    @Override
    public void update() {
        velocity.x = 0;

        // Check if the player is on the ground
        boolean onGround = CollisionHandler.onGround(this);

        if (onGround) {
            velocity.y = 0; // Reset vertical velocity when on the ground
        }

        isColliding = false;

        boolean continuousJumping;

        if (keyI.wPressed) {

            if (onGround && jumpKeyPressStartTime == 0)
                jumpKeyPressStartTime = System.currentTimeMillis();

            if (System.currentTimeMillis() - jumpKeyPressStartTime <= 200) {
                continuousJumping = true;
            } else {
                jumpKeyPressStartTime = 0;
                continuousJumping = false;
            }

        } else {
            jumpKeyPressStartTime = 0;
            continuousJumping = false;
        }

        // Handle jumping
        if (keyI.wPressed && continuousJumping) {
            velocity.y = jumpStrength; // Apply stronger upward force
            isColliding = false; // Temporarily disable ground collision
        }

        if (!onGround && !continuousJumping) {
            if (velocity.y < terminalVelocity) {
                velocity.y += 0.6;
            }
        }

        // Check for collisions after applying gravity
        CollisionHandler.checkTileCollision(this);

        // Apply velocity to position
        position.add(velocity);

        // Handle movement input
        if (keyI.aPressed || keyI.dPressed) {
            spriteRow = 3;

            if (canMove) {
                if (keyI.aPressed) {
                    direction = "left";
                    velocity.x = -speed;
                }
                if (keyI.dPressed) {
                    direction = "right";
                    velocity.x = speed;
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
        } else {
            spriteCol = 0;
            spriteCounter = 0;
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

        AffineTransform originalTransform = g2.getTransform();

        if (direction.contains("left")) {
            g2.scale(-1, 1);
            screenX = -screenX - solidArea.width * GamePanel.scale - 10; // Adjust for flipped coordinates
        }

        g2.setColor(Color.red);
        g2.drawImage(image,
                (int) screenX - 30, (int) screenY - 17,
                (int) (screenX - 30 + 90 * GamePanel.scale), (int) (screenY - 17 + 37 * GamePanel.scale),
                spriteCol * 90, spriteRow * 37,
                (spriteCol + 1) * 90, (spriteRow + 1) * 37, null);

//        g2.drawRect((int) screenX, (int) screenY, (int) (90 * GamePanel.scale), (int) (37 * GamePanel.scale));

        g2.setTransform(originalTransform);
    }
}
