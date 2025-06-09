/*
 * Wasp.java
 * Leo Bogaert
 * Jun 7, 2025,
 * Creates the Wasp enemy
 */

package Entitys.Enemies.TheHive;

import Attacks.Enemies.SummonerAttack;
import Attacks.Enemies.WaspAttack;
import Entitys.Enemies.Enemy;
import Handlers.CollisionHandler;
import Handlers.ImageHandler;
import Handlers.Sound.SoundHandlers.EnemySoundHandler;
import Handlers.Vector2;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;
import java.awt.image.VolatileImage;

public class Wasp extends Enemy {

    public enum State {IDLE, FLY, DAMAGED, ATTACKING, DEAD}
    private enum Logic {PATROL, AGGRESSIVE, PASSIVE}

    private State currentState = State.IDLE;
    private Logic currentLogic = Logic.PATROL;
    private static final VolatileImage WASP_IMAGE = ImageHandler.loadImage("Assets/Images/Enemies/The Hive/Wasp/Wasp 16x16 Sprite Sheet.png");

    private final double visionRadius = 250;
    private long lastAttackTime, passiveAttackTimer, passiveAttackInterval;
    private boolean waitingForAttack = false;

    private long patrolStateChangeTime, patrolDuration;
    private boolean patrolFlying = false, footstepsPlaying = false, passiveArcForward = true;
    private double passiveCircleAngle = 0;

    /**
     * Wasp constructor.
     * @param pos The initial position of the wasp.
     */
    public Wasp(Vector2 pos) {
        super(pos, 2, 8, 16, 16, 2, new Rectangle(0, 0, 30, 30));
        this.image = WASP_IMAGE;
    }

