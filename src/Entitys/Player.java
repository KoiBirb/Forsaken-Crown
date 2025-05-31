package Entitys;

import Attacks.MeleeAttacks.Player.PlayerDashHeavyAttack;
import Attacks.MeleeAttacks.Player.PlayerDashSwingAttack;
import Attacks.MeleeAttacks.Player.PlayerHeavyAttack;
import Attacks.MeleeAttacks.Player.PlayerQuickAttack;
import Handlers.CollisionHandler;
import Handlers.ImageHandler;
import Handlers.Sound.MusicHandler;
import Handlers.Vector2;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;
import java.awt.geom.AffineTransform;

import static Main.Main.*;

public class Player extends Entity {

    public enum PlayerState {
        SPAWNING,
        IDLE,
        WALKING,
        JUMPING,
        FALLING,
        ATTACKING,
        DASHING,
        HEALING,
        HIT,
        DEAD
    }

    private PlayerState state;
    private int spriteCounter, spriteRow, spriteCol, maxSpriteCol, lastSpriteRow, lives;

    // timers
    private long jumpKeyPressStartTime, lastQuickAttackTime, lastHeavyAttackTime,
            fallStartTime, healStartTime, dashStartTime, lastDashTime, deathTime, lastGroundedTime,
            lastHitTime;

    private Vector2 spawnPosition;
    private boolean chain, continuousJump;

    public Player(Vector2 position) {
        super(position, new Vector2(0, 0), 90, 37,
                6.4, new Rectangle(0, 0, 18, 47),
                ImageHandler.loadImage("Assets/Images/Hero/SwordMaster/The SwordMaster/Sword Master Sprite Sheet 90x37.png"), 10, 10);

        spawnPosition = new Vector2(position.x, position.y);
        state = PlayerState.SPAWNING;
        lives = 1;
    }

    @Override
    public void update() {

        if (currentHealth > 0) {
            switch (state) {
                case SPAWNING:
                    velocity.x = 0;
                    velocity.y = 0;
                    spriteRow = 17;
                    maxSpriteCol = 3;
                    MusicHandler.spawn();
                    break;

                case HIT:
                    if (spriteRow != 25) {
                        spriteRow = 25;
                        maxSpriteCol = 1;
                        spriteCol = 0;
                    }
                    break;

                default:
                    handlePlayerInput();
                    break;
            }
        } else if (state != PlayerState.DEAD) {
            state = PlayerState.DEAD;
            spriteRow = 26;
            maxSpriteCol = 5;
            spriteCol = 0;
            velocity.setLength(0);
            deathTime = System.currentTimeMillis();
            MusicHandler.playerDeath();
            MusicHandler.stopFootsteps();
            MusicHandler.stopFalling();
            MusicHandler.stopHealCharge();

            lives--;
            if (lives <= 0)
                switchToDeath();
        }

        determineDirection();

        isColliding = false;
        CollisionHandler.checkTileCollision(this);

        if (!onGround && !continuousJump && state != PlayerState.SPAWNING) {
            if (velocity.y < 9) {
                velocity.y += 0.8;
            } else {
                TiledMap.cameraShake(1, 6);
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
                    if (state == PlayerState.HEALING) {
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
                        MusicHandler.setSpawnPlaying(false);
                        state = PlayerState.IDLE;
                    }
                    if (state == PlayerState.DEAD) {
                        spriteCol = 5;
                    }
                }
            }
        }

        if (state == PlayerState.DEAD && System.currentTimeMillis() - deathTime >= 5000) {
            resetPlayer();
        }

        super.update();

