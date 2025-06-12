/*
 * Ghoul.java
 * Leo Bogaert, Heeyoung Shin
 * Jun 7, 2025,
 * Creates a Ghoul enemy
 */

package Entitys.Enemies;

import Attacks.Enemies.GhoulAttack;
import Handlers.CollisionHandler;
import Handlers.ImageHandler;
import Handlers.Sound.SoundHandlers.EnemySoundHandler;
import Handlers.Vector2;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;
import java.awt.image.VolatileImage;

public class Ghoul extends Enemy {

    public enum State {IDLE, WALK, DAMAGED, ATTACKING, DEAD}
    protected State currentState = State.IDLE;

    private boolean idleForward = true, footstepsPlaying = false;
    private final double visionRadius = 200;
    private long lastAttackTime = 0;

    private static final VolatileImage imageReg = ImageHandler.loadImage("Assets/Images/Enemies/Ghoul/Ghoul Sprite Sheet 62 x 33.png");

    /**
     * Ghoul constructor
     * @param pos Initial position of the ghoul
     */
    public Ghoul(Vector2 pos) {
        super(pos, 1, 8, 62, 33, 3,  new Rectangle(0, 0, 20, 40));

        this.direction = new java.util.Random().nextBoolean() ? "right" : "left";
        this.image = imageReg;
    }

