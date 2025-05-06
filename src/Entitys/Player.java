/*
 * Player.java
 * Leo Bogaert
 * May 6, 2025,
 * Handles all player actions
 */
package Entitys;

import Attacks.MeleeAttacks.*;
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

    private boolean canMove, attacking, chain, healing, dashing;
    private int spriteCounter, spriteRow, spriteCol, maxSpriteCol, lastSpriteRow;

    // timers
    private long jumpKeyPressStartTime, lastQuickAttackTime, lastHeavyAttackTime,
            fallStartTime, healStartTime, dashStartTime, lastDashTime;

    MeleeAttack attack;

    /**
     * Constructor for the player
     * @param position Initial coordinates of the player
     * @param width width of player
     * @param height height of player
     */
    public Player(Vector2 position, int width, int height) {
        super(position, new Vector2(0,0), width,
                height, 6.4, new Rectangle(30,8,18, 47),
                ImageHandler.loadImage("Assets/Images/Hero/SwordMaster/The SwordMaster/Sword Master Sprite Sheet 90x37.png"), 10, 10);
    }

    /**
     * Update method for the player
     * Handles collision and movement
     */

    @Override
    public void update() {
        if (!dashing)
            velocity.x = 0;

        onGround = CollisionHandler.onGround(this);

        // falling

        if (velocity.y > 0) {
            if (fallStartTime == 0)
                fallStartTime = System.currentTimeMillis();

            if (velocity.y > 9)
                MusicHandler.falling();
        } else {
            fallStartTime = 0;
            MusicHandler.stopFalling();
        }


        // landing
        if (onGround) {
            if (fallStartTime > 0 && System.currentTimeMillis() - fallStartTime > 400) {
                MusicHandler.landHard();
            } else if (fallStartTime > 0) {
                MusicHandler.land();
            }
            velocity.y = 0;
            fallStartTime = 0;
        }

        boolean continuousJumping;

        // Jumping
        if (keyI.wPressed && canMove && !healing) {
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

        if (keyI.wPressed && continuousJumping && !keyI.iPressed) {
            velocity.y = -8;
            isColliding = false;

            if (!attacking) {
                spriteRow = 13;
                maxSpriteCol = 2;
            }
        } else if (!onGround && (System.currentTimeMillis() - jumpKeyPressStartTime <= 80 || spriteCol == maxSpriteCol) && !attacking && !dashing) {
            spriteRow = 14;
            maxSpriteCol = 3;
        } else if (!onGround && velocity.y > 6 && !attacking && !dashing) {
            spriteRow = 15;
            maxSpriteCol = 2;
        }

        if (!onGround && !continuousJumping) {
            if (velocity.y < 9) {
                velocity.y += 0.8;
            } else {
                TiledMap.cameraShake(1,6);
            }
        }

        // Dashing
        if (keyI.kPressed && !dashing && System.currentTimeMillis() - lastDashTime >= 1000 && currentMana > 0) {
            dashing = true;
            dashStartTime = System.currentTimeMillis();
            lastDashTime = dashStartTime;
            velocity.x = direction.equals("right") ? 30 : -30;

            MusicHandler.dash();
            TiledMap.cameraShake(3,1);
            currentMana--;
            spriteRow = 12;
            maxSpriteCol = 5;
        }

        if (dashing) {

            long elapsedTime = System.currentTimeMillis() - dashStartTime;
            double deceleration = 0.1;
            TiledMap.cameraShake(1,6);

            if (elapsedTime <= 300) { // Dash duration
                velocity.x *= (1 - deceleration);
                if (Math.abs(velocity.x) < 0.5) {
                    velocity.x = 0;
                }
            }
        }

        // Attacking
        if (keyI.uPressed && !keyI.iPressed) {
            long currentTime = System.currentTimeMillis();
            if (!attacking) {
                if (!dashing) {
                    if (!chain) {
                        // Normal attack
                        if (currentTime - lastQuickAttackTime >= PlayerQuickAttack.getCooldown()) {
                            spriteRow = 8;
                            maxSpriteCol = 4;
                            attack = new PlayerQuickAttack(this, false);
                            MusicHandler.hit();
                            chain = true;
                            lastQuickAttackTime = currentTime;
                            currentMana++;
                        }
                    } else {
                        spriteRow = 7;
                        maxSpriteCol = 6;
                        attack = new PlayerQuickAttack(this, true);
                        MusicHandler.hit();
                        chain = false;
                        lastQuickAttackTime = currentTime;
                    }
                } else {
                    spriteRow = 10;
                    maxSpriteCol = 5;
                    dashing = false;
                    attack = new PlayerDashSwingAttack(this);
                    MusicHandler.dashSwingAttack();
                    lastQuickAttackTime = currentTime;
                }
            }
        }

        if (keyI.jPressed && !attacking && !keyI.iPressed) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastHeavyAttackTime >= PlayerHeavyAttack.getCooldown()) {
                if (dashing) {
                    attack = new PlayerDashHeavyAttack(this);
                    MusicHandler.dashHeavyAttack();
                    dashing = false;
                    spriteRow = 6;
                    maxSpriteCol = 6;
                } else {
                    attack = new PlayerHeavyAttack(this);
                    MusicHandler.heavyAttack();
                    spriteRow = 9;
                    maxSpriteCol = 4;
                }
                currentHealth--;
                lastHeavyAttackTime = currentTime;
            }
        }

        // Healing
        if (keyI.iPressed && onGround && !healing) {
            if (healStartTime == 0) {
                healStartTime = System.currentTimeMillis();
                MusicHandler.healCharge();
            }

            long elapsedTime = System.currentTimeMillis() - healStartTime;

            TiledMap.cameraShake((int) (1.0 + Math.min(elapsedTime / 1000.0, 2.0)),1);
        } else {
            if (healStartTime > 0) {

                spriteRow = 11;
                maxSpriteCol = 5;

                healing = true;

                int healAmount = (int) ((System.currentTimeMillis() - healStartTime) / 750);
                healAmount = Math.min(healAmount, currentMana);
                healAmount = Math.min(healAmount, maxHealth - currentHealth);


                currentHealth += healAmount;
                currentMana -= healAmount;

                TiledMap.cameraShake(healAmount,1);

                MusicHandler.stopHealCharge();
                MusicHandler.heal();
            }
            healStartTime = 0;
            MusicHandler.stopHealCharge();
        }

        isColliding = false;

        CollisionHandler.checkTileCollision(this);

        // movement
        if ((keyI.aPressed || keyI.dPressed) && !(keyI.iPressed && onGround) && !healing && !dashing) {
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

            if (onGround && canMove)
                MusicHandler.footsteps();
             else
                MusicHandler.stopFootsteps();

        } else {
            MusicHandler.stopFootsteps();

            if (onGround && !continuousJumping && !attacking && !healing && !dashing) {
                spriteRow = 1;
                maxSpriteCol = 8;
            }
        }

        // update sprite
        spriteCounter++;
        if (spriteCounter > 5) {
            spriteCounter = 0;
            spriteCol++;
            if (spriteCol >= maxSpriteCol) {
                if (spriteRow == 1 || spriteCol == 7) {
                    spriteCol = 0;
                }
                healing = false;
                dashing = false;
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
     * Sets the players attacking status
     * @param attacking true if attacking, false otherwise
     */
    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
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

        // debug draw image area
//        g2.drawRect((int) screenX, (int) screenY, (int) (90 * GamePanel.scale), (int) (37 * GamePanel.scale));

        g2.setTransform(originalTransform);
    }
}
