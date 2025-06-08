/*
 * Skeleton.java
 * Leo Bogaert
 * Jun 7, 2025,
 * Creates a Skeleton enemy
 */
package Entitys.Enemies.Summoner;

import Attacks.Enemies.SkeletonAttack;
import Entitys.Enemies.Enemy;
import Handlers.CollisionHandler;
import Handlers.ImageHandler;
import Handlers.Sound.SoundHandlers.EnemySoundHandler;
import Handlers.Vector2;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;
import java.awt.image.VolatileImage;

public class Skeleton extends Enemy {

    public enum State {IDLE, WALK, DAMAGED, ATTACKING, DEAD, SPAWNING}
    protected State currentState;

    private boolean footstepsPlaying = false;

    private final double visionRadius = 300;
    private long lastAttackTime = 0;

    private final static VolatileImage skeletonImage = ImageHandler.loadImage("Assets/Images/Enemies/Skeleton Summoner/Skeleton - Unarmed/Skeleton - Unarmed 43x22.png");

    /**
     * Skeleton constructor.
     * @param pos The initial position of the Skeleton.
     */
    public Skeleton(Vector2 pos) {
        super(pos, 2, 8, 43, 22, 2,  new Rectangle(0, 0, 25, 33));

        this.image = skeletonImage;

        this.currentState = State.SPAWNING;
        this.spriteRow = 0;
        this.spriteCol = 0;
        this.maxSpriteCol = 16;

        EnemySoundHandler.skeletonSpawn();
    }

    /**
     * Updates the Skeleton's state and behavior.
     */
    public void update() {

        if (currentState == State.SPAWNING) {
            spriteCounter++;
            if (spriteCounter > 4) {
                spriteCounter = 0;
                spriteCol++;
                if (spriteCol > maxSpriteCol) {
                    currentState = State.IDLE;
                    spriteRow = 1;
                    spriteCol = 0;
                    maxSpriteCol = 11;
                }
            }
            return;
        }

        if (currentState != State.DEAD) {

            int ts = TiledMap.getScaledTileSize();
            Vector2 playerPos = GamePanel.player.getSolidAreaCenter();
            Vector2 currentPos = getSolidAreaCenter();
            Vector2 currentTopPos = getSolidAreaXCenter();

            int playerRoom = TiledMap.getPlayerRoomId();
            boolean inSameRoom = roomNumber == playerRoom;

            double dist = currentPos.distanceTo(playerPos);
            boolean inVision = dist <= visionRadius;

            canSeePlayer = inSameRoom && inVision && hasLineOfSight(currentTopPos, GamePanel.player.getSolidAreaXCenter());

            if (canSeePlayer) hasStartedChasing = true;

            Vector2 target = hasStartedChasing && canSeePlayer ? playerPos : spawnPos;
            double dx = target.x - currentPos.x;
            boolean closeX = Math.abs(dx) <= ts;

            CollisionHandler.checkTileCollision(this);
            boolean onGround = isOnGround();

            if (hasStartedChasing && canSeePlayer && !hit) {
                long now = System.currentTimeMillis();

                if (dist <= ts && currentState != State.ATTACKING) {
                    if (now - lastAttackTime >= SkeletonAttack.COOLDOWN) {
                        setAttacking(true);
                        spriteCol = 0;
                        spriteRow = 3;
                        maxSpriteCol = 8;
                        spriteCounter = 0;
                        velocity.x = 0;
                        new SkeletonAttack(this);
                        lastAttackTime = now;
                    } else {
                        velocity.x = 0;
                        currentState = State.IDLE;
                        spriteRow = 1;
                        maxSpriteCol = 11;
                        if (spriteCol > maxSpriteCol) spriteCol = 0;
                    }

                } else if (!closeX) {
                    double moveDir = Math.signum(dx);
                    if (isGroundAhead(currentPos.x, currentPos.y, moveDir)) {
                        velocity.x = moveDir * getSpeed();
                        direction = velocity.x < 0 ? "left" : "right";
                        if (currentState != State.ATTACKING) {
                            currentState = State.WALK;
                            spriteRow = 2;
                            maxSpriteCol = 7;
                            if (spriteCol > maxSpriteCol) spriteCol = 0;
                        }
                    } else {
                        velocity.x = 0;
                        currentState = State.IDLE;
                        spriteRow = 1;
                        maxSpriteCol = 11;
                        if (spriteCol > maxSpriteCol) spriteCol = 0;
                    }
                }

            } else if (!hit) {
                velocity.x = 0;
                currentState = State.IDLE;
                spriteRow = 1;
                maxSpriteCol = 11;
                if (spriteCol > maxSpriteCol) spriteCol = 0;
            }

            if (currentState == State.WALK && onGround) {
                if (!footstepsPlaying) {
                    EnemySoundHandler.skeletonFootsteps();
                    footstepsPlaying = true;
                }
            } else {
                if (footstepsPlaying) {
                    EnemySoundHandler.stopSkeletonFootsteps();
                    footstepsPlaying = false;
                }
            }

            if (!onGround) {
                velocity.y = Math.min(velocity.y + GRAVITY, TERMINAL_VELOCITY);
            } else {
                velocity.y = 0;
                jumpedOut = false;
            }
        }

        spriteCounter++;
        if (spriteCounter >= 4) {
            spriteCounter = 0;
                spriteCol++;
                if (spriteCol >= maxSpriteCol) {
                    if (currentState == State.ATTACKING) {
                        spriteCol = 6;
                        setAttacking(false);
                        if (hasStartedChasing && !hit) {
                            currentState = State.WALK;
                            spriteRow = 2;
                            maxSpriteCol = 7;
                            if (spriteCol > maxSpriteCol) spriteCol = 0;
                        } else {
                            currentState = State.IDLE;
                            spriteRow = 1;
                            maxSpriteCol = 11;
                        }
                    } else if (hit && !currentState.equals(State.DEAD)) {
                        spriteCol = 3;
                        hit = false;
                    } else if (currentState == State.DEAD){
                        GamePanel.enemies.remove(this);
                    } else {
                        spriteCol = 0;
                    }
                }
            }

        if (currentState == State.ATTACKING) {
            velocity.x = 0;
        }

        super.update();
    }

