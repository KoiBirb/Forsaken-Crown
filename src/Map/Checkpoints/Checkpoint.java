/*
 * Checkpoint.java
 * Leo Bogaert
 * Jun 4, 2025,
 * Handles the checkpoint logic and rendering
 */

package Map.Checkpoints;

import Handlers.ImageHandler;
import Handlers.Sound.SoundHandlers.PlayerSoundHandler;
import Handlers.Vector2;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;
import java.awt.image.VolatileImage;

public class Checkpoint {

    private final Vector2 position;
    private final Rectangle solidArea;
    private static final VolatileImage checkpointImage = ImageHandler.loadImage("Assets/Images/Tilesets/Objects/Save/spritesheet_vertical.png");

    private boolean isActive;
    private final int roomId;

    private int spriteCounter = 0, spriteCounterSpeed;
    private int row, col, maxCol;

    /**
     * Constructor for the Checkpoint class.
     * @param position The position of the checkpoint in the game world.
     * @param roomId The ID of the room where this checkpoint is located.
     */
    public Checkpoint(Vector2 position, int roomId) {
        this.position = position;
        this.isActive = false;
        this.roomId = roomId;

        this.solidArea = new Rectangle((int) position.x, (int) position.y, 60, 80);

        row = 3;
        col = 0;
        spriteCounterSpeed = 5;
        maxCol = 0;
    }

    /**
     * Updates the checkpoint state.
     */
    public void update() {
        if (TiledMap.getPlayerRoomId() == roomId) {
            if (!isActive && GamePanel.player.getSolidArea().intersects(solidArea)) {
                isActive = true;
                PlayerSoundHandler.checkpoint();
                maxCol = 6;
                GamePanel.player.setSpawnPosition(new Vector2(position.x + 20, position.y - 5));
            } else {
                int lives = GamePanel.player.getLives();

                if (row != 3) {
                    switch (lives) {
                        case 2 -> row = 1;
                        case 3 -> row = 2;
                        default -> row = 0;
                    }
                }

                spriteCounter++;
                if (spriteCounter > spriteCounterSpeed) {
                    spriteCounter = 0;
                    col++;
                    if (col >= maxCol) {
                        if (row == 3 && isActive) {
                            switch (lives) {
                                case 2 -> row = 1;
                                case 3 -> row = 2;
                                default -> row = 0;
                            }
                            spriteCounterSpeed = 14;
                            maxCol = 3;
                        }
                        col = 0;
                    }
                }
            }
        }
    }

    /**
     * Draws the checkpoint on the screen
     * @param g2 Graphics2D object to draw on
     */
    public void draw(Graphics2D g2) {
        if (TiledMap.getPlayerRoomId() == roomId) {

//            debugDraw(g2);

            Vector2 cameraPos = GamePanel.tileMap.returnCameraPos();

            double screenX = position.x - cameraPos.x;
            double screenY = position.y - cameraPos.y;

            g2.drawImage(checkpointImage,
                    (int) screenX, (int) screenY,
                    (int) (screenX + 16 * GamePanel.scale * 2), (int) (screenY + 19 * GamePanel.scale * 2),
                    col * 16, row * 19,
                    (col + 1) * 16, (row + 1) * 19, null);
        }
    }

    /**
     * Debug draw method to show hitbox
     * @param g2 graphics object to draw on
     */
    public void debugDraw(Graphics2D g2) {
        if (TiledMap.getPlayerRoomId() == roomId) {
            Vector2 cameraPos = GamePanel.tileMap.returnCameraPos();
            double screenX = solidArea.x - cameraPos.x;
            double screenY = solidArea.y - cameraPos.y;

            Color oldColor = g2.getColor();
            Composite oldComposite = g2.getComposite();

            g2.setColor(new Color(255, 0, 0, 128));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g2.fillRect((int) screenX, (int) screenY, solidArea.width, solidArea.height);

            g2.setColor(Color.RED);
            g2.setComposite(oldComposite);
            g2.drawRect((int) screenX, (int) screenY, solidArea.width, solidArea.height);

            g2.setColor(oldColor);
        }
    }
}
