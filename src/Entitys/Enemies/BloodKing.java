/*
 * SkeletonSummoner.java
 * Leo Bogaert
 * May 28, 2025,
 * Extends enemy, represents a skeleton summoner enemy that can summon skeletons and attack the player.
 */

package Entitys.Enemies;

import Attacks.Enemies.BloodKing.*;
import Handlers.CollisionHandler;
import Handlers.ImageHandler;
import Handlers.Sound.SoundHandlers.EnemySoundHandler;
import Handlers.Sound.SoundHandlers.PlayerSoundHandler;
import Handlers.Vector2;
import Main.Main;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;
import java.awt.image.VolatileImage;
import java.util.ArrayList;

public class BloodKing extends Enemy{

    // states
    public enum State {IDLE, WALK, DAMAGED, ATTACKING, DEAD, SPAWNING}
    private enum Action {ATTACK, MOVE}
    private enum Attack {DODGE, SLASH, FINISHER, SLAM, HEART, STAB, HEARTTRANSFD, HEARTTRANSBW, CHARGE}

    private VolatileImage imageReg, imageHit;

    private final java.util.HashMap<Attack, Long> lastUsed = new java.util.HashMap<>();
    private State currentState;
    private Attack currentAttack = null;
    private ArrayList<Attack> viableAttacks = new ArrayList<>();

    private Action currentAction = Action.ATTACK;
    private long actionStartTime = 0, actionDuration = 2000;
    private int moveDir = 1, spawnCounter, distanceToTravel = 0;
    private boolean startSpawn = false;

    int offsetXL, offsetX, offsetY = 100, hitCounter = 0;
    private final double visionRadius = 1500;
    private long lastAttackTime = 0;

    private Vector2 orgPos = new Vector2(0, 0);

    private boolean footstepsPlaying = false;
    private boolean canMove = true;
    private boolean directionLocked = false;

    private long lastDamagedTime = 0;
    private static final long DAMAGED_DURATION_MS = 400; // adjust as needed

    /**
     * Summoner constructor.
     * @param pos The initial position of the summoner.
     */
    public BloodKing(Vector2 pos) {
        super(pos, 2, 8, 168, 79, 40,  new Rectangle(0, 0, 50, 65));

        imageReg = ImageHandler.loadImage("Images/Boss/Blood_King_combined.png");
        imageHit = ImageHandler.loadImage("Images/Boss/Blood_King_combined_Hit.png");

        this.image = ImageHandler.loadImage("Images/Boss/Blood_King_combined.png");

        this.currentState = State.SPAWNING;

        spriteRow = 16;
        spriteCol = 0;
        maxSpriteCol = 11;
        spawnCounter = 0;
    }

