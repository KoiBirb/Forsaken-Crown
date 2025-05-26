package Entitys.Enemies;

import Attacks.MeleeAttacks.MeleeAttack;
import Handlers.CollisionHandler;
import Handlers.ImageHandler;
import Handlers.Vector2;
import Handlers.SpikeDetectionHandler;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;

public class Ghoul extends Enemy {

    protected enum State { IDLE, WALK, DAMAGED}
    protected State currentState = State.IDLE;
    private double visionRadius = 200;

    public Ghoul(Vector2 pos) {
        super(pos, 1, 8, 62, 33, 100,  new Rectangle(0, 0, 20, 40));

        this.image = ImageHandler.loadImage("Assets/Images/Enemies/Ghoul/Ghoul Sprite Sheet 62 x 33.png");
    }

    public void update() {
        int ts = TiledMap.getScaledTileSize();
        Vector2 playerPos = GamePanel.player.getSolidAreaCenter();
        Vector2 currentPos = getSolidAreaCenter();

        //room check
        int myRoom = TiledMap.getRoomId(currentPos.x, currentPos.y);
        int playerRoom = TiledMap.getPlayerRoomId();
        boolean inSameRoom = myRoom == playerRoom;

        // los
        double dist = currentPos.distanceTo(playerPos);
        boolean inVision = dist <= visionRadius;

        boolean canSeePlayer = inSameRoom && inVision && hasLineOfSight(currentPos, playerPos);

        if (canSeePlayer) hasStartedChasing = true;

        Vector2 target = hasStartedChasing && canSeePlayer ? playerPos : spawnPos;
        double dx = target.x - currentPos.x;
        boolean closeX = Math.abs(dx) <= ts;

        CollisionHandler.checkTileCollision(this);
        boolean onGround = isOnGround();

        if (!closeX && hasStartedChasing && canSeePlayer && !hit) {
            velocity.x = Math.signum(dx) * getSpeed();
            direction = velocity.x < 0 ? "left" : "right";
            currentState = State.WALK;
            spriteRow = 1;
            maxSpriteCol = 7;

            long now = System.currentTimeMillis();

            boolean facingSpike = SpikeDetectionHandler.isFacingSpike(currentPos.x, currentPos.y, velocity.x, WIDTH, HEIGHT);
            boolean canLand = SpikeDetectionHandler.canLandAfterSpike(currentPos.x, currentPos.y, velocity.x, WIDTH, HEIGHT);

            if (facingSpike && canLand && onGround && !jumpedOut && now - lastJumpTime >= JUMP_COOLDOWN_MS) {
                velocity.y = JUMP_FORCE;
                jumpedOut = true;
                lastJumpTime = now;
            }

        } else if (!hit) {
            velocity.x = 0;
            currentState = State.IDLE;
            spriteRow = 0;
            maxSpriteCol = 0;
            spriteCol = 0;
        }

        if (!onGround) {
            velocity.y = Math.min(velocity.y + GRAVITY, TERMINAL_VELOCITY);
        } else {
            velocity.y = 0;
            jumpedOut = false;
        }

        if (currentState != State.IDLE) {
            spriteCounter++;
            if (spriteCounter >= 10) {
                spriteCounter = 0;
                spriteCol++;
            } if (spriteCol >= maxSpriteCol) {
                if (!hit)
                    spriteCol = 0;
                else {
                    spriteCol = 3;
                    hit = false;
                }
            }
        }

        super.update();
    }

    @Override
    public void draw(Graphics2D g2) {
        debugDraw(g2);
        Vector2 cam = GamePanel.tileMap.returnCameraPos();

        int sx = (int) (position.x - cam.x);
        int sy = (int) (position.y - cam.y - HEIGHT + 10);

        if ("left".equals(direction)) {
            g2.drawImage(
                image,
                sx + WIDTH * 2 - 50, sy, sx - 50, sy + HEIGHT * 2,
                spriteCol * WIDTH, spriteRow * HEIGHT,
                (spriteCol + 1) * WIDTH, (spriteRow + 1) * HEIGHT,
                null
            );
        } else {
            g2.drawImage(
                image,
                sx - 50, sy, sx + WIDTH * 2 - 50, sy + HEIGHT * 2,
                spriteCol * WIDTH, spriteRow * HEIGHT,
                (spriteCol + 1) * WIDTH, (spriteRow + 1) * HEIGHT,
                null
            );
        }
    }

    private void debugDraw(Graphics2D g2) {
        Vector2 cam = GamePanel.tileMap.returnCameraPos();

        // Draw vision radius
        g2.setColor(new Color(0, 0, 255, 64));
        int r = (int) visionRadius;
        Vector2 center = getSolidAreaCenter();
        g2.drawOval((int) (center.x - r - cam.x), (int) (center.y - r - cam.y), r * 2, r * 2);

        // Draw line to player using solid area centers
        Vector2 playerCenter = GamePanel.player.getSolidAreaCenter();
        int myRoom = TiledMap.getRoomId(center.x, center.y);
        int playerRoom = TiledMap.getPlayerRoomId();
        boolean inSameRoom = myRoom == playerRoom;
        boolean inVision = center.distanceTo(playerCenter) <= visionRadius;
        boolean canSee = inSameRoom && inVision && hasLineOfSight(center, playerCenter);

        g2.setColor(canSee ? Color.GREEN : Color.RED);
        g2.drawLine(
                (int) (center.x - cam.x), (int) (center.y - cam.y),
                (int) (playerCenter.x - cam.x), (int) (playerCenter.y - cam.y)
        );

        // Optionally: draw bounding box
        g2.setColor(Color.MAGENTA);
        Rectangle solid = getSolidArea();
        g2.drawRect((int) (solid.x - cam.x), (int) (solid.y - cam.y), solid.width, solid.height);
    }

    public void hit(int damage, int knockbackX, int knockbackY) {
        if (!hit){
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
            hit = true;
        }

        if (currentHealth <= 0) {
            death();
        }
    }
}