    /**
     * Stops the footsteps sound when the enemy stops moving.
     */
    @Override
    public void stopSteps() {
        if (footstepsPlaying) {
            EnemySoundHandler.stopSkeletonFootsteps();
            footstepsPlaying = false;
        }
    }

    /**
     * Checks if the footsteps sound is currently playing.
     * @return true if footsteps sound is playing, false otherwise.
     */
    @Override
    public boolean getFootstepsPlaying() {
        return footstepsPlaying;
    }

    /**
     * Draws the Skeleton
     * @param g2 Graphics2D object to draw on
     */
    @Override
    public void draw(Graphics2D g2) {
//        debugDraw(g2);
        Vector2 cam = GamePanel.tileMap.returnCameraPos();

        int sx = (int) (position.x - cam.x);
        int sy = (int) (position.y - cam.y - height + 10);

        if ("left".equals(direction)) {
            g2.drawImage(
                    image,
                    sx + width * 2 - 32, sy + 2, sx - 32, sy + height * 2 + 2,
                    spriteCol * width, spriteRow * height,
                    (spriteCol + 1) * width, (spriteRow + 1) * height,
                    null
            );
        } else {
            g2.drawImage(
                    image,
                    sx - 32, sy + 2, sx + width * 2 - 32, sy + height * 2 + 2,
                    spriteCol * width, spriteRow * height,
                    (spriteCol + 1) * width, (spriteRow + 1) * height,
                    null
            );
        }
    }

