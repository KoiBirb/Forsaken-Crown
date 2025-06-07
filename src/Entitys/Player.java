/*
 * Player.java
 * Leo Bogaert
 * Jun 7, 2025,
 * Handles player entity and its actions
 */

package Entitys;

import Attacks.Player.PlayerDashHeavyAttack;
import Attacks.Player.PlayerDashSwingAttack;
import Attacks.Player.PlayerHeavyAttack;
import Attacks.Player.PlayerQuickAttack;
import Handlers.CollisionHandler;
import Handlers.EnemySpawnHandler;
import Handlers.ImageHandler;
import Handlers.Sound.SoundHandlers.PlayerSoundHandler;
import Handlers.Vector2;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;
import java.awt.image.VolatileImage;

import static Main.Main.*;

public class Player extends Entity {

    public enum PlayerState { SPAWNING, IDLE, WALKING, JUMPING,
                FALLING, ATTACKING, DASHING, HEALING, HIT, DEAD }
    private PlayerState state;

    private VolatileImage imageReg, imageHit;
    private Vector2 spawnPosition;

    private boolean chain, continuousJump, canMove, directionLock, heal;
    private int spriteCounter, spriteRow, spriteCol,
            maxSpriteCol, lastSpriteRow, lives, hitCounter, initialHealTickTime;;

    private long jumpKeyPressStartTime, lastQuickAttackTime, lastHeavyAttackTime,
            fallStartTime, healStartTime, dashStartTime, lastDashTime, deathTime,
            lastGroundedTime, now, lastHealTickTime;

    /**
     * Constructor for Player
     * @param position Vector2 initial position of the player
     */
    public Player(Vector2 position) {
        super(position, new Vector2(0, 0), 90, 37,
                6.4, new Rectangle(0, 0, 18, 47),
                ImageHandler.loadImage("Images/Hero/Sword Master Sprite Sheet 90x37.png"), 10, 10);

        imageHit = ImageHandler.loadImage("Images/Hero/Sword Master Sprite Sheet 90x37 Hit.png");
        imageReg = image;

        spawnPosition = new Vector2(position.x, position.y);
        state = PlayerState.SPAWNING;
        lives = 1;
        initialHealTickTime = 900;
        canMove = true;
    }

    /**
     * Updates player state and actions
     */
    @Override
    public void update() {

        now = System.currentTimeMillis();

        if (currentHealth > 0) {
            switch (state) {
                case SPAWNING:
                    velocity.x = 0;
                    velocity.y = 0;
                    spriteRow = 17;
                    maxSpriteCol = 3;
                    PlayerSoundHandler.spawn();
                    break;
                case HIT:
                    if (spriteRow != 25) {
                        spriteRow = 25;
                        maxSpriteCol = 1;
                        spriteCol = 0;
                    }
                    break;
                default:
                    if (canMove)
                        handlePlayerInput();
                    else
                        velocity.x = 0;
                    break;
            }
        } else if (state != PlayerState.DEAD) {
            state = PlayerState.DEAD;
            spriteRow = 26;
            maxSpriteCol = 5;
            spriteCol = 0;
            velocity.setLength(0);
            deathTime = now;

            PlayerSoundHandler.death();
            PlayerSoundHandler.stopFootsteps();
            PlayerSoundHandler.stopFalling();
            PlayerSoundHandler.stopHealCharge();
            GamePanel.backgroundMusic.fadeOut(2000);
        }

        if (!directionLock)
            determineDirection();

        isColliding = false;
        CollisionHandler.checkTileCollision(this);

        if (!onGround && !continuousJump && state != PlayerState.SPAWNING) {
            if (velocity.y < 9)
                velocity.y += 0.8;
             else
                TiledMap.cameraShake(1, 6);
        }

        if (image != imageReg) {
            hitCounter++;
            if (hitCounter > 25) {
                hitCounter = 0;
                image = imageReg;
                knockedBack = false;
            }
        }

        if (!GamePanel.fading || state == PlayerState.SPAWNING) {
            spriteCounter++;
            if (spriteCounter > 5) {
                spriteCounter = 0;
                spriteCol++;
                if (spriteCol > maxSpriteCol) {
                    if (spriteRow == 1 || spriteRow == 3 || spriteRow == 15) {
                        spriteCol = 0;
                    }
                    if (state == PlayerState.HEALING && spriteRow != 1) {
                        state = PlayerState.IDLE;
                        spriteCol = maxSpriteCol;
                    }
                    if (state == PlayerState.DASHING) {
                        state = PlayerState.IDLE;
                        spriteCol = maxSpriteCol;
                    }
                    if (state == PlayerState.HIT) {
                        knockedBack = false;
                        state = PlayerState.IDLE;
                        spriteCol = maxSpriteCol;
                    }
                    if (state == PlayerState.ATTACKING) {
                        setAttacking(false);
                        state = PlayerState.IDLE;
                        spriteCol = maxSpriteCol;
                    }
                    if (state == PlayerState.SPAWNING) {
                        PlayerSoundHandler.setSpawnPlaying(false);
                        state = PlayerState.IDLE;
                    }
                    if (state == PlayerState.DEAD) {
                        spriteCol = 5;
                    }
                }
            }
        }

        if (state == PlayerState.DEAD && now - deathTime >= 4500) {
            lives--;
            if (lives <= 0)
                switchToEnd(false);
            EnemySpawnHandler.setup();
            resetPlayer();
        }

        super.update();

        if (lastSpriteRow != spriteRow) {
            spriteCol = 0;
            spriteCounter = 0;
        }
        lastSpriteRow = spriteRow;
    }

