/*
 * HeavySlicer.java
 * Leo Bogaert
 * Jun 7, 2025,
 * Creates a Heavy Slicer enemy
 */

package Entitys.Enemies;

import Attacks.Enemies.HeavySlicerAttack;
import Attacks.Enemies.ShockerAttack;
import Handlers.CollisionHandler;
import Handlers.ImageHandler;
import Handlers.Sound.SoundHandlers.EnemySoundHandler;
import Handlers.Vector2;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;
import java.awt.image.VolatileImage;

public class HeavySlicer extends Enemy{

    public enum State {IDLE, WALK, ATTACKING, DEAD}
    private enum Logic {PATROL, AGGRESSIVE}

    private State currentState = State.IDLE;
    private Logic currentLogic = Logic.PATROL;

    private final double visionRadius = 300;
    private long lastAttackTime = 0;

    private long patrolStateChangeTime = 0, patrolDuration = 0;
    private boolean patrolWalking = false, footstepsPlaying = false;

    private int hitCounter = 0;

    private static final VolatileImage imageRegPRELOAD = ImageHandler.loadImage("Assets/Images/Enemies/Heavy Slicer/Heavy Slicer Sprite Sheet 48x32.png");
    private static final VolatileImage imageHitPRELOAD = ImageHandler.loadImage("Assets/Images/Enemies/Heavy Slicer/Heavy Slicer Sprite Sheet 48x32 Hit.png");

    private final VolatileImage imageReg, imageHit;

    /**
     * Heavy Slicer constructor.
     * @param pos The initial position of the Heavy Slicer.
     */
    public HeavySlicer(Vector2 pos) {
        super(pos, 1, 8, 48, 32, 10,  new Rectangle(0, 0, 50, 64));

        imageReg = imageRegPRELOAD;
        imageHit = imageHitPRELOAD;

        this.image = imageReg;
    }