    /**
     * Sets the Skeleton's attacking state.
     * @param attacking true if the Skeleton is attacking, false otherwise.
     */
    public void setAttacking(boolean attacking) {
        if (attacking) {
            currentState = State.ATTACKING;
            spriteRow = 2;
            spriteCol = 0;
            maxSpriteCol = 8;
        } else {
            currentState = State.IDLE;
            spriteRow = 1;
            spriteCol = 0;
            maxSpriteCol = 11;
        }
    }

    /**
     * Checks if there is ground ahead of the Skeleton.
     * @return true if there is ground, false otherwise.
     */
    public boolean isGroundAhead(double x, double y, double direction) {
        int checkX = (int) (x + direction * ((double) width/2.0));
        int checkY = (int) (y + (double) height + 2);
        return CollisionHandler.isSolidTileAt(checkX, checkY);
    }

    /**
     * Debug draw method
     * @param g2 Graphics2D object to draw on
     */
    private void debugDraw(Graphics2D g2) {
        Vector2 cam = GamePanel.tileMap.returnCameraPos();

        // Draw vision radius
        g2.setColor(new Color(0, 0, 255, 64));
        int r = (int) visionRadius;
        Vector2 center = getSolidAreaCenter();
        g2.drawOval((int) (center.x - r - cam.x), (int) (center.y - r - cam.y), r * 2, r * 2);

        Vector2 playerCenter = GamePanel.player.getSolidAreaXCenter();
        Vector2 topCenter = getSolidAreaXCenter();
        int playerRoom = TiledMap.getPlayerRoomId();
        boolean inSameRoom = roomNumber == playerRoom;
        boolean inVision = center.distanceTo(playerCenter) <= visionRadius;
        boolean canSee = inSameRoom && inVision && hasLineOfSight(topCenter,playerCenter);

        g2.setColor(canSee ? Color.GREEN : Color.RED);
        g2.drawLine(
                (int) (center.x - cam.x), (int) (position.y - cam.y),
                (int) (playerCenter.x - cam.x), (int) (GamePanel.player.getPosition().y - cam.y)
        );

        g2.setColor(Color.MAGENTA);
        Rectangle solid = getSolidArea();
        g2.drawRect((int) (solid.x - cam.x), (int) (solid.y - cam.y), solid.width, solid.height);

        double moveDir = (velocity.x < 0) ? -1 : 1;
        int checkX = (int) (center.x + moveDir * (width /2.0));
        int checkY = (int) (center.y + height + 2);

        g2.setColor(Color.MAGENTA);
        g2.fillRect(checkX - (int) cam.x - 2, checkY - (int) cam.y - 2, 4, 4);
    }

    /**
     * Handles damage taken by the Skeleton.
     * @param damage The amount of damage taken.
     * @param knockbackX The knockback force in the X direction.
     * @param knockbackY The knockback force in the Y direction.
     */
    public void hit(int damage, int knockbackX, int knockbackY) {
        if (canBeHit() && !hit){
            currentHealth -= damage;

            spriteRow = 1;
            spriteCol = 0;
            maxSpriteCol = 3;

            currentState = State.DAMAGED;
            super.hit();
            EnemySoundHandler.stopSkeletonAttack();

            if (currentHealth > 0)
                EnemySoundHandler.skeletonHit();

            hit = true;
        }

        if (currentHealth <= 0) {
            death();
        }
    }

    /**
     * Handles the death of the Skeleton.
     */
    public void death(){
        if (currentState != State.DEAD) {
            currentState = State.DEAD;
            spriteRow = 4;
            spriteCol = 0;
            maxSpriteCol = 15;
            velocity.x = 0;
            velocity.y = 0;
            GamePanel.points += 50;
            EnemySoundHandler.stopSkeletonAttack();
            EnemySoundHandler.stopSkeletonFootsteps();
            EnemySoundHandler.skeletonDeath();
            super.death();
        }
    }

    /**
     * Gets the current state of the summoner.
     * @return The current state.
     */
    public State getState(){
        return currentState;
    }
}
