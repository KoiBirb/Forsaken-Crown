/*
 * Player.java
 * Leo Bogaert
 * May 1, 2025,
 * Handles all player actions
 */
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
    int spriteCounter = 0, spriteRow = 0, spriteCol = 0, maxSpriteCol = 0, lastSpriteRow = 0;

    final double terminalVelocity = 5; // Maximum falling speed
    final double jumpStrength = -4.5;

    private long jumpKeyPressStartTime = 0;

    /**
     * Constructor for the player
     * @param position Initial coordinates of the player
     * @param width width of player
     * @param height height of player
     */
    public Player(Vector2 position, int width, int height) {
        super(position, new Vector2(0,0), width,
                height, 4, new Rectangle(30,8,18, 47),
                ImageHandler.loadImage("Assets/Images/Hero/SwordMaster/The SwordMaster/Sword Master Sprite Sheet 90x37.png"));

        canMove = true;
    }

    /**
     * Update method for the player
     * Handles collision and movement
     */
    @Override
    public void update() {

        velocity.x = 0;

        boolean onGround = CollisionHandler.onGround(this);

        if (onGround)
            velocity.y = 0;

        isColliding = false;

        boolean continuousJumping;

        // jumping
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

        //jump animation
        if (keyI.wPressed && continuousJumping) {
            velocity.y = jumpStrength;
            isColliding = false;

            spriteRow = 13;
            maxSpriteCol = 2;
        } else if (!onGround && (System.currentTimeMillis() - jumpKeyPressStartTime <= 60 || spriteCol == maxSpriteCol)) {
            spriteRow = 14;
            maxSpriteCol = 3;
        } else if (!onGround && velocity.y > 4) {
            spriteRow = 15;
            maxSpriteCol = 2;
        }

        if (!onGround && !continuousJumping) {
            if (velocity.y < terminalVelocity) {
                velocity.y += 0.6;
            }
        }

        CollisionHandler.checkTileCollision(this);

        position.add(velocity);

        if (keyI.aPressed || keyI.dPressed) {
            if (onGround && !continuousJumping) {
                maxSpriteCol = 7;
                spriteRow = 3;
            }

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
        } else if (onGround && !continuousJumping) {
            spriteRow = 1;
            maxSpriteCol = 8;
        }

        spriteCounter++;
        if (spriteCounter > 3) {
            spriteCounter = 0;
            spriteCol++;
            if (spriteCol >= maxSpriteCol) {
                spriteCol = 0;
            }
        }

        super.update();

        if (lastSpriteRow != spriteRow) {
            spriteCol = 0;
            spriteCounter = 0;
        }

        lastSpriteRow = spriteRow;
    }

    /**
     * Sets the players ability to move
     * @param canMove true to let player move
     */
    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }


    /**
     * Draws the player on the screen
     * @param g2 Graphics2D object to draw on
     */
    @Override
    public void draw(Graphics2D g2) {

        Vector2 cameraPos = GamePanel.tileMap.getCameraPos();

        double screenX = position.x - cameraPos.x;
        double screenY = position.y - cameraPos.y;

        AffineTransform originalTransform = g2.getTransform();

        // flip image
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
