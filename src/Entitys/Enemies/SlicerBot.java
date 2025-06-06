/*
 * SkeletonSummoner.java
 * Leo Bogaert
 * May 28, 2025,
 * Extends enemy, represents a skeleton summoner enemy that can summon skeletons and attack the player.
 */

package Entitys.Enemies;

import Attacks.Enemies.SlicerAttack;
import Handlers.CollisionHandler;
import Handlers.ImageHandler;
import Handlers.Sound.SoundHandlers.EnemySoundHandler;
import Handlers.SpikeDetectionHandler;
import Handlers.Vector2;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;
import java.awt.image.VolatileImage;

public class SlicerBot extends Enemy{

    // states
    public enum State {IDLE, WALK, ATTACKING, DEAD, RETRACTING}
    private enum Logic {PATROL, AGGRESSIVE}

    private State currentState = State.IDLE;
    private Logic currentLogic = Logic.PATROL;

    private final double visionRadius = 200;
    private long lastAttackTime = 0;

    private long patrolStateChangeTime = 0;
    private long patrolDuration = 0;
    private boolean patrolWalking = false, footstepsPlaying = false;

    private static final VolatileImage imageReg = ImageHandler.loadImage("Assets/Images/Enemies/Slicer Bot/Sicer Bot Sprite Sheet 16x32.png");

    /**
     * Summoner constructor.
     * @param pos The initial position of the summoner.
     */
    public SlicerBot(Vector2 pos) {
        super(pos, 2, 8, 16, 32, 1,  new Rectangle(0, 0, 20, 32));

        this.image = imageReg;
    }

    /**
     * Updates summoner state and behavior.
     */
    public void update() {

        if (currentState != State.DEAD) {
            int ts = TiledMap.getScaledTileSize();
            Vector2 playerPos = GamePanel.player.getSolidAreaCenter();
            Vector2 currentPos = getSolidAreaCenter();
            Vector2 topCenter = getSolidAreaXCenter();

            //room check
            int myRoom = TiledMap.getRoomId(currentPos.x, currentPos.y);
            int playerRoom = TiledMap.getPlayerRoomId();
            boolean inSameRoom = myRoom == playerRoom;

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

                        if (dist <= 0.5 * ts && currentState != State.ATTACKING) {
                            if (now - lastAttackTime >= SlicerAttack.COOLDOWN) {
                                setAttacking(true);
                                spriteCol = 0;
                                spriteRow = 1;
                                maxSpriteCol = 2;
                                spriteCounter = 0;
                                new SlicerAttack(this);
                                velocity.x = 0;
                                lastAttackTime = now;
                            } else {
                                velocity.x = 0;
                                currentState = State.IDLE;
                                spriteRow = 0;
                                maxSpriteCol = 0;
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
                                    spriteRow = 3;
                                    maxSpriteCol = 1;
                                }
                            } else {
                                velocity.x = 0;
                                currentState = State.IDLE;
                                spriteRow = 0;
                                maxSpriteCol = 0;
                                if (spriteCol > maxSpriteCol) spriteCol = 0;
                            }

                            boolean facingSpike = SpikeDetectionHandler.isFacingSpike(currentPos.x, currentPos.y, velocity.x, width, height);
                            boolean canLand = SpikeDetectionHandler.canLandAfterSpike(currentPos.x, currentPos.y, velocity.x, width, height);

                            if (facingSpike && canLand && onGround && !jumpedOut && now - lastJumpTime >= JUMP_COOLDOWN_MS) {
                                velocity.y = JUMP_FORCE;
                                jumpedOut = true;
                                lastJumpTime = now;
                            }
                        }

                    } else if (!hit) {
                        velocity.x = 0;
                        currentState = State.IDLE;
                        spriteRow = 0;
                        maxSpriteCol = 0;
                        if (spriteCol > maxSpriteCol) spriteCol = 0;
                    }
                    break;

                case PATROL: // Randomly walk back and forth
                    boolean onGroundPatrol = isOnGround();
                    long now = System.currentTimeMillis();

