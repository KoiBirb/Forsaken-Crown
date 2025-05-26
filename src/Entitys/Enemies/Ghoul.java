package Entitys.Enemies;

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

    private double visionRadius = 200;

    public Ghoul(Vector2 pos, int detectionRadiusTiles) {
        super(pos, 1, detectionRadiusTiles, 62, 33, 3,  new Rectangle(0, (int) 0, 20, 40));

        this.image = ImageHandler.loadImage("Assets/Images/Enemies/Ghoul/Ghoul Sprite Sheet 62 x 33.png");
    }

    public void update() {
        int ts = TiledMap.getScaledTileSize();
        Vector2 playerPos = GamePanel.player.getPosition();

        // 1. Room check
        int myRoom = TiledMap.getRoomId(position.x, position.y);
        int playerRoom = TiledMap.getPlayerRoomId();
        boolean inSameRoom = myRoom == playerRoom;

        // 2. Circular vision range
        double dist = position.distanceTo(playerPos);
        boolean inVision = dist <= visionRadius;

        // 3. Line of sight
        boolean canSeePlayer = inSameRoom && inVision && hasLineOfSight(position, playerPos);

        if (canSeePlayer) hasStartedChasing = true;

        Vector2 target = hasStartedChasing && canSeePlayer ? playerPos : spawnPos;
        double dx = target.x - position.x;
        boolean closeX = Math.abs(dx) <= ts;

        CollisionHandler.checkTileCollision(this);
        boolean onGround = isOnGround();

        if (!closeX && hasStartedChasing && canSeePlayer) {
            velocity.x = Math.signum(dx) * getSpeed();
            direction = velocity.x < 0 ? "left" : "right";
            currentState = State.WALK;
            spriteRow = 1;
            maxSpriteCol = 7;

            long now = System.currentTimeMillis();

            boolean facingSpike = SpikeDetectionHandler.isFacingSpike(position.x, position.y, velocity.x, WIDTH, HEIGHT);
            boolean canLand = SpikeDetectionHandler.canLandAfterSpike(position.x, position.y, velocity.x, WIDTH, HEIGHT);

            if (facingSpike && canLand && onGround && !jumpedOut && now - lastJumpTime >= JUMP_COOLDOWN_MS) {
                velocity.y = JUMP_FORCE;
                jumpedOut = true;
                lastJumpTime = now;
            }

        } else {
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

        if (currentState == State.WALK) {
            spriteCounter++;
            if (spriteCounter >= 12) {
                spriteCounter = 0;
                spriteCol = (spriteCol + 1) % (maxSpriteCol + 1);
            }
        }

        position.x += velocity.x;
        position.y += velocity.y;

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
        g2.drawOval((int) (position.x - r - cam.x), (int) (position.y - r - cam.y), r * 2, r * 2);

        // Draw line to player if in same room
        Vector2 playerPos = GamePanel.player.getPosition();
        int myRoom = TiledMap.getRoomId(position.x, position.y);
        int playerRoom = TiledMap.getPlayerRoomId();
        boolean inSameRoom = myRoom == playerRoom;
        boolean inVision = position.distanceTo(playerPos) <= visionRadius;
        boolean canSee = inSameRoom && inVision && hasLineOfSight(position, playerPos);

        g2.setColor(canSee ? Color.GREEN : Color.RED);
        g2.drawLine(
            (int) (position.x - cam.x), (int) (position.y - cam.y),
            (int) (playerPos.x - cam.x), (int) (playerPos.y - cam.y)
        );

        // Optionally: draw bounding box
        g2.setColor(Color.MAGENTA);
        Rectangle solid = getSolidArea();
        g2.drawRect((int) (solid.x - cam.x), (int) (solid.y - cam.y), solid.width, solid.height);
    }

    @Override
    public void hit(int damage, int knockbackX, int knockbackY) {
        currentHealth -= damage;
        if (currentHealth <= 0) {
            death();
        } else {
            velocity.x += knockbackX;
            velocity.y += knockbackY;
            position.x += knockbackX;
            position.y += knockbackY;
            if (velocity.x > 0) {
                direction = "right";
            } else if (velocity.x < 0) {
                direction = "left";
            }
        }
    }
}