    /**
     * Updates the wasp's state and behavior.
     */
    public void update() {
        if (currentState != State.DEAD) {
            int ts = TiledMap.getScaledTileSize();
            Vector2 playerPos = GamePanel.player.getSolidAreaCenter();
            Vector2 currentPos = getSolidAreaCenter();

            int myRoom = GamePanel.tileMap.getRoomId(currentPos.x, currentPos.y);
            int playerRoom = TiledMap.getPlayerRoomId();
            boolean inSameRoom = myRoom == playerRoom;

            double dist = currentPos.distanceTo(playerPos);
            boolean inVision = dist <= visionRadius;

            canSeePlayer = inSameRoom && inVision && hasLineOfSight(currentPos, playerPos);

            if (canSeePlayer) {
                hasStartedChasing = true;

                if (currentLogic != Logic.AGGRESSIVE)
                    currentLogic = Logic.PASSIVE;
            } else {
                hasStartedChasing = false;
                currentLogic = Logic.PATROL;
            }

            determineDirection();
            CollisionHandler.checkTileCollision(this);

            switch (currentLogic) {
                case AGGRESSIVE:
                    if (hasStartedChasing && canSeePlayer && !hit) {
                        long now = System.currentTimeMillis();
                        if (dist <= ts && currentState != State.ATTACKING) {
                            if (now - lastAttackTime >= WaspAttack.COOLDOWN) {
                                setAttacking(true);
                                new WaspAttack(this);
                                spriteCol = 0;
                                spriteRow = 1;
                                maxSpriteCol = 5;
                                spriteCounter = 0;
                                velocity.x = 0;
                                velocity.y = 0;
                                lastAttackTime = now;
                            } else {
                                velocity.x = 0;
                                velocity.y = 0;
                                currentState = State.IDLE;
                                spriteRow = 0;
                                maxSpriteCol = 3;
                                if (spriteCol > maxSpriteCol) spriteCol = 0;
                                break;
                            }
                        } else {
                            Vector2 dir = playerPos.subtract(currentPos).returnSetLength(1);
                            velocity.x = dir.x * getSpeed();
                            velocity.y = dir.y * getSpeed();
                            direction = velocity.x < 0 ? "left" : "right";
                            if (currentState != State.ATTACKING) {
                                currentState = State.FLY;
                                spriteRow = 0;
                                maxSpriteCol = 3;
                            }
                        }
                    } else if (!hit) {
                        velocity.x = 0;
                        velocity.y = 0;
                        currentState = State.IDLE;
                        spriteRow = 0;
                        maxSpriteCol = 3;
                        if (spriteCol > maxSpriteCol) spriteCol = 0;
                    }
                    break;

                case PATROL:
                    long now = System.currentTimeMillis();
                    if (now > patrolStateChangeTime) {
                        patrolFlying = !patrolFlying;
                        if (patrolFlying) {
                            patrolDuration = 200 + (long) (Math.random() * 500);
                            currentState = State.FLY;
                            spriteRow = 0;
                            maxSpriteCol = 3;

                            double angle = Math.random() * 2 * Math.PI;
                            velocity.x = Math.cos(angle) * getSpeed();
                            velocity.y = Math.sin(angle) * getSpeed();
                            direction = velocity.x < 0 ? "left" : "right";
                        } else {
                            patrolDuration = 100 + (long) (Math.random() * 300);
                            currentState = State.IDLE;
                            spriteRow = 0;
                            maxSpriteCol = 3;
                            velocity.x = 0;
                            velocity.y = 0;
                        }
                        patrolStateChangeTime = now + patrolDuration;
                    }
                    if (!patrolFlying) {
                        velocity.x = 0;
                        velocity.y = 0;
                    }
                    break;

                case PASSIVE:
                    if (!hit) {
                        if (!waitingForAttack) {
                            passiveAttackInterval = 1000 + (long)(Math.random() * 2000);
                            passiveAttackTimer = System.currentTimeMillis();
                            waitingForAttack = true;
                        }

                        if (waitingForAttack && System.currentTimeMillis() - passiveAttackTimer >= passiveAttackInterval) {
                            currentLogic = Logic.AGGRESSIVE;
                            waitingForAttack = false;
                        }

                        if (currentState != State.ATTACKING) {
                            double arcWidth = 4 * ts;
                            double arcHeight = 0.5 * ts;
                            double waveSpeed = 0.025;

                            if (passiveArcForward) {
                                passiveCircleAngle += waveSpeed;
                                if (passiveCircleAngle >= Math.PI) {
                                    passiveCircleAngle = Math.PI;
                                    passiveArcForward = false;
                                }
                            } else {
                                passiveCircleAngle -= waveSpeed;
                                if (passiveCircleAngle <= 0) {
                                    passiveCircleAngle = 0;
                                    passiveArcForward = true;
                                }
                            }

                            double centerX = playerPos.x;
                            double centerY = playerPos.y - arcHeight;
                            double angle = -passiveCircleAngle;
                            double targetX = centerX + arcWidth * Math.cos(angle);
                            double targetY = centerY + arcWidth * Math.sin(angle);
                            double deviation = 8;
                            targetX += (Math.random() - 0.5) * deviation;
                            targetY += (Math.random() - 0.5) * deviation;

                            Vector2 target = new Vector2(targetX, targetY);
                            Vector2 dir = target.subtract(currentPos).returnSetLength(1);

                            velocity.x = dir.x * getSpeed();
                            velocity.y = dir.y * getSpeed();
                        } else {
                            velocity.x = 0;
                            velocity.y = 0;
                        }
                    }
                    break;

            }

            if (spriteCounter == 2 || spriteCounter == 5) {
                double jitter = 0.7;
                velocity.x += (Math.random() * 2 - 1) * jitter;
                velocity.y += (Math.random() * 2 - 1) * jitter;

                velocity.returnSetLength(speed);
            }

            if (currentState == State.FLY) {
                if (!footstepsPlaying) {
                    EnemySoundHandler.waspFly();
                    footstepsPlaying = true;
                }
            } else {
                if (footstepsPlaying) {
                    EnemySoundHandler.stopWaspFly();
                    footstepsPlaying = false;
                }
            }
        }

        spriteCounter++;
        if (spriteCounter >= 5) {
            spriteCounter = 0;
            spriteCol++;
            if (spriteCol >= maxSpriteCol) {
                if (currentState == State.IDLE) {
                    spriteCol = 0;
                }
                if (currentState == State.ATTACKING) {
                    spriteCol = maxSpriteCol;
                    setAttacking(false);
                    currentLogic = Logic.PASSIVE;
                    if (hasStartedChasing && !hit) {
                        currentState = State.FLY;
                        spriteRow = 0;
                        maxSpriteCol = 3;
                    } else {
                        currentState = State.IDLE;
                        spriteRow = 0;
                        maxSpriteCol = 3;
                    }
                } else if (hit && !currentState.equals(State.DEAD)) {
                    spriteCol = maxSpriteCol;
                    hit = false;
                } else if (currentState == State.DEAD) {
                    GamePanel.enemies.remove(this);
                } else if (currentState == State.DAMAGED) {
                    currentState = State.IDLE;
                    spriteRow = 0;
                    spriteCol = 0;
                } else {
                    spriteCol = 0;
                }
            }
        }

        if (currentState == State.ATTACKING) {
            velocity.x = 0;
            velocity.y = 0;
        }

        super.update();
    }

    /**
     * Stops the flying sound
     */
    @Override
    public void stopSteps() {
        if (footstepsPlaying){
            EnemySoundHandler.stopWaspFly();
            footstepsPlaying = false;
        }
    }

    /**
     * Checks if the wasp is currently playing flying sound.
     * @return false if not playing, true if playing.
     */
    @Override
    public boolean getFootstepsPlaying() {
        return footstepsPlaying;
    }

