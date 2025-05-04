/*
 * Player.java
 * Leo Bogaert
 * May 2, 2025,
 * Handles all player actions
 */
package Entitys;

import Attacks.MeleeAttacks.MeleeAttack;
import Attacks.MeleeAttacks.PlayerHeavyAttack;
import Attacks.MeleeAttacks.PlayerQuickAttack;
import Handlers.CollisionHandler;
import Handlers.ImageHandler;
import Handlers.Sound.MusicHandler;
import Handlers.Vector2;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;
import java.awt.geom.AffineTransform;

import static Main.Panels.GamePanel.keyI;

public class Player extends Entity {

    // Constants
    final double TERMINALVELOCITY = 4.5;
    final double JUMPSTRENGTH = -4;

    private boolean canMove, attacking, chain, jump, onGround;
    private int spriteCounter, spriteRow, spriteCol, maxSpriteCol, lastSpriteRow;

    // timers
    private long jumpKeyPressStartTime, lastQuickAttackTime, lastHeavyAttackTime, fallStartTime;

    MeleeAttack attack;

    /**
     * Constructor for the player
     * @param position Initial coordinates of the player
     * @param width width of player
     * @param height height of player
     */
    public Player(Vector2 position, int width, int height) {
        super(position, new Vector2(0,0), width,
                height, 3.2, new Rectangle(30,8,18, 47),
                ImageHandler.loadImage("Assets/Images/Hero/SwordMaster/The SwordMaster/Sword Master Sprite Sheet 90x37.png"), 10);
    }

    /**
     * Update method for the player
     * Handles collision and movement
     */

    @Override
    public void update() {
        velocity.x = 0;

        onGround = CollisionHandler.onGround(this);

        if (velocity.y > 0) {
            if (fallStartTime == 0)
                fallStartTime = System.currentTimeMillis();

            if (velocity.y > TERMINALVELOCITY)
                MusicHandler.falling();
        } else {
            fallStartTime = 0;
            MusicHandler.stopFalling();
        }



        if (onGround) {
            if (fallStartTime > 0 && System.currentTimeMillis() - fallStartTime > 400) {
                MusicHandler.landHard();
            } else if (fallStartTime > 0) {
                MusicHandler.land();
            }
            velocity.y = 0;
            fallStartTime = 0;
        }

        isColliding = false;

        boolean continuousJumping;

        // Jumping logic
        if (keyI.wPressed) {
            if (jump && onGround && jumpKeyPressStartTime == 0) {
                jumpKeyPressStartTime = System.currentTimeMillis();
                jump = false;
                MusicHandler.jump();
            }

            if (System.currentTimeMillis() - jumpKeyPressStartTime <= 200) {
                continuousJumping = true;
            } else {
                jumpKeyPressStartTime = 0;
                continuousJumping = false;
            }
        } else {
            jumpKeyPressStartTime = 0;
            continuousJumping = false;
            jump = true;
        }

        // Jump animation
        if (keyI.wPressed && continuousJumping) {
            velocity.y = JUMPSTRENGTH;
            isColliding = false;

            if (!attacking) {
                spriteRow = 13;
                maxSpriteCol = 2;
            }
        } else if (!onGround && (System.currentTimeMillis() - jumpKeyPressStartTime <= 80 || spriteCol == maxSpriteCol) && !attacking) {
            spriteRow = 14;
            maxSpriteCol = 3;
        } else if (!onGround && velocity.y > 3 && !attacking) {
            spriteRow = 15;
            maxSpriteCol = 2;
        }

        if (!onGround && !continuousJumping) {
            if (velocity.y < TERMINALVELOCITY) {
                velocity.y += 0.4;
            } else {
                TiledMap.cameraShake(1,6);
            }
        }

        if (keyI.uPressed) {
            long currentTime = System.currentTimeMillis();

            if (!attacking) {
                if (!chain) {
                    // Normal attack
                    if (currentTime - lastQuickAttackTime >= PlayerQuickAttack.getCooldown()) {
                        spriteRow = 8;
                        maxSpriteCol = 4;
                        attack = new PlayerQuickAttack(this, false);
                        MusicHandler.hit();
                        chain = true; // Enable chain attack
                        lastQuickAttackTime = currentTime;
                    }
                } else {
                    // Chain attack
                    spriteRow = 7;
                    maxSpriteCol = 6;
                    attack = new PlayerQuickAttack(this, true);
                    MusicHandler.hit();
                    chain = false; // Reset chain flag
                    lastQuickAttackTime = currentTime;
                }
            }
        }

        if (keyI.jPressed && !attacking) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastHeavyAttackTime >= PlayerHeavyAttack.getCooldown()) {
                spriteRow = 9;
                maxSpriteCol = 4;
                attack = new PlayerHeavyAttack(this);
                MusicHandler.heavyAttack();
                currentHealth--;
                lastHeavyAttackTime = currentTime;
            }
        }

        CollisionHandler.checkTileCollision(this);

        position.add(velocity);

        if ((keyI.aPressed || keyI.dPressed)) {
            if (onGround && !continuousJumping && !attacking) {
                maxSpriteCol = 7;
                spriteRow = 3;
            }

            speed = (attacking) ? 2.8 : 3;

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

            // Play footsteps when moving
            if (onGround)
                MusicHandler.footsteps();
             else
                MusicHandler.stopFootsteps();

        } else {
            MusicHandler.stopFootsteps();

            if (onGround && !continuousJumping && !attacking) {
                spriteRow = 1;
                maxSpriteCol = 8;
            }
        }

        spriteCounter++;
        if (spriteCounter > 4) {
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

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

    public boolean isOnGround() {
        return onGround;
    }


    /**
     * Draws the player on the screen
     * @param g2 Graphics2D object to draw on
     */
    @Override
    public void draw(Graphics2D g2) {

        Vector2 cameraPos = GamePanel.tileMap.returnCameraPos();

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