                    if (now > patrolStateChangeTime) {
                        patrolWalking = !patrolWalking;
                        if (patrolWalking) {
                            patrolDuration = 1000 + (long) (Math.random() * 1500);
                            currentState = State.WALK;
                            spriteRow = 3;
                            maxSpriteCol = 2;
                            if (Math.random() < 0.5) {
                                direction = "left";
                                velocity.x = -getSpeed();
                            } else {
                                direction = "right";
                                velocity.x = getSpeed();
                            }
                        } else {
                            patrolDuration = 1000 + (long) (Math.random() * 3000);
                            currentState = State.IDLE;
                            spriteRow = 0;
                            maxSpriteCol = 0;
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
                                maxSpriteCol = 0;
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
                    footstepsPlaying = true;
                }
            } else {
                if (footstepsPlaying) {
                    EnemySoundHandler.stopBotSteps();
                    footstepsPlaying = false;
                }
            }
        }


        spriteCounter++;
        if (spriteCounter > 6) {
            spriteCounter = 0;
            spriteCol++;
            if (spriteCol >= maxSpriteCol) {
                if (currentState == State.IDLE) {
                    spriteCol = 0;
                }
                if (currentState == State.ATTACKING) {
                    spriteCol = maxSpriteCol;
                    currentState = State.RETRACTING;
                    spriteRow = 2;
                } else if (currentState == State.RETRACTING) {
                    spriteCol = maxSpriteCol;
                    if (hasStartedChasing && !hit) {
                        currentState = State.WALK;
                        spriteRow = 3;
                        maxSpriteCol = 1;
                    } else {
                        currentState = State.IDLE;
                        spriteRow = 0;
                        maxSpriteCol = 0;
                    }
                } else if (hit && !currentState.equals(State.DEAD)) {
                    spriteCol = maxSpriteCol;
                    hit = false;
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
        int checkX = (int) (x + direction * (width /4.0));
        int checkY = (int) (y + height/2.0 + 5);
        return CollisionHandler.isSolidTileAt(checkX, checkY);
    }

    /**
     * Draws the summoner
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
                    sx + width * 2 - 5, sy - 10, sx - 5, sy + height * 2 - 10,
                    spriteCol * width, spriteRow * height,
                    (spriteCol + 1) * width, (spriteRow + 1) * height,
                    null
            );
        } else {
            g2.drawImage(
                    image,
                    sx-5, sy - 10, sx + width * 2 - 5, sy + height * 2 - 10,
                    spriteCol * width, spriteRow * height,
                    (spriteCol + 1) * width, (spriteRow + 1) * height,
                    null
            );
        }
    }

    @Override
    public void stopSteps() {
        if (footstepsPlaying) {
            EnemySoundHandler.stopBotSteps();
            footstepsPlaying = false;
        }
    }

    @Override
    public boolean getFootstepsPlaying() {
        return footstepsPlaying;
    }

    /**
     * Sets the attacking state of the summoner.
     * @param attacking true if summoner is attacking, false otherwise.
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
     * Debug draw method to visualize the summoner's vision radius and line of sight.
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
        int myRoom = TiledMap.getRoomId(center.x, center.y);
        int playerRoom = TiledMap.getPlayerRoomId();
        boolean inSameRoom = myRoom == playerRoom;
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
        int checkX = (int) (center.x + moveDir * (width /4.0));
        int checkY = (int) (center.y + height/2.0 + 5);

        g2.setColor(Color.MAGENTA);
        g2.fillRect(checkX - (int) cam.x - 2, checkY - (int) cam.y - 2, 4, 4);
    }

    /**
     * Handles damage taken by the summoner.
     * @param damage The amount of damage taken.
     * @param knockbackX The knockback force in the X direction.
     * @param knockbackY The knockback force in the Y direction.
     */
    public void hit(int damage, int knockbackX, int knockbackY) {
        if (canBeHit() && !hit){
            currentHealth -= damage;

            super.hit();

            hit = true;
        }

        if (currentHealth <= 0) {
            death();
        }
    }

    /**
     * Handles the death of the summoner.
     */
    public void death(){
        if (currentState != State.DEAD) {
            currentState = State.DEAD;
            spriteRow = 4;
            spriteCol = 0;
            maxSpriteCol = 12;
            velocity.x = 0;
            velocity.y = 0;
            GamePanel.points += 200;
            EnemySoundHandler.stopBotSteps();
            EnemySoundHandler.botDeath();
        }
    }

    /**
     * Gets the current state of the summoner.
     * @return The current state.
     */
    public State getState(){
        return currentState;
    }

    public void setState(State state){
        this.currentState = state;
    }
}