    /**
     * Updates Ghoul state and behavior.
     */
    public void update() {

        if (currentState != State.DEAD) {
            int ts = TiledMap.getScaledTileSize();
            Vector2 playerPos = GamePanel.player.getSolidAreaCenter();
            Vector2 currentPos = getSolidAreaCenter();
            Vector2 currentTopPos = getSolidAreaXCenter();

            //room check
            int playerRoom = TiledMap.getPlayerRoomId();
            boolean inSameRoom = roomNumber == playerRoom;

            // los
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

                if (dist <= 1.2 * ts && currentState != State.ATTACKING) {
                    if (now - lastAttackTime >= GhoulAttack.COOLDOWN) {
                        setAttacking(true);
                        spriteCol = 0;
                        spriteRow = 2;
                        maxSpriteCol = 6;
                        spriteCounter = 0;
                        velocity.x = 0;
                        new GhoulAttack(this);
                        EnemySoundHandler.ghoulAttack();
                        lastAttackTime = now;
                    } else {
                        velocity.x = 0;
                        currentState = State.IDLE;
                        spriteRow = 0;
                        maxSpriteCol = 3;
                        if (spriteCol > maxSpriteCol) spriteCol = 0;
                    }

                } else if (!closeX) {
                    double moveDir = Math.signum(dx);
                    if (isGroundAhead(currentPos.x, currentPos.y, moveDir)) {
                        velocity.x = moveDir * getSpeed();
                        direction = velocity.x < 0 ? "left" : "right";
                        if (currentState != State.ATTACKING) {
                            currentState = State.WALK;
                            spriteRow = 1;
                            maxSpriteCol = 7;
                        }
                    } else {
                        velocity.x = 0;
                        currentState = State.IDLE;
                        spriteRow = 0;
                        maxSpriteCol = 3;
                        if (spriteCol > maxSpriteCol) spriteCol = 0;
                    }
                }

            } else if (!hit) {
                velocity.x = 0;
                currentState = State.IDLE;
                spriteRow = 0;
                maxSpriteCol = 3;
                if (spriteCol > maxSpriteCol) spriteCol = 0;
            }

            if (currentState == State.WALK && onGround) {
                if (!footstepsPlaying) {
                    EnemySoundHandler.ghoulFootsteps();
                    footstepsPlaying = true;
                }
            } else {
                if (footstepsPlaying) {
                    EnemySoundHandler.stopGhoulFootsteps();
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
        if (spriteCounter >= 12) {
            spriteCounter = 0;
            if (currentState == State.IDLE) {
                if (idleForward) {
                    spriteCol++;
                    if (spriteCol >= 3) {
                        spriteCol = 3;
                        idleForward = false;
                    }
                } else {
                    spriteCol--;
                    if (spriteCol <= 0) {
                        spriteCol = 0;
                        idleForward = true;
                    }
                }
            } else {
                spriteCol++;
                if (spriteCol >= maxSpriteCol) {
                    if (currentState == State.ATTACKING) {
                        spriteCol = 6;
                        setAttacking(false);
                        if (hasStartedChasing && !hit) {
                            currentState = State.WALK;
                            spriteRow = 1;
                            maxSpriteCol = 7;
                        } else {
                            currentState = State.IDLE;
                            spriteRow = 0;
                            maxSpriteCol = 3;
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
        }

        if (currentState == State.ATTACKING) {
            velocity.x = 0;
        }

        super.update();
    }

    /**
     * Draws the Ghoul on the screen
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
                sx + width * 2 - 50, sy, sx - 50, sy + height * 2,
                spriteCol * width, spriteRow * height,
                (spriteCol + 1) * width, (spriteRow + 1) * height,
                null
            );
        } else {
            g2.drawImage(
                image,
                sx - 50, sy, sx + width * 2 - 50, sy + height * 2,
                spriteCol * width, spriteRow * height,
                (spriteCol + 1) * width, (spriteRow + 1) * height,
                null
            );
        }
    }

    /**
     * Sets the Ghoul's state to attacking or idle
     * @param attacking true if the Ghoul is attacking, false if idle
     */
    public void setAttacking(boolean attacking) {
        if (attacking) {
            currentState = State.ATTACKING;
            spriteRow = 2;
            spriteCol = 0;
            maxSpriteCol = 6;
        } else {
            currentState = State.IDLE;
            spriteRow = 0;
            spriteCol = 0;
            maxSpriteCol = 3;
        }
    }

    /**
     * Checks for ledges ahead of the Ghoul
     * @return false if there is a ledge, true if there is no ledge
     */
    public boolean isGroundAhead(double x, double y, double direction) {
        int checkX = (int) (x + direction * ((double) width/2.0));
        int checkY = (int) (y + (double) height + 2);
        return CollisionHandler.isSolidTileAt(checkX, checkY);
    }

    /**
     * Debug draw
     * @param g2
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
     * Handles if the Ghoul has been hit
     * @param damage int amount of damage taken
     * @param knockbackX int knockback force in the X direction
     * @param knockbackY int knockback force in the Y direction
     */
    public void hit(int damage, int knockbackX, int knockbackY) {
        if (canBeHit() && !hit){
            currentHealth -= damage;

            spriteRow = 3;
            spriteCol = 0;
            maxSpriteCol = 3;

            currentState = State.DAMAGED;
            super.hit();
            EnemySoundHandler.stopGhoulAttack();

            if (currentHealth > 0)
                EnemySoundHandler.ghoulHit();

            hit = true;
        }

        if (currentHealth <= 0) {
            death();
        }
    }

    /**
     * Stops the footsteps sound if it is playing
     */
    @Override
    public void stopSteps() {
        if (footstepsPlaying) {
            EnemySoundHandler.stopGhoulFootsteps();
            footstepsPlaying = false;
        }
    }

    /**
     * Checks if the footsteps sound is currently playing
     * @return true if footsteps are playing, false otherwise
     */
    @Override
    public boolean getFootstepsPlaying() {
        return footstepsPlaying;
    }

    /**
     * Handles the death of the Ghoul
     */
    public void death(){
        if (currentState != State.DEAD) {
            GamePanel.activeEnemies.remove(this);
            currentState = State.DEAD;
            spriteRow = 4;
            spriteCol = 0;
            maxSpriteCol = 6;
            velocity.x = 0;
            velocity.y = 0;
            GamePanel.points += 75;
            EnemySoundHandler.stopGhoulAttack();
            EnemySoundHandler.stopGhoulFootsteps();
            EnemySoundHandler.ghoulDeath();
            super.death();
        }
    }

    /**
     * Gets the current state of the Ghoul
     * @return currentState the current state of the Ghoul
     */
    public State getState(){
        return currentState;
    }

    /**
     * Sets the current state of the Ghoul
     * @param state the new state to set
     */
    public void setState(State state){
        this.currentState = state;
    }
}
