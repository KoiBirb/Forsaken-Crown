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

    public Ghoul(Vector2 pos, double speed, int detectionRadiusTiles) {
        super(pos, speed, detectionRadiusTiles, 62, 33);

        this.image = ImageHandler.loadImage("Assets/Images/Enemies/Ghoul/Ghoul Sprite Sheet 62 x 33.png");
    }

    @Override
    public void update() {
        int ts = TiledMap.getScaledTileSize();
        Vector2 playerPos = GamePanel.player.getPosition();
        double distanceX = Math.abs(playerPos.x - spawnPos.x);
        boolean inRadius = distanceX <= detectionRadiusTiles * ts;

        if (inRadius) hasStartedChasing = true;

        Vector2 target = hasStartedChasing && inRadius ? playerPos : spawnPos;
        double dx = target.x - position.x;
        boolean closeX = Math.abs(dx) <= ts;

        CollisionHandler.checkTileCollision(this);
        boolean onGround = isOnGround();

        if (!closeX && hasStartedChasing && inRadius) {
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
        Vector2 cam = GamePanel.tileMap.returnCameraPos();
        int spriteWidth = 62;
        int spriteHeight = 33;

        int sx = (int) (position.x - cam.x);
        int sy = (int) (position.y - cam.y - (spriteHeight * 2 - HEIGHT));
        int drawWidth = spriteWidth * 2;

        if ("left".equals(direction)) {
            g2.drawImage(
                    image,
                    sx + drawWidth, sy, sx, sy + spriteHeight * 2,
                    spriteCol * spriteWidth, spriteRow * spriteHeight,
                    (spriteCol + 1) * spriteWidth, (spriteRow + 1) * spriteHeight,
                    null
            );
        } else {
            g2.drawImage(
                    image,
                    sx, sy, sx + drawWidth, sy + spriteHeight * 2,
                    spriteCol * spriteWidth, spriteRow * spriteHeight,
                    (spriteCol + 1) * spriteWidth, (spriteRow + 1) * spriteHeight,
                    null
            );
        }
    }

    @Override
    public void hit(int damage, int knockbackX, int knockbackY) {
        // Optional: damage logic
    }
}