    /**
     * Handles player input and actions
     */
    private void handlePlayerInput() {

        if (onGround) {
            lastGroundedTime = now;
        }

        if (state != PlayerState.DASHING)
            velocity.x = 0;

        // Falling
        if (velocity.y > 0) {
            if (fallStartTime == 0)
                fallStartTime = now;
            if (velocity.y > 9)
                PlayerSoundHandler.falling();
        } else {
            fallStartTime = 0;
            PlayerSoundHandler.stopFalling();
        }

        // Landing
        if (onGround) {
            if (fallStartTime > 0 && now - fallStartTime > 400) {
                PlayerSoundHandler.landHard();
            } else if (fallStartTime > 0) {
                PlayerSoundHandler.land();
            }
            velocity.y = 0;
            fallStartTime = 0;
        }

        // Jumping
        if (keyI.wPressed && state != PlayerState.HEALING) {
            int coyoteTime = 100;
            if (jump && (onGround || now - lastGroundedTime <= coyoteTime) && jumpKeyPressStartTime == 0) {
                jumpKeyPressStartTime = now;
                jump = false;
                PlayerSoundHandler.jump();
            }
            if (now - jumpKeyPressStartTime <= 200) {
                continuousJump = true;
            } else {
                jumpKeyPressStartTime = 0;
                continuousJump = false;
            }
        } else {
            jumpKeyPressStartTime = 0;
            continuousJump = false;
            jump = true;
        }

        if (keyI.wPressed && continuousJump && !keyI.iPressed) {
            velocity.y = -8;
            isColliding = false;
            if (state != PlayerState.ATTACKING && state != PlayerState.DASHING) {
                spriteRow = 13;
                maxSpriteCol = 2;
            }
        } else if (!onGround && spriteCol == maxSpriteCol && state != PlayerState.ATTACKING && state != PlayerState.DASHING && velocity.y < 6) {
            spriteRow = 14;
            maxSpriteCol = 3;
        } else if (!onGround && velocity.y > 6 && state != PlayerState.ATTACKING && state != PlayerState.DASHING) {
            spriteRow = 15;
            maxSpriteCol = 2;
        }

        // Dashing
        if (keyI.kPressed && state != PlayerState.DASHING && now - lastDashTime >= 1000 && currentMana >= 3) {
            state = PlayerState.DASHING;
            dashStartTime = now;
            lastDashTime = dashStartTime;
            velocity.x = direction.contains("right") ? 30 : -30;
            PlayerSoundHandler.dash();
            TiledMap.cameraShake(3, 1);
            currentMana -= 3;
            spriteRow = 12;
            maxSpriteCol = 5;
        }

        if (state == PlayerState.DASHING) {
            long elapsedTime = now - dashStartTime;
            double deceleration = 0.1;
            TiledMap.cameraShake(1, 6);
            if (elapsedTime <= 300) {
                velocity.x *= (1 - deceleration);
                if (Math.abs(velocity.x) < 0.5) {
                    velocity.x = 0;
                }
            }
        }

        // Attacking
        if (keyI.uPressed && !keyI.iPressed && state != PlayerState.HEALING) {
            long currentTime = now;
            if (state != PlayerState.ATTACKING) {
                if (state != PlayerState.DASHING) {
                    if (!chain) {
                        if (currentTime - lastQuickAttackTime >= PlayerQuickAttack.COOLDOWN) {
                            spriteRow = 8;
                            maxSpriteCol = 4;
                            new PlayerQuickAttack(this, false);
                            PlayerSoundHandler.hit();
                            chain = true;
                            lastQuickAttackTime = currentTime;
                            state = PlayerState.ATTACKING;
                        }
                    } else {
                        spriteRow = 7;
                        maxSpriteCol = 6;
                        new PlayerQuickAttack(this, true);
                        PlayerSoundHandler.hit();
                        chain = false;
                        lastQuickAttackTime = currentTime;
                        state = PlayerState.ATTACKING;
                    }
                } else {
                    spriteRow = 10;
                    maxSpriteCol = 5;
                    state = PlayerState.ATTACKING;
                    new PlayerDashSwingAttack(this);
                    PlayerSoundHandler.dashSwingAttack();
                    lastQuickAttackTime = currentTime;
                }
            }
        }

        if (keyI.jPressed && state != PlayerState.ATTACKING && !keyI.iPressed && state != PlayerState.HEALING) {
            long currentTime = now;
            if (currentTime - lastHeavyAttackTime >= PlayerHeavyAttack.COOLDOWN) {
                if (state == PlayerState.DASHING) {
                    new PlayerDashHeavyAttack(this);
                    PlayerSoundHandler.dashHeavyAttack();
                    spriteRow = 6;
                    maxSpriteCol = 5;
                } else {
                    new PlayerHeavyAttack(this);
                    PlayerSoundHandler.heavyAttack();
                    spriteRow = 9;
                    maxSpriteCol = 4;
                }
                lastHeavyAttackTime = currentTime;
                state = PlayerState.ATTACKING;
            }
        }

        // Healing
        if (keyI.iPressed && onGround && currentHealth != maxHealth && currentMana > 0) {
            if (healStartTime == 0) {
                initialHealTickTime = 900;
                healStartTime = now;
                lastHealTickTime = now;
                PlayerSoundHandler.healCharge();
                state = PlayerState.HEALING;

                heal = true;

                spriteRow = 1;
                maxSpriteCol = 8;
            }
            if (now - lastHealTickTime >= initialHealTickTime && currentMana > 0 && currentHealth < maxHealth) {
                initialHealTickTime -= 50;
                currentHealth++;
                currentMana--;
                lastHealTickTime = now;
                PlayerSoundHandler.heal();
                TiledMap.cameraShake(6, 4);
            } else {
                TiledMap.cameraShake(2, 1);
            }
        } else if (state == PlayerState.HEALING && heal) {
            heal = false;

            spriteRow = 11;
            spriteCol = 0;
            maxSpriteCol = 5;
            spriteCounter = 0;

            healStartTime = 0;
            lastHealTickTime = 0;
            PlayerSoundHandler.healEnd();
            PlayerSoundHandler.stopHealCharge();
        }

        // Movement
        if ((keyI.aPressed || keyI.dPressed) && !(keyI.iPressed && onGround) && state != PlayerState.HEALING && state != PlayerState.DASHING) {
            if (onGround && !continuousJump && state != PlayerState.ATTACKING) {
                maxSpriteCol = 7;
                spriteRow = 3;
            }
            double speed = (state == PlayerState.ATTACKING) ? 2.8 : 3;
            if (keyI.aPressed && !knockedBack) {
                velocity.x = -speed;
            }
            if (keyI.dPressed && !knockedBack) {
                velocity.x = speed;
            }
            if (onGround)
                PlayerSoundHandler.footsteps();
            else
                PlayerSoundHandler.stopFootsteps();
            if (state != PlayerState.ATTACKING)
                state = PlayerState.WALKING;
        } else {
            PlayerSoundHandler.stopFootsteps();
            if (onGround && !continuousJump && state != PlayerState.ATTACKING && state != PlayerState.HEALING && state != PlayerState.DASHING && state != PlayerState.SPAWNING) {
                spriteRow = 1;
                maxSpriteCol = 8;
                state = PlayerState.IDLE;
            }
        }
    }

