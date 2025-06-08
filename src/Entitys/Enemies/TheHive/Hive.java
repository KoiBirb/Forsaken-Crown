/*
 * Hive.java
 * Leo Bogaert,
 * May 28, 2025,
 * Creates a Hive enemy that summons Wasps
 */

package Entitys.Enemies.TheHive;

import Entitys.Enemies.Enemy;
import Handlers.ImageHandler;
import Handlers.Sound.SoundHandlers.EnemySoundHandler;
import Handlers.Vector2;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;
import java.awt.image.VolatileImage;
import java.util.ArrayList;

public class Hive extends Enemy{

    // states
    public enum State {IDLE, EXPLODE, DEAD}

    private State currentState = State.IDLE;

    private final double visionRadius = 250;
    boolean summoned = false;
    private final ArrayList<Wasp> summons = new ArrayList<>();

    private boolean footstepsPlaying = false;

    private static final VolatileImage imageReg = ImageHandler.loadImage("Assets/Images/Enemies/The Hive/The Hive 78x43.png");

    /**
     * Hive constructor.
     * @param pos The initial position of the Hive.
     */
    public Hive(Vector2 pos) {
        super(pos, 0, 8, 78, 43, 1,  new Rectangle(0, 0, 55, 65));

        this.image = imageReg;

        spriteRow = 1;
        spriteCol = 0;
        maxSpriteCol = 8;
        spriteCounter = 0;
    }

    /**
     * Updates Hive state and behavior.
     */
    public void update() {

        if (currentState != State.DEAD) {

            if (!footstepsPlaying && currentState != State.EXPLODE) {
                EnemySoundHandler.hiveIdle();
                footstepsPlaying = true;
            }

            Vector2 playerPos = GamePanel.player.getSolidAreaCenter();
            Vector2 currentPos = getSolidAreaCenter();
            Vector2 topCenter = getSolidAreaXCenter();

            topCenter.set(topCenter.x, topCenter.y + 30);

            //room check
            int playerRoom = TiledMap.getPlayerRoomId();
            boolean inSameRoom = roomNumber == playerRoom;

            // line of sight
            double dist = currentPos.distanceTo(playerPos);
            boolean inVision = dist <= visionRadius;

            canSeePlayer = inSameRoom && inVision && hasLineOfSight(topCenter, playerPos);

            // Logic handling
            if (canSeePlayer) {
                death();
            }
        } else {
            canSeePlayer = false;
        }


        if (currentState != State.DEAD){
            spriteCounter++;
            if (spriteCounter >= 10) {
                spriteCounter = 0;
                spriteCol++;

                if (currentState == State.EXPLODE && spriteCol == 2) {
                    summon();
                    TiledMap.cameraShake(3, 2);
                }
                if (spriteCol >= maxSpriteCol) {
                    if (currentState == State.IDLE) {
                        spriteCol = 0;
                    } else if (currentState == State.EXPLODE) {
                        currentState = State.DEAD;
                    } else {
                        spriteCol = 0;
                    }
                }
            }
        }

        super.update();
    }

    /**
     * Stops buzzing idle sound
     */
    @Override
    public void stopSteps() {
        if (footstepsPlaying) {
            EnemySoundHandler.stopHiveIdle();
            footstepsPlaying = false;
        }
    }

    /**
     * Checks if idle sound is playing.
     * @return true if idle is playing, false otherwise.
     */
    @Override
    public boolean getFootstepsPlaying() {
        return footstepsPlaying;
    }

    /**
     * Checks for ledges.
     * @return false, Hive does not check for ledges.
     */
    public boolean isGroundAhead(double x, double y, double direction) {
        return false;
    }

    /**
     * Draws the Hive
     * @param g2 graphics object to draw on
     */
    @Override
    public void draw(Graphics2D g2) {
//        debugDraw(g2);
        Vector2 cam = GamePanel.tileMap.returnCameraPos();

        int sx = (int) (position.x - cam.x);
        int sy = (int) (position.y - cam.y - height + 10);


        g2.drawImage(
                image,
                sx - 45, sy + 35, sx + width * 2 - 45, sy + height * 2 + 35,
                spriteCol * width, spriteRow * height,
                (spriteCol + 1) * width, (spriteRow + 1) * height,
                null
        );
    }

    /**
     * Summons Wasps
     */
    private void summon(){
        summoned = true;
        double summonX = position.x;

        EnemySoundHandler.hiveExplode();

        stopSteps();

        for (int i = 1; i <= 6; i++) {
            double summonY = position.y + (int)(Math.random() * 61);
            Wasp wasp = new Wasp(new Vector2(summonX, summonY));
            GamePanel.enemies.add(wasp);
            summons.add(wasp);
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

        // hitbox
        g2.setColor(Color.MAGENTA);
        Rectangle solid = getSolidArea();
        g2.drawRect((int) (solid.x - cam.x), (int) (solid.y - cam.y), solid.width, solid.height);
    }

    /**
     * Handles damage taken by the Hive.
     * @param damage The amount of damage taken.
     * @param knockbackX The knockback force in the X direction.
     * @param knockbackY The knockback force in the Y direction.
     */
    public void hit(int damage, int knockbackX, int knockbackY) {}

    /**
     * Handles the death of the Hive.
     */
    public void death(){
        if (currentState != State.EXPLODE) {
            currentState = State.EXPLODE;
            spriteRow = 3;
            spriteCol = 0;
            maxSpriteCol = 7;
            velocity.x = 0;
            velocity.y = 0;
            GamePanel.points += 100;
            canSeePlayer = false;
        }
    }

    /**
     * Gets the current state of the Hive.
     * @return The current state.
     */
    public State getState(){
        return currentState;
    }

    /**
     * Sets the current state of the Hive.
     * @param state The new state to set.
     */
    public void setState(State state){
        this.currentState = state;
    }
}