    /**
     * Checks if there is ground ahead of the wasp.
     * @param x The x-coordinate to check.
     * @param y The y-coordinate to check.
     * @param direction The direction to check (1 for right, -1 for left).
     * @return true if there is ground ahead, false otherwise.
     */
    public boolean isGroundAhead(double x, double y, double direction) {
        return true;
    }

    /**
     * Draws the wasp
     * @param g2 graphics object to draw on
     */
    @Override
    public void draw(Graphics2D g2) {
//        debugDraw(g2);
        Vector2 cam = GamePanel.tileMap.returnCameraPos();

        int sx = (int) (position.x - cam.x);
        int sy = (int) (position.y - cam.y - height + 10);

        if (direction.contains("right")) {
            g2.drawImage(
                    image,
                    sx + width * 3 - 10, sy - 5, sx - 10, sy + height * 3 - 5,
                    spriteCol * width, spriteRow * height,
                    (spriteCol + 1) * width, (spriteRow + 1) * height,
                    null
            );
        } else {
                g2.drawImage(
                    image,
                    sx - 10, sy - 5, sx + width * 3 - 10, sy + height * 3 - 5,
                    spriteCol * width, spriteRow * height,
                    (spriteCol + 1) * width, (spriteRow + 1) * height,
                    null
            );
        }
    }

    /**
     * Sets the attacking state of the wasp.
     * @param attacking true if wasp is attacking, false otherwise.
     */
    public void setAttacking(boolean attacking) {
        if (attacking) {
            currentState = State.ATTACKING;
            spriteRow = 2;
            spriteCol = 0;
            maxSpriteCol = 18;
        } else {
            currentState = State.IDLE;
            spriteRow = 0;
            spriteCol = 0;
            maxSpriteCol = 11;
        }
    }

    /**
     * Debug draw method
     * @param g2 Graphics2D object for drawing.
     */
    private void debugDraw(Graphics2D g2) {
        Vector2 cam = GamePanel.tileMap.returnCameraPos();

        // vision radius
        g2.setColor(new Color(0, 0, 255, 64));
        int r = (int) visionRadius;
        Vector2 center = getSolidAreaCenter();
        g2.drawOval((int) (center.x - r - cam.x), (int) (center.y - r - cam.y), r * 2, r * 2);

        // Path
        Vector2 topCenter = getSolidAreaXCenter();
        Vector2 playerCenter = GamePanel.player.getSolidAreaXCenter();
        int playerRoom = TiledMap.getPlayerRoomId();
        boolean inSameRoom = roomNumber == playerRoom;
        boolean inVision = center.distanceTo(playerCenter) <= visionRadius;
        boolean canSee = inSameRoom && inVision && hasLineOfSight(topCenter, playerCenter);

        g2.setColor(canSee ? Color.GREEN : Color.RED);
        g2.drawLine(
                (int) (topCenter.x - cam.x), (int) (topCenter.y - cam.y),
                (int) (playerCenter.x - cam.x), (int) (playerCenter.y - cam.y)
        );

        // hitbox
        g2.setColor(Color.MAGENTA);
        Rectangle solid = getSolidArea();
        g2.drawRect((int) (solid.x - cam.x), (int) (solid.y - cam.y), solid.width, solid.height);
    }

    /**
     * Handles damage taken by the wasp.
     * @param damage The amount of damage taken.
     * @param knockbackX The knockback force in the X direction.
     * @param knockbackY The knockback force in the Y direction.
     */
    public void hit(int damage, int knockbackX, int knockbackY) {
        if (canBeHit() && !hit){
            currentHealth -= damage;

            spriteRow = 2;
            spriteCol = 0;
            maxSpriteCol = 1;

            currentState = State.DAMAGED;

            super.hit();
            if (currentHealth > 0)
                EnemySoundHandler.waspHit();

            hit = true;
        }

        if (currentHealth <= 0) {
            death();
        }
    }

    /**
     * Handles the death of the wasp.
     */
    public void death(){
        if (currentState != State.DEAD) {
            GamePanel.activeEnemies.remove(this);
            currentState = State.DEAD;
            spriteRow = 3;
            spriteCol = 0;
            maxSpriteCol = 4;
            velocity.x = 0;
            velocity.y = 0;
            GamePanel.points += 25;
            EnemySoundHandler.stopWaspFly();
            EnemySoundHandler.waspDeath();
        }
    }

    /**
     * Determines the direction of the wasp based on its velocity.
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
     * Gets the current state of the wasp.
     * @return The current state.
     */
    public State getState(){
        return currentState;
    }

    /**
     * Sets the current state of the wasp.
     * @param state The new state to set.
     */
    public void setState(State state){
        this.currentState = state;
    }
}