    /**
     * Determines the player's direction based on velocity
     */
    private void determineDirection() {
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
        PlayerSoundHandler.setDeathPlaying(false);
        direction = "right";
        currentHealth = maxHealth;
        currentMana = maxMana;
        velocity.setLength(0);
        position.set(spawnPosition);
        state = PlayerState.SPAWNING;
        chain = false;
        knockedBack = false;
        continuousJump = false;
    }

    /**
     * Sets the attacking state of the player
     * @param attacking boolean indicating if the player is attacking
     */
    public void setAttacking(boolean attacking) {
        state = attacking ? PlayerState.ATTACKING : PlayerState.IDLE;
    }

    /**
     * Handles the player's hit action
     * @param damage int amount of damage taken
     * @param knockbackX int horizontal knockback
     * @param knockbackY int vertical knockback
     */
    public void hit(int damage, int knockbackX, int knockbackY) {
        if (currentHealth > 0 && image != imageHit) {

            if (state != PlayerState.ATTACKING) {
                state = PlayerState.HIT;
                spriteRow = 25;
                spriteCounter = 0;
                spriteCol = 0;
                maxSpriteCol = 1;
                knockedBack = true;
                velocity.set((direction.contains("left") ? knockbackX : -knockbackX), -knockbackY);
            }

            image = imageHit;

            currentHealth -= damage;
            PlayerSoundHandler.playerDamaged();
        }
    }

