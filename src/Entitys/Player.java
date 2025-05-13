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

    private boolean attacking, chain, healing, dashing, spawning, death, damaged, knockedBack, continuousJumping;
    private int spriteCounter, spriteRow, spriteCol, maxSpriteCol, lastSpriteRow;

    // timers
    private long jumpKeyPressStartTime, lastQuickAttackTime, lastHeavyAttackTime,
            fallStartTime, healStartTime, dashStartTime, lastDashTime, deathTime;

    private Vector2 spawnPosition;

    /**
     * Constructor for the player
     * @param position Initial coordinates of the player
     */
    public Player(Vector2 position) {
        super(position, new Vector2(0,0), 90,37,
                6.4, new Rectangle(30,8,18, 47),
                ImageHandler.loadImage("Assets/Images/Hero/SwordMaster/The SwordMaster/Sword Master Sprite Sheet 90x37.png"), 10, 10);

        spawnPosition = new Vector2(position.x, position.y);
        spawning = true;
    }

    /**
     * Update method for the player
     * Handles collision and movement
     */

    @Override
    public void update() {

        if (currentHealth > 0) {

            // spawning
            if (spawning) {
                velocity.x = 0;
                velocity.y = 0;

                spriteRow = 17;
                maxSpriteCol = 3;

                MusicHandler.spawn();
            } else {

                if (!dashing)
                    velocity.x = 0;

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

                // Jumping
                if (keyI.wPressed && !healing && !damaged) {
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

                if (keyI.wPressed && continuousJumping && !keyI.iPressed && !damaged) {
                    velocity.y = -8;
                    isColliding = false;

                    if (!attacking && !dashing) {
                        spriteRow = 13;
                        maxSpriteCol = 2;
                    }
                } else if (!onGround && spriteCol == maxSpriteCol && !attacking && !dashing && !damaged && velocity.y < 6) {
                    spriteRow = 14;
                    maxSpriteCol = 3;
                } else if (!onGround && velocity.y > 6 && !attacking && !dashing && !damaged) {
                    spriteRow = 15;
                    maxSpriteCol = 2;
                }

                // Dashing
                if (keyI.kPressed && !dashing && System.currentTimeMillis() - lastDashTime >= 1000 && currentMana > 0 && !damaged && !spawning) {
                    dashing = true;
                    dashStartTime = System.currentTimeMillis();
                    lastDashTime = dashStartTime;
                    velocity.x = direction.contains("right") ? 30 : -30;

                    MusicHandler.dash();
                    TiledMap.cameraShake(3, 1);
                    currentMana--;
                    spriteRow = 12;
                    maxSpriteCol = 5;
                }

                if (dashing) {

                    long elapsedTime = System.currentTimeMillis() - dashStartTime;
                    double deceleration = 0.1;
                    TiledMap.cameraShake(1, 6);

                    if (elapsedTime <= 300) { // Dash duration
                        velocity.x *= (1 - deceleration);
                        if (Math.abs(velocity.x) < 0.5) {
                            velocity.x = 0;
                        }
                    }
                }

                // Attacking
                if (keyI.uPressed && !keyI.iPressed && !damaged) {
                    long currentTime = System.currentTimeMillis();
                    if (!attacking) {
                        if (!dashing) {
                            if (!chain) {
                                // Normal attack
                                if (currentTime - lastQuickAttackTime >= PlayerQuickAttack.getCooldown()) {
                                    spriteRow = 8;
                                    maxSpriteCol = 4;
                                    new PlayerQuickAttack(this, false);
                                    MusicHandler.hit();
                                    chain = true;
                                    lastQuickAttackTime = currentTime;
                                    currentMana += 2;
                                }
                            } else {
                                spriteRow = 7;
                                maxSpriteCol = 6;
                                new PlayerQuickAttack(this, true);
                                MusicHandler.hit();
                                chain = false;
                                lastQuickAttackTime = currentTime;
                            }
                        } else {
                            spriteRow = 10;
                            maxSpriteCol = 5;
                            dashing = false;
                            new PlayerDashSwingAttack(this);
                            MusicHandler.dashSwingAttack();
                            lastQuickAttackTime = currentTime;
                        }
                    }
                }

                if (keyI.jPressed && !attacking && !keyI.iPressed && !damaged) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastHeavyAttackTime >= PlayerHeavyAttack.getCooldown()) {
                        if (dashing) {
                            new PlayerDashHeavyAttack(this);
                            MusicHandler.dashHeavyAttack();
                            dashing = false;
                            spriteRow = 6;
                            maxSpriteCol = 6;
                        } else {
                            new PlayerHeavyAttack(this);
                            MusicHandler.heavyAttack();
                            spriteRow = 9;
                            maxSpriteCol = 4;
                        }
                        lastHeavyAttackTime = currentTime;
                    }
                }

                // Healing
                if (keyI.iPressed && onGround && !healing && !damaged) {
                    if (healStartTime == 0) {
                        healStartTime = System.currentTimeMillis();
                        MusicHandler.healCharge();
                    }

                    long elapsedTime = System.currentTimeMillis() - healStartTime;

                    TiledMap.cameraShake((int) (1.0 + Math.min(elapsedTime / 1000.0, 2.0)), 1);
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

                        TiledMap.cameraShake(healAmount, 1);

                        MusicHandler.stopHealCharge();
                        MusicHandler.heal();
                    }
                    healStartTime = 0;
                    MusicHandler.stopHealCharge();
                }

                // movement
                if ((keyI.aPressed || keyI.dPressed) && !(keyI.iPressed && onGround) && !healing && !dashing && !damaged) {
                    if (onGround && !continuousJumping && !attacking) {
                        maxSpriteCol = 7;
                        spriteRow = 3;
                    }

                    speed = (attacking) ? 2.8 : 3;

                    if (keyI.aPressed && !knockedBack) {
                        velocity.x = -speed;
                    }
                    if (keyI.dPressed && !knockedBack) {
                        velocity.x = speed;
                    }

                    if (onGround)
                        MusicHandler.footsteps();
                    else
                        MusicHandler.stopFootsteps();

                } else {
                    MusicHandler.stopFootsteps();

                    if (onGround && !continuousJumping && !attacking && !healing && !dashing && !spawning && !damaged) {
                        spriteRow = 1;
                        maxSpriteCol = 8;
                    }
                }
            }

            if (keyI.oPressed) {
                hit(1);
            }

        } else if (!death) {

            death = true;

            deathTime = System.currentTimeMillis();

            MusicHandler.playerDeath();

            MusicHandler.stopFootsteps();
            MusicHandler.stopFalling();
            MusicHandler.stopHealCharge();

            spriteRow = 26;
            maxSpriteCol = 5;

           velocity.setLength(0);
        }

        determineDirection();

        isColliding = false;
        CollisionHandler.checkTileCollision(this);

        if (!onGround && !continuousJumping && !spawning) {
            if (velocity.y < 9) {
                velocity.y += 0.8;
            } else {
                TiledMap.cameraShake(1, 6);
            }
        }

        // update sprite
        if (!GamePanel.fading || !spawning) {

            spriteCounter++;
            if (spriteCounter > 5) {
                spriteCounter = 0;
                spriteCol++;
                if (spriteCol > maxSpriteCol) {
                    if (spriteRow == 1 || spriteRow == 3 || spriteRow == 15) {
                        spriteCol = 0;
                    }

                    knockedBack = false;

                    if (healing) {
                        healing = false;
                        spriteCol = maxSpriteCol;
                    }

                    if (dashing){
                        dashing = false;
                        spriteCol = maxSpriteCol;
                    }

                    if (damaged) {
                        damaged = false;
                        direction = (direction.contains("left")) ? "right" : "left";
                        spriteCol = 0;
                    }

                    if (spawning) {
                        MusicHandler.setSpawnPlaying(false);
                        spawning = false;
                    }

                    if (death){
                        spriteCol = 5;
                    }
                }
            }
        }

        if (death && System.currentTimeMillis() - deathTime >= 5000)
            resetPlayer();

        super.update();

        if (lastSpriteRow != spriteRow) {
            spriteCol = 0;
            spriteCounter = 0;
        }

        lastSpriteRow = spriteRow;
    }

    private void determineDirection(){
        if (velocity.x != 0) {
            if (velocity.x > 0) {
                if (velocity.y < 0) {
                    direction = "up-right";
                } else if (velocity.y > 0) {
                    direction = "down-right";
                } else {
                    direction = "right";
                }
            } else {
                if (velocity.y < 0) {
                    direction = "up-left";
                } else if (velocity.y > 0) {
                    direction = "down-left";
                } else {
                    direction = "left";
                }
            }
        } else if (velocity.y < 0) {
            if (direction.contains("left")) {
                direction = "up-left";
            } else if (direction.contains("right")) {
                direction = "up-right";
            } else {
                direction = "up";
            }
        } else if (velocity.y > 0) {
            if (direction.contains("left")) {
                direction = "down-left";
            } else if (direction.contains("right")) {
                direction = "down-right";
            } else {
                direction = "down";
            }
        }
    }

    /**
     * Resets the player to its initial state
     */
    private void resetPlayer() {
        MusicHandler.setDeathPlaying(false);
        direction = "right";
        currentHealth = maxHealth;
        currentMana = maxMana;
        velocity.setLength(0);
        position.set(spawnPosition);
        death = false;

        spawning = true;
    }

    /**
     * Sets the players attacking status
     * @param attacking true if attacking, false otherwise
     */
    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

    /**
     * Activates a player damaged state
     */
    public void hit(int damage){
        if (currentHealth > 0 && !damaged) {
            spriteRow = 25;
            maxSpriteCol = 1;
            currentHealth -= damage;

            velocity.set((direction.contains("left") ? 7 : -7),-7);
            knockedBack = true;

            damaged = true;

            MusicHandler.playerDamaged();
        }
    }

    /**
     * Sets the players knockback status
     * @param knockback true if knockback, false otherwise
     */
    public void setKnockback(boolean knockback) {
        this.knockedBack = knockback;
    }

    public void setVelocity(double x, double y) {
        velocity.set(x, y);
    }

    /**
     * gets the players knockback status
     * @return boolean, true if knockedback
     */
    public boolean getKnockback() {
        return knockedBack;
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
        if (direction.contains("left") && !knockedBack ||
                (knockedBack && direction.contains("right"))) {
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
