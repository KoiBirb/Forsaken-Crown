package Entitys.Enemies;

import Attacks.MeleeAttacks.Enemies.GhoulAttack;
import Entitys.Enemies.Summoner.Skeleton;
import Handlers.CollisionHandler;
import Handlers.ImageHandler;
import Handlers.Sound.EnemySoundHandler;
import Handlers.Vector2;
import Handlers.SpikeDetectionHandler;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;

public class Ghoul extends Enemy {

    public enum State {IDLE, WALK, DAMAGED, ATTACKING, DEAD}
    private boolean idleForward = true, footstepsPlaying = false;
    protected State currentState = State.IDLE;
    private final double visionRadius = 200;
    private long lastAttackTime = 0;

    public Ghoul(Vector2 pos) {
        super(pos, 1, 8, 62, 33, 3,  new Rectangle(0, 0, 20, 40));

        this.image = ImageHandler.loadImage("Assets/Images/Enemies/Ghoul/Ghoul Sprite Sheet 62 x 33.png");
    }

    public void update() {

        if (currentState != State.DEAD) {
            int ts = TiledMap.getScaledTileSize();
            Vector2 playerPos = GamePanel.player.getSolidAreaCenter();
            Vector2 currentPos = getSolidAreaCenter();
            Vector2 currentTopPos = getSolidAreaXCenter();

            //room check
            int myRoom = TiledMap.getRoomId(currentPos.x, currentPos.y);
            int playerRoom = TiledMap.getPlayerRoomId();
            boolean inSameRoom = myRoom == playerRoom;

            // los
            double dist = currentPos.distanceTo(playerPos);
            boolean inVision = dist <= visionRadius;

            boolean canSeePlayer = inSameRoom && inVision && hasLineOfSight(currentTopPos, GamePanel.player.getSolidAreaXCenter());

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

    public boolean isGroundAhead(double x, double y, double direction) {
        // Check a point just ahead of the Ghoul's feet in the direction of movement
        int checkX = (int) (x + direction * ((double) width/2.0));
        int checkY = (int) (y + (double) height + 2);
        return CollisionHandler.isSolidTileAt(checkX, checkY);
    }

    private void debugDraw(Graphics2D g2) {
        Vector2 cam = GamePanel.tileMap.returnCameraPos();

        // Draw vision radius
        g2.setColor(new Color(0, 0, 255, 64));
        int r = (int) visionRadius;
        Vector2 center = getSolidAreaCenter();
        g2.drawOval((int) (center.x - r - cam.x), (int) (center.y - r - cam.y), r * 2, r * 2);

        Vector2 playerCenter = GamePanel.player.getSolidAreaXCenter();
        Vector2 topCenter = getSolidAreaXCenter();
        int myRoom = TiledMap.getRoomId(center.x, center.y);
        int playerRoom = TiledMap.getPlayerRoomId();
        boolean inSameRoom = myRoom == playerRoom;
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

    public void hit(int damage, int knockbackX, int knockbackY) {
        if (canBeHit() && !hit){
            currentHealth -= damage;

            Vector2 ghoulCenter = getSolidAreaCenter();
            Vector2 playerCenter = GamePanel.player.getSolidAreaCenter();

            if (ghoulCenter.x > playerCenter.x) {
                knockbackX = -Math.abs(knockbackX);
            } else {
                knockbackX = Math.abs(knockbackX);
            }

            velocity.x += knockbackX;
            velocity.y += knockbackY;
            position.x += knockbackX;
            position.y += knockbackY;

            if (velocity.x > 0) {
                direction = "right";
            } else if (velocity.x < 0) {
                direction = "left";
            }

            spriteRow = 3;
            spriteCol = 0;
            maxSpriteCol = 3;

            currentState = State.DAMAGED;

            EnemySoundHandler.stopGhoulAttack();

            if (currentHealth > 0)
                EnemySoundHandler.ghoulHit();

            hit = true;
        }

        if (currentHealth <= 0) {
            death();
        }
    }

    public void death(){
        if (currentState != State.DEAD) {
            currentState = State.DEAD;
            spriteRow = 4;
            spriteCol = 0;
            maxSpriteCol = 6;
            velocity.x = 0;
            velocity.y = 0;
            EnemySoundHandler.stopGhoulAttack();
            EnemySoundHandler.stopGhoulFootsteps();
            EnemySoundHandler.ghoulDeath();
        }
    }

    public State getState(){
        return currentState;
    }

    public void setState(State state){
        this.currentState = state;
    }
}
