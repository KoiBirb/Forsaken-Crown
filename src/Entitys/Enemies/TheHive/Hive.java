/*
 * SkeletonSummoner.java
 * Leo Bogaert
 * May 28, 2025,
 * Extends enemy, represents a skeleton summoner enemy that can summon skeletons and attack the player.
 */

package Entitys.Enemies.TheHive;

import Entitys.Enemies.Enemy;
import Handlers.ImageHandler;
import Handlers.Sound.SoundHandlers.EnemySoundHandler;
import Handlers.Vector2;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;
import java.util.ArrayList;

public class Hive extends Enemy{

    // states
    public enum State {IDLE, EXPLODE, DEAD}

    private State currentState = State.IDLE;

    private final double visionRadius = 300;
    boolean summoned = false;
    private final ArrayList<Wasp> summons = new ArrayList<>();

    private boolean footstepsPlaying = false;

    /**
     * Summoner constructor.
     * @param pos The initial position of the summoner.
     */
    public Hive(Vector2 pos) {
        super(pos, 0, 8, 78, 43, 1,  new Rectangle(0, 0, 55, 65));

        this.image = ImageHandler.loadImage("Assets/Images/Enemies/The Hive/The Hive 78x43.png");

        spriteRow = 1;
        spriteCol = 0;
        maxSpriteCol = 8;
        spriteCounter = 0;
    }

    /**
     * Updates summoner state and behavior.
     */
    public void update() {

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

                if (currentState == State.EXPLODE && spriteCol == 2)
                    summon();
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

    @Override
    public void stopSteps() {
        if (footstepsPlaying) {
            EnemySoundHandler.stopSummonerFootsteps();
            footstepsPlaying = false;
        }
    }

    @Override
    public boolean getFootstepsPlaying() {
        return footstepsPlaying;
    }

    /**
     * Checks for ledges.
     * @return true if no ledge, false otherwise.
     */
    public boolean isGroundAhead(double x, double y, double direction) {
        return false;
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


        g2.drawImage(
                image,
                sx - 45, sy + 35, sx + width * 2 - 45, sy + height * 2 + 35,
                spriteCol * width, spriteRow * height,
                (spriteCol + 1) * width, (spriteRow + 1) * height,
                null
        );
    }

    /**
     * Summons skeletons
     */
    private void summon(){
        summoned = true;
        double summonX = position.x;

        for (int i = 1; i <= 6; i++) {
            Wasp wasp = new Wasp(new Vector2(summonX, position.y + 30));
            GamePanel.enemies.add(wasp);
            summons.add(wasp);
        }

        EnemySoundHandler.summonerSummon();
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

        // hitbox
        g2.setColor(Color.MAGENTA);
        Rectangle solid = getSolidArea();
        g2.drawRect((int) (solid.x - cam.x), (int) (solid.y - cam.y), solid.width, solid.height);
    }

    /**
     * Handles damage taken by the summoner.
     * @param damage The amount of damage taken.
     * @param knockbackX The knockback force in the X direction.
     * @param knockbackY The knockback force in the Y direction.
     */
    public void hit(int damage, int knockbackX, int knockbackY) {}

    /**
     * Handles the death of the summoner.
     */
    public void death(){
        if (currentState != State.EXPLODE) {
            currentState = State.EXPLODE;
            spriteRow = 3;
            spriteCol = 0;
            maxSpriteCol = 7;
            velocity.x = 0;
            velocity.y = 0;
            GamePanel.points += 50;
            canSeePlayer = false;
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