    /**
     * Updates Heavy Slicer state and behavior.
     */
    public void update() {

        if (currentState != State.DEAD) {
            int ts = TiledMap.getScaledTileSize();
            Vector2 playerPos = GamePanel.player.getSolidAreaCenter();
            Vector2 currentPos = getSolidAreaCenter();
            Vector2 topCenter = getSolidAreaXCenter();

            //room check
            int playerRoom = TiledMap.getPlayerRoomId();
            boolean inSameRoom = roomNumber == playerRoom;

            // line of sight
            double dist = currentPos.distanceTo(playerPos);
            boolean inVision = dist <= visionRadius;

            canSeePlayer = inSameRoom && inVision && hasLineOfSight(topCenter,playerPos);

            // Logic handling
            if (canSeePlayer) {
                hasStartedChasing = true;
                currentLogic = Logic.AGGRESSIVE;
            } else {
                hasStartedChasing = false;
                currentLogic = Logic.PATROL;
            }

            // collision and gravity handling
            CollisionHandler.checkTileCollision(this);
            boolean onGround = isOnGround();

            if (!onGround) {
                velocity.y = Math.min(velocity.y + GRAVITY, TERMINAL_VELOCITY);
            } else {
                velocity.y = 0;
                jumpedOut = false;
            }

            switch (currentLogic) {
                case AGGRESSIVE: // chase and attack player
                    Vector2 target = hasStartedChasing && canSeePlayer ? playerPos : spawnPos;
                    double dx = target.x - currentPos.x;
                    boolean closeX = Math.abs(dx) <= ts;

                    if (hasStartedChasing && canSeePlayer && !hit) {
                        long now = System.currentTimeMillis();

                        if (dist <= 1.8 * ts && currentState != State.ATTACKING) {
                            if (now - lastAttackTime >= ShockerAttack.COOLDOWN) {
                                setAttacking(true);
                                spriteCol = 0;
                                spriteRow = 2;
                                maxSpriteCol = 7;
                                spriteCounter = 0;
                                new HeavySlicerAttack(this);
                                velocity.x = 0;
                                lastAttackTime = now;
                            } else {
                                velocity.x = 0;
                                currentState = State.IDLE;
                                spriteRow = 0;
                                maxSpriteCol = 11;
                                if (spriteCol > maxSpriteCol) spriteCol = 0;
                                break;
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
                                    if (spriteCol > maxSpriteCol) spriteCol = 0;
                                }
                            } else {
                                velocity.x = 0;
                                currentState = State.IDLE;
                                spriteRow = 0;
                                maxSpriteCol = 11;
                                if (spriteCol > maxSpriteCol) spriteCol = 0;
                            }
                        }

                    } else if (!hit) {
                        velocity.x = 0;
                        currentState = State.IDLE;
                        spriteRow = 0;
                        maxSpriteCol = 11;
                    }
                    break;

                case PATROL: // Randomly walk back and forth
                    boolean onGroundPatrol = isOnGround();
                    long now = System.currentTimeMillis();

                    if (now > patrolStateChangeTime) {
                        patrolWalking = !patrolWalking;
                        if (patrolWalking) {
                            patrolDuration = 1500 + (long) (Math.random() * 2500);
                            currentState = State.WALK;
                            spriteRow = 1;
                            maxSpriteCol = 7;
                            if (spriteCol > maxSpriteCol) spriteCol = 0;
                            if (Math.random() < 0.5) {
                                direction = "left";
                                velocity.x = -getSpeed();
                            } else {
                                direction = "right";
                                velocity.x = getSpeed();
                            }
                        } else {
                            patrolDuration = 1500 + (long) (Math.random() * 3500);
                            currentState = State.IDLE;
                            spriteRow = 0;
                            maxSpriteCol = 11;
                            velocity.x = 0;
                        }
                        patrolStateChangeTime = now + patrolDuration;
                    }

                    if (patrolWalking && currentState == State.WALK && onGroundPatrol) {
                        double moveDir = velocity.x < 0 ? -1 : 1;
                        if (!isGroundAhead(currentPos.x, currentPos.y, moveDir)) {
                            if (Math.random() < 0.5) {
                                patrolWalking = false;
                                currentState = State.IDLE;
                                spriteRow = 0;
                                maxSpriteCol = 11;
                                velocity.x = 0;
                                patrolDuration = 1000 + (long) (Math.random() * 3000);
                                patrolStateChangeTime = now + patrolDuration;
                            } else {
                                velocity.x = -velocity.x;
                                direction = "left".equals(direction) ? "right" : "left";
                            }
                        }
                    } else if (!patrolWalking) {
                        velocity.x = 0;
                    }
                    break;
            }

            if (currentState == State.WALK && onGround) {
                if (!footstepsPlaying) {
                    EnemySoundHandler.botSteps();
                    EnemySoundHandler.slicerFootsteps();
                    footstepsPlaying = true;
                }
            } else {
                if (footstepsPlaying) {
                    EnemySoundHandler.stopBotSteps();
                    EnemySoundHandler.stopSlicerFootsteps();
                    footstepsPlaying = false;
                }
            }
        }

        if (image != imageReg) {
            hitCounter++;
            if (hitCounter > 18) {
                hitCounter = 0;
                image = imageReg;
            }
        }