    /**
     * Updates BloodKing state and behavior.
     */
    public void update() {

        if (currentState == State.SPAWNING) {
            if (!startSpawn) {
                spawnCounter++;
                if (spawnCounter > 80) {
                    GamePanel.backgroundMusic.playBossMain();
                    EnemySoundHandler.heartKingAppear();
                    startSpawn = true;
                }
            } else {
                spriteCounter++;
                if (spriteCounter > 9) {
                    spriteCounter = 0;
                    spriteCol++;
                    if (spriteCol >= maxSpriteCol) {
                        currentState = State.IDLE;
                        spawnCounter = 0;
                        startSpawn = false;
                    }
                }
            }
            return;
        }

        if (currentState != State.DEAD) {
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

            hasStartedChasing = canSeePlayer;

            boolean onGround = true;

            if (spriteRow != 9) {
                CollisionHandler.checkTileCollision(this);
                onGround = isOnGround();
            }

            if (!onGround && spriteRow != 12 && spriteRow != 9) {
                velocity.y = Math.min(velocity.y + GRAVITY, TERMINAL_VELOCITY);
            } else {
                velocity.y = 0;
            }

            long now = System.currentTimeMillis();
            Vector2 target = hasStartedChasing && canSeePlayer ? playerPos : spawnPos;
            double dx = target.x - currentPos.x;
            boolean closeX = Math.abs(dx) <= ts;

            if (currentState == State.DAMAGED) {
                velocity.x = 0;
                velocity.y = 0;
                if (System.currentTimeMillis() - lastDamagedTime > DAMAGED_DURATION_MS) {
                    currentState = State.IDLE;
                    spriteRow = 11;
                    maxSpriteCol = 11;
                    if (spriteCol > maxSpriteCol) spriteCol = 0;
                    hit = false;
                }
                return;
            }

                if ((now - actionStartTime > actionDuration) && currentState == State.IDLE) {
                    currentAction = Math.random() < 0.5 ? Action.ATTACK : Action.MOVE;
                    distanceToTravel = 1 + (int) (Math.random() * 4);
                    actionStartTime = now;
                    actionDuration = 1000 + (int)(Math.random() * 2000);
                }

                if (currentAction == Action.MOVE) {
                    directionLocked = false;
                    if (dist <= distanceToTravel * ts && currentState != State.ATTACKING) {
                        if (now - lastAttackTime >= 1000) {
                            currentAction = Action.ATTACK;
                            lastAttackTime = now;
                        } else {
                            velocity.x = 0;
                            currentState = State.IDLE;
                            spriteRow = 11;
                            maxSpriteCol = 11;
                            if (spriteCol > maxSpriteCol) spriteCol = 0;
                        }
                    } else if (!closeX) {
                        double moveDir = Math.signum(dx);
                        if (isGroundAhead(currentPos.x, currentPos.y, moveDir)) {
                            if (!directionLocked) {
                                velocity.x = moveDir * getSpeed();
                                direction = moveDir < 0 ? "left" : "right";
                            }
                            if (currentState != State.ATTACKING) {
                                currentState = State.WALK;
                                spriteRow = 15;
                                maxSpriteCol = 7;
                                if (spriteCol > maxSpriteCol) spriteCol = 0;
                            }
                        }
                    } else {
                        velocity.x = 0;
                        currentState = State.IDLE;
                        spriteRow = 11;
                        maxSpriteCol = 11;
                        if (spriteCol > maxSpriteCol) spriteCol = 0;
                    }
                } else if (currentAction == Action.ATTACK) {
                    if (currentState != State.ATTACKING) {
                        direction = (playerPos.x < currentPos.x) ? "left": "right";
                        chooseAttack(dist);
                        if (currentAttack != null) {
                            switch (currentAttack) {
                                case DODGE:
                                    currentState = State.ATTACKING;
                                    setAttackUsed(Attack.DODGE);
                                    new Dodge(this);
                                    spriteRow = 1;
                                    spriteCol = 0;
                                    spriteCounter = 0;
                                    maxSpriteCol = 19;
                                    velocity.x = 0;
                                    break;

                                case SLASH:
                                    currentState = State.ATTACKING;
                                    setAttackUsed(Attack.SLASH);
                                    new Slash(this);
                                    spriteRow = 7;
                                    spriteCol = 0;
                                    spriteCounter = 0;
                                    maxSpriteCol = 13;
                                    break;

                                case FINISHER:
                                    currentState = State.ATTACKING;
                                    setAttackUsed(Attack.FINISHER);
                                    new Finisher(this);
                                    spriteRow = 2;
                                    spriteCol = 0;
                                    spriteCounter = 0;
                                    maxSpriteCol = 13;
                                    break;
                                case SLAM:
                                    currentState = State.ATTACKING;
                                    setAttackUsed(Attack.SLAM);
                                    new Slam(this);
                                    spriteRow = 12;
                                    spriteCol = 0;
                                    spriteCounter = 0;
                                    maxSpriteCol = 12;
                                    break;
                                case HEARTTRANSFD:
                                    currentState = State.ATTACKING;
                                    EnemySoundHandler.heartKingSink();
                                    setAttackUsed(Attack.HEART);
                                    orgPos.set(position.x, position.y);
                                    spriteRow = 6;
                                    spriteCol = 0;
                                    spriteCounter = 0;
                                    maxSpriteCol = 10;
                                    break;
                                case STAB:
                                    currentState = State.ATTACKING;
                                    setAttackUsed(Attack.STAB);
                                    new Stab(this);
                                    spriteRow = 0;
                                    spriteCol = 0;
                                    spriteCounter = 0;
                                    maxSpriteCol = 12;
                                    break;
                                default:
                            }
                        }
                        directionLocked = true;
                    }
                }

            if (currentState == State.WALK && onGround) {
                if (!footstepsPlaying) {
                    EnemySoundHandler.kingFootsteps();
                    footstepsPlaying = true;
                }
            } else {
                if (footstepsPlaying) {
                    EnemySoundHandler.stopKingFootsteps();
                    footstepsPlaying = false;
                }
            }
        }

        if (currentState == State.ATTACKING && (currentAttack == Attack.DODGE || currentAttack == Attack.STAB) && canMove) {
            if (spriteCounter == 0) {
                if (TiledMap.getRoomId(position.x + ((("left").equals(direction)) ? -getHitboxXOffset() : getHitboxXOffset()), position.y) == 19) {
                    position.x += (("left").equals(direction)) ? -getHitboxXOffset() : getHitboxXOffset();
                }
            }
        } else if (currentState == State.ATTACKING && currentAttack == Attack.SLAM) {

            if (spriteCounter == 0) {
                    position.y += getHitboxYOffset();
                    offsetY += getHitboxYOffset();
            }

            if (canMove) {
                Vector2 currentPos = getSolidAreaCenter();
                Vector2 playerPos = GamePanel.player.getSolidAreaCenter();
                moveDir = "left".equals(direction) ? -1 : 1;

                double targetX = playerPos.x - moveDir * 30;
                double dx = targetX - currentPos.x;

                boolean playerInFront = (moveDir > 0 && dx > 0) || (moveDir < 0 && dx < 0);

                if (playerInFront) {
                    velocity.x = moveDir * getSpeed();
                } else {
                    velocity.x = 0;
                }
            }
        } else if (currentAttack == Attack.HEART && currentState == State.ATTACKING && canMove){
            Vector2 playerPos = GamePanel.player.getSolidAreaCenter();
            Vector2 currentPos = getSolidAreaCenter();
            moveDir = (playerPos.x < currentPos.x) ? -1 : 1; // -1 for left, 1 for right

            velocity.x = moveDir * getSpeed();
        }

        if (!canMove) {
            velocity.x = 0;
        }

        if (image != imageReg) {
            hitCounter++;
            if (hitCounter > 18) {
                hitCounter = 0;
                image = imageReg;
                currentAction = Action.ATTACK;
            }
        }

        spriteCounter++;
        if (spriteCounter > 9) {
            spriteCounter = 0;
            spriteCol++;
            if (spriteCol >= maxSpriteCol) {
                if (currentState == State.IDLE) {
                    spriteCol = 0;
                }
                if (currentState == State.ATTACKING) {
                    if (spriteRow == 6) {
                        currentAttack = Attack.HEART;
                        new HeartSlam(this);
                        spriteRow = 9;
                        spriteCol = 0;
                        maxSpriteCol = 16;
                        position.set(GamePanel.player.getPosition().x, position.y);
                    } else if (spriteRow == 9){
                        currentAttack = Attack.HEARTTRANSBW;
                        EnemySoundHandler.heartKingAppear();
                        spriteRow = 16;
                        spriteCol = 0;
                        maxSpriteCol = 11;
                        position.set(orgPos.x, orgPos.y);
                    } else {
                        spriteCol = maxSpriteCol;
                        setAttacking(false);
                        hit = false;
                        currentAttack = null;
                        currentState = State.IDLE;
                        spriteRow = 11;
                        maxSpriteCol = 11;
                        if (spriteCol > maxSpriteCol) spriteCol = 0;
                    }
                } else if (hit && !currentState.equals(State.DEAD)) {
                    spriteCol = maxSpriteCol;
                    hit = false;
                } else if (currentState == State.DEAD) {
                    PlayerSoundHandler.stopFootsteps();
                    GamePanel.enemies.remove(this);
                    Main.switchToEnd(true);
                } else {
                    spriteCol = 0;
                }
            }
        }

        if (currentState == State.ATTACKING && (currentAttack != Attack.SLAM && currentAttack != Attack.HEART)) {
            velocity.x = 0;
        }

        super.update();
    }