        if (lastSpriteRow != spriteRow) {
            spriteCol = 0;
            spriteCounter = 0;
        }
        lastSpriteRow = spriteRow;
    }

    private void handlePlayerInput() {

        if (onGround) {
            lastGroundedTime = System.currentTimeMillis();
        }

        if (state != PlayerState.DASHING)
            velocity.x = 0;

        // Falling
        if (velocity.y > 0) {
            if (fallStartTime == 0)
                fallStartTime = System.currentTimeMillis();
            if (velocity.y > 9)
                MusicHandler.falling();
        } else {
            fallStartTime = 0;
            MusicHandler.stopFalling();
        }

        // Landing
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
        if (keyI.wPressed && state != PlayerState.HEALING) {
            int coyoteTime = 100;
            if (jump && (onGround || System.currentTimeMillis() - lastGroundedTime <= coyoteTime) && jumpKeyPressStartTime == 0) {
                jumpKeyPressStartTime = System.currentTimeMillis();
                jump = false;
                MusicHandler.jump();
            }
            if (System.currentTimeMillis() - jumpKeyPressStartTime <= 200) {
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
        if (keyI.kPressed && state != PlayerState.DASHING && System.currentTimeMillis() - lastDashTime >= 1000 && currentMana > 0) {
            state = PlayerState.DASHING;
            dashStartTime = System.currentTimeMillis();
            lastDashTime = dashStartTime;
            velocity.x = direction.contains("right") ? 30 : -30;
            MusicHandler.dash();
            TiledMap.cameraShake(3, 1);
            currentMana--;
            spriteRow = 12;
            maxSpriteCol = 5;
        }

        if (state == PlayerState.DASHING) {
            long elapsedTime = System.currentTimeMillis() - dashStartTime;
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
        if (keyI.uPressed && !keyI.iPressed) {
            long currentTime = System.currentTimeMillis();
            if (state != PlayerState.ATTACKING) {
                if (state != PlayerState.DASHING) {
                    if (!chain) {
                        if (currentTime - lastQuickAttackTime >= PlayerQuickAttack.COOLDOWN) {
                            spriteRow = 8;
                            maxSpriteCol = 4;
                            new PlayerQuickAttack(this, false);
                            MusicHandler.hit();
                            chain = true;
                            lastQuickAttackTime = currentTime;
                            currentMana += 2;
                            state = PlayerState.ATTACKING;
                        }
                    } else {
                        spriteRow = 7;
                        maxSpriteCol = 6;
                        new PlayerQuickAttack(this, true);
                        MusicHandler.hit();
                        chain = false;
                        lastQuickAttackTime = currentTime;
                        state = PlayerState.ATTACKING;
                    }
                } else {
                    spriteRow = 10;
                    maxSpriteCol = 5;
                    state = PlayerState.ATTACKING;
                    new PlayerDashSwingAttack(this);
                    MusicHandler.dashSwingAttack();
                    lastQuickAttackTime = currentTime;
                }
            }
        }

        if (keyI.jPressed && state != PlayerState.ATTACKING && !keyI.iPressed) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastHeavyAttackTime >= PlayerHeavyAttack.COOLDOWN) {
                if (state == PlayerState.DASHING) {
                    new PlayerDashHeavyAttack(this);
                    MusicHandler.dashHeavyAttack();
                    spriteRow = 6;
                    maxSpriteCol = 5;
                } else {
                    new PlayerHeavyAttack(this);
                    MusicHandler.heavyAttack();
                    spriteRow = 9;
                    maxSpriteCol = 4;
                }
                lastHeavyAttackTime = currentTime;
                state = PlayerState.ATTACKING;
            }
        }

        // Healing
        if (keyI.iPressed && onGround && state != PlayerState.HEALING) {
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

                state = PlayerState.HEALING;

                int healAmount = (int) ((System.currentTimeMillis() - healStartTime) / 650);
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
                MusicHandler.footsteps();
            else
                MusicHandler.stopFootsteps();
            if (state != PlayerState.ATTACKING)
                state = PlayerState.WALKING;
        } else {
            MusicHandler.stopFootsteps();
            if (onGround && !continuousJump && state != PlayerState.ATTACKING && state != PlayerState.HEALING && state != PlayerState.DASHING && state != PlayerState.SPAWNING) {
                spriteRow = 1;
                maxSpriteCol = 8;
                state = PlayerState.IDLE;
            }
        }
    }

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

    private void resetPlayer() {
        MusicHandler.setDeathPlaying(false);
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

    public void setAttacking(boolean attacking) {
        state = attacking ? PlayerState.ATTACKING : PlayerState.IDLE;
    }

    public void hit(int damage, int knockbackX, int knockbackY) {
        long now = System.currentTimeMillis();
        if (currentHealth > 0 && state != PlayerState.HIT && now - lastHitTime > 500) {
            lastHitTime = now;
            spriteRow = 25;
            maxSpriteCol = 1;
            currentHealth -= damage;
            velocity.set((direction.contains("left") ? knockbackX : -knockbackX), -knockbackY);
            knockedBack = true;
            state = PlayerState.HIT;
            MusicHandler.playerDamaged();
        }
    }

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

    public void increaseMana(int amount) {
        currentMana = Math.min(currentMana + amount, maxMana);
    }

    public PlayerState getState() {
        return state;
    }
}