        spriteCounter++;
        if (spriteCounter > 11) {
            spriteCounter = 0;
            spriteCol++;
            if (currentState == State.DEAD && spriteCol == 10)
                EnemySoundHandler.shockerHitGround();
            if (spriteCol >= maxSpriteCol) {
                if (currentState == State.IDLE) {
                    spriteCol = 0;
                }
                if (currentState == State.ATTACKING) {
                    spriteCol = maxSpriteCol;
                    if (hasStartedChasing && !hit) {
                        currentState = State.WALK;
                        spriteRow = 1;
                        maxSpriteCol = 7;
                    } else {
                        currentState = State.IDLE;
                        spriteRow = 0;
                        maxSpriteCol = 11;
                    }
                } else if (currentState == State.DEAD) {
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
     * Checks for ledges.
     * @return true if no ledge, false otherwise.
     */
    public boolean isGroundAhead(double x, double y, double direction) {
        int checkX = (int) (x + direction * (width / 2.8));
        int checkY = (int) (y + height/2.0 + 25);
        return CollisionHandler.isSolidTileAt(checkX, checkY);
    }

    /**
     * Stops the footsteps sound if it is playing.
     */
    @Override
    public void stopSteps() {
        if (footstepsPlaying) {
            EnemySoundHandler.stopBotSteps();
            EnemySoundHandler.stopSlicerFootsteps();
            footstepsPlaying = false;
        }
    }

    /**
     * Checks if footsteps sound is currently playing.
     * @return true if footsteps are playing, false otherwise.
     */
    @Override
    public boolean getFootstepsPlaying() {
        return footstepsPlaying;
    }

    /**
     * Draws the Heavy Slicer
     * @param g2 graphics object to draw on
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
                    sx + width * 2 - 40, sy + 25, sx - 40, sy + height * 2 + 25,
                    spriteCol * width, spriteRow * height,
                    (spriteCol + 1) * width, (spriteRow + 1) * height,
                    null
            );
        } else {
            g2.drawImage(
                    image,
                    sx - 4, sy + 25, sx + width * 2 - 4, sy + height * 2 + 25,
                    spriteCol * width, spriteRow * height,
                    (spriteCol + 1) * width, (spriteRow + 1) * height,
                    null
            );
        }
    }

    /**
     * Sets the attacking state of the Heavy Slicer.
     * @param attacking true if Heavy Slicer is attacking, false otherwise.
     */
    public void setAttacking(boolean attacking) {
        if (attacking) {
            currentState = State.ATTACKING;
            spriteRow = 2;
            spriteCol = 0;
            maxSpriteCol = 8;
        } else {
            currentState = State.IDLE;
            spriteRow = 0;
            spriteCol = 0;
            maxSpriteCol = 11;
        }
    }

    /**
     * Debug draw method to visualize the Heavy Slicer's vision radius and line of sight.
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

        // ledge check
        double moveDir = (velocity.x < 0) ? -1 : 1;
        int checkX = (int) (center.x + moveDir * (width /2.8));
        int checkY = (int) (center.y + height/2.0 + 25);

        g2.setColor(Color.MAGENTA);
        g2.fillRect(checkX - (int) cam.x - 2, checkY - (int) cam.y - 2, 4, 4);
    }

    /**
     * Handles damage taken by the Heavy Slicer.
     * @param damage The amount of damage taken.
     * @param knockbackX The knockback force in the X direction.
     * @param knockbackY The knockback force in the Y direction.
     */
    public void hit(int damage, int knockbackX, int knockbackY) {
        if (image != imageHit){
            currentHealth -= damage;

            image = imageHit;

            super.hit();
            EnemySoundHandler.slicerHit();
        }

        if (currentHealth <= 0) {
            death();
        }
    }

    /**
     * Handles the death of the Heavy Slicer.
     */
    public void death(){
        if (currentState != State.DEAD) {
            GamePanel.activeEnemies.remove(this);
            currentState = State.DEAD;
            spriteRow = 5;
            spriteCol = 0;
            maxSpriteCol = 14;
            velocity.x = 0;
            velocity.y = 0;
            GamePanel.points += 150;
            EnemySoundHandler.stopBotSteps();
            EnemySoundHandler.stopSlicerFootsteps();
            EnemySoundHandler.slicerDeath();
            super.death();
        }
    }

    /**
     * Gets the current state of the Heavy Slicer.
     * @return The current state.
     */
    public State getState(){
        return currentState;
    }

    /**
     * Sets the current state of the Heavy Slicer.
     * @param state The new state to set.
     */
    public void setState(State state){
        this.currentState = state;
    }
}