    @Override
    public void stopSteps() {
        if (footstepsPlaying) {
            EnemySoundHandler.stopKingFootsteps();
            footstepsPlaying = false;
        }
    }

    @Override
    public boolean getFootstepsPlaying() {
        return footstepsPlaying;
    }

    private void chooseAttack(double playerDistance) {

        int TileSize = TiledMap.getScaledTileSize();

        if (currentAttack == null) {
            viableAttacks.clear();

            if (playerDistance < 2 * TileSize) {
                if (isAttackOffCooldown(Attack.FINISHER)) {
                    viableAttacks.add(Attack.FINISHER);
                }
            }

            if (playerDistance < 2.5 * TileSize) {
                if (isAttackOffCooldown(Attack.SLASH)) {
                    viableAttacks.add(Attack.SLASH);
                }
            }

            if (playerDistance < 3.5 * TileSize && playerDistance > 1.5 * TileSize) {
                if (isAttackOffCooldown(Attack.SLAM)) {
                    viableAttacks.add(Attack.SLAM);
                }
            }

            if (playerDistance < 5 * TileSize && playerDistance > 2 * TileSize) {

                if (isAttackOffCooldown(Attack.STAB)) {
                    viableAttacks.add(Attack.STAB);
                }
                if (isAttackOffCooldown(Attack.DODGE)) {
                    viableAttacks.add(Attack.DODGE);
                }
            }


            if (playerDistance > 5 * TileSize) {
                if (isAttackOffCooldown(Attack.HEART)) {
                    viableAttacks.add(Attack.HEARTTRANSFD);
                }
            }
            
            if (viableAttacks.isEmpty()) {
                currentAttack = null;
                currentAction = Action.MOVE;
            } else {
                currentAttack = viableAttacks.get((int) (Math.random() * viableAttacks.size()));
                lastAttackTime = System.currentTimeMillis();
            }
        }

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

    private boolean isAttackOffCooldown(Attack atk) {
        long now = System.currentTimeMillis();
        return switch (atk) {
            case DODGE -> now - lastUsed.getOrDefault(atk, 0L) >= Dodge.COOLDOWN;
            case SLASH -> now - lastUsed.getOrDefault(atk, 0L) >= Slash.COOLDOWN;
            case FINISHER -> now - lastUsed.getOrDefault(atk, 0L) >= Finisher.COOLDOWN;
            case SLAM -> now - lastUsed.getOrDefault(atk, 0L) >= Slam.COOLDOWN;
            case HEART -> now - lastUsed.getOrDefault(atk, 0L) >= HeartSlam.COOLDOWN;
            case STAB -> now - lastUsed.getOrDefault(atk, 0L) >= Stab.COOLDOWN;
            default -> true;
        };
    }

    private void setAttackUsed(Attack atk) {
        lastUsed.put(atk, System.currentTimeMillis());
    }

    /**
     * Draws the summoner
     * @param g2 graphics object to draw on
     */
    @Override
    public void draw(Graphics2D g2) {
        Vector2 cam = GamePanel.tileMap.returnCameraPos();

        int sx = (int) (position.x - cam.x);
        int sy = (int) (position.y - cam.y - height + 10);

        if (spriteRow == 12) {
            offsetX = 290;
            offsetXL = 160;
        } else if (spriteRow == 9) {
            offsetX = 180;
            offsetXL = 265;
        } else if (spriteRow == 1 || spriteRow == 0) {

            if (spriteCounter == 0) {
                if (spriteCol == 0) {
                    offsetX = 133;
                    offsetXL = 315;
                }

                offsetX += (("left").equals(direction)) ? -getHitboxXOffset() : getHitboxXOffset();
                offsetXL += (("left").equals(direction)) ? -getHitboxXOffset() : getHitboxXOffset();
            }
        } else {
            offsetX = 133;
            offsetXL = 315;
        }

        if ((!"left".equals(direction) && (currentAttack == Attack.FINISHER || currentAttack == Attack.SLAM))
                || ("left".equals(direction) && (currentAttack != Attack.FINISHER && currentAttack != Attack.SLAM))) {
            g2.drawImage(
                    image,
                    sx + width * 3 - offsetXL, sy - offsetY, sx - offsetXL, sy + height * 3 - offsetY,
                    spriteCol * width, spriteRow * height,
                    (spriteCol + 1) * width, (spriteRow + 1) * height,
                    null
            );
        } else {
            g2.drawImage(
                    image,
                    sx - offsetX, sy - offsetY, sx + width * 3 - offsetX, sy + height * 3 - offsetY,
                    spriteCol * width, spriteRow * height,
                    (spriteCol + 1) * width, (spriteRow + 1) * height,
                    null
            );
        }
//        debugDraw(g2);
    }

    private int getHitboxYOffset() {
        if (spriteRow == 12) {
            switch (spriteCol) {
                case 1: return -90;
                case 2: return -80;
                case 5: return 80;
                case 6: return 90;
                default: return 0;
            }
        }
        return 0;
    }

    private int getHitboxXOffset() {
        if (spriteRow == 1) {
            switch (spriteCol) {
                case 3: return -65;
                case 4: return -55;
                case 8: return 160;
                case 9: return 70;
                case 14: return 25;
                default: return 0;
            }
        } else if (spriteRow == 0) {
            switch (spriteCol) {
                case 5: return 117;
                case 9: return 17;
                case 10: return 35;
                default: return 0;
            }
        }
        return 0;
    }

    /**
     * Sets the attacking state of the summoner.
     * @param attacking true if summoner is attacking, false otherwise.
     */
    public void setAttacking(boolean attacking) {
        currentState = attacking ? State.ATTACKING : State.IDLE;
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
        if (!directionLocked) {
            moveDir = (velocity.x < 0) ? -1 : 1;
        }
        int checkX = (int) (center.x + moveDir * (width /4.0));
        int checkY = (int) (center.y + height/2.0 + 5);

        g2.setColor(Color.MAGENTA);
        g2.fillRect(checkX - (int) cam.x - 2, checkY - (int) cam.y - 2, 4, 4);

        Vector2 playerPos = GamePanel.player.getSolidAreaCenter();
        Vector2 currentPos = getSolidAreaCenter();

        double dist = currentPos.distanceTo(playerPos);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("Dist: " + dist/ts, (int) (position.x - cam.x), (int) (position.y - cam.y - 10));
    }

    /**
     * Handles damage taken by the summoner.
     * @param damage The amount of damage taken.
     * @param knockbackX The knockback force in the X direction.
     * @param knockbackY The knockback force in the Y direction.
     */
    public void hit(int damage, int knockbackX, int knockbackY) {
        if (spriteRow != 9 && image != imageHit) {
            currentHealth -= damage;

            if (currentAttack == null) {
                currentState = State.DAMAGED;
                lastDamagedTime = System.currentTimeMillis();
                spriteRow = 10;
                spriteCol = 0;
                maxSpriteCol = 1;
            }

            image = imageHit;
            super.hit();
            EnemySoundHandler.kingHit();
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
            spriteRow = 6;
            spriteCol = 0;
            maxSpriteCol = 10;
            velocity.x = 0;
            velocity.y = 0;

            GamePanel.points += (int) (5000 / (1 + 0.0003 * ((System.currentTimeMillis() - GamePanel.initialTime)/100.0)));
            EnemySoundHandler.stopKingFootsteps();
            EnemySoundHandler.kingDeath();
            GamePanel.backgroundMusic.fadeOut(2000);
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

    public void canMove (boolean canMove) {
        this.canMove = canMove;
    }
}