    /**
     * Draws the player on the screen
     * @param g2 Graphics object to draw on
     */
    @Override
    public void draw(Graphics2D g2) {

        Vector2 cameraPos = GamePanel.tileMap.returnCameraPos();
        double screenX = position.x - cameraPos.x;
        double screenY = position.y - cameraPos.y;

        if (direction.contains("left")){
            g2.drawImage(image,
                    (int) screenX - 115, (int) screenY - 17,
                    (int) (screenX - 115 + 90 * GamePanel.scale), (int) (screenY - 17 + 37 * GamePanel.scale),
                    (spriteCol + 1) * 90, spriteRow * 37,
                    spriteCol * 90, (spriteRow + 1) * 37, null);

        } else {
            g2.drawImage(image,
                    (int) screenX - 45, (int) screenY - 17,
                    (int) (screenX - 45 + 90 * GamePanel.scale), (int) (screenY - 17 + 37 * GamePanel.scale),
                    spriteCol * 90, spriteRow * 37,
                    (spriteCol + 1) * 90, (spriteRow + 1) * 37, null);
        }
    }

    /**
     * Increases the player's mana by a specified amount
     * @param amount int amount to increase mana by
     */
    public void increaseMana(int amount) {
        currentMana = Math.min(currentMana + amount, maxMana);
    }

    /**
     * Gets the current state of the player
     * @return PlayerState current state of the player
     */
    public PlayerState getState() {
        return state;
    }

    /**
     * Sets the player ability to move
     * @param canMove boolean indicating if the player can move
     */
    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    /**
     * Gets the current number of lives the player has
     * @return int number of lives
     */
    public int getLives() {
        return lives;
    }

    /**
     * Sets the players ability to change direction
     * @param lock boolean indicating if the player should be locked in direction
     */
    public void setDirectionLock(boolean lock) {
        this.directionLock = lock;
    }

    /**
     * Sets the spawn position of the player
     * @param pos Vector2 new spawn position
     */
    public void setSpawnPosition(Vector2 pos) {
        this.spawnPosition.set(pos);
    }
}