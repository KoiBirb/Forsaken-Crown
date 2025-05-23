package Entitys.Enemies;

import Handlers.CollisionHandler;
import Handlers.ImageHandler;
import Handlers.Vector2;
import Handlers.SpikeDetectionHandler;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Enemy extends Entitys.Entity {
    public static final int WIDTH = 62 * 2;
    public static final int HEIGHT = 66;

    private static final double GRAVITY = 0.8;
    private static final double TERMINAL_VELOCITY = 12;
    private static final double JUMP_FORCE = -8;

    private final Vector2 spawnPos;
    private final int detectionRadiusTiles;
    private boolean hasStartedChasing = false;
    private long lastJumpTime = 0;
    private long jumpKeyPressStartTime = 0;
    private static final long JUMP_COOLDOWN_MS = 1000;
    private static final long MAX_JUMP_DURATION_MS = 200;
    private static final long COYOTE_TIME = 100;
    private long lastGroundedTime = 0;

    private int spriteCounter = 0;
    private int spriteCol = 0;
    private int spriteRow = 0;
    private int maxSpriteCol = 7;
    private BufferedImage spriteSheet;

    private enum State { IDLE, WALK }
    private State currentState = State.IDLE;

    public Enemy(Vector2 pos, double speed, int detectionRadiusTiles) {
        super(pos, new Vector2(0, 0), WIDTH, HEIGHT, speed,
                new Rectangle(0, 0, WIDTH, HEIGHT), null, 3, 0);

        this.spawnPos = new Vector2(pos.x, pos.y);
        this.detectionRadiusTiles = detectionRadiusTiles;
        this.spriteSheet = ImageHandler.loadImage("Assets/Images/Enemies/Ghoul/Ghoul Sprite Sheet 62 x 33.png");
        this.image = spriteSheet;
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
        if (onGround) lastGroundedTime = System.currentTimeMillis();

        long now = System.currentTimeMillis();

        if (!closeX && hasStartedChasing && inRadius) {
            velocity.x = Math.signum(dx) * getSpeed();
            direction = velocity.x < 0 ? "left" : "right";
            currentState = State.WALK;
            spriteRow = 1;
            maxSpriteCol = 7;

            boolean wantsToJump = SpikeDetectionHandler.isFacingSpike(this) &&
                    SpikeDetectionHandler.canLandAfterSpike(this);

            if (wantsToJump && (onGround || now - lastGroundedTime <= COYOTE_TIME)) {
                if (jumpKeyPressStartTime == 0 && now - lastJumpTime >= JUMP_COOLDOWN_MS) {
                    jumpKeyPressStartTime = now;
                    lastJumpTime = now;
                }

                if (jumpKeyPressStartTime > 0 && now - jumpKeyPressStartTime <= MAX_JUMP_DURATION_MS) {
                    velocity.y = JUMP_FORCE;
                } else {
                    jumpKeyPressStartTime = 0;
                }
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

        BufferedImage frame = spriteSheet.getSubimage(spriteCol * spriteWidth, spriteRow * spriteHeight, spriteWidth, spriteHeight);
        int drawWidth = spriteWidth * 2;

        if ("left".equals(direction)) {
            g2.drawImage(frame, sx + drawWidth, sy, -drawWidth, spriteHeight * 2, null);
        } else {
            g2.drawImage(frame, sx, sy, drawWidth, spriteHeight * 2, null);
        }
    }

    @Override
    public void hit(int damage, int knockbackX, int knockbackY) {
        // Optional: damage logic
    }
}
