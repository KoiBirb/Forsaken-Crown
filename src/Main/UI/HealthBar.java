/*
* HealthBar.java
* Leo Bogaert
* May 7, 2025,
* Renders and updates a health bar for an entity
*/

package Main.UI;

import Entitys.Entity;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Random;

public class HealthBar extends UIElement {

    protected Entity entity;
    protected int[][] segments;
    protected int scale;
    protected int shakeOffsetX, shakeOffsetY;
    protected long shakeTimer;

    /**
     * Constructor for HealthBar
     * @param entity Entity to which the health bar belongs to
     * @param x int x position of the health bar
     * @param y int y position of the health bar
     * @param width int width of the health bar
     * @param height int height of the health bar
     */
    public HealthBar(Entity entity, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.entity = entity;
        this.scale = 4;
        this.shakeOffsetX = 0;
        this.shakeOffsetY = 0;
        this.shakeTimer = 0;
    }

    /**
     * Update the health bar
     */
    public void update() {
        int previousHealth = segments != null ? calculateTotalHealth(segments) : 0;
        segments = calculateHealthSegments(entity.getCurrentHealth());

        if (entity.getCurrentHealth() < previousHealth) {
            triggerShake();
        }

        if (shakeTimer > 0) {
            shakeTimer -= 16;
            Random random = new Random();
            shakeOffsetX = random.nextInt(6) - 3;
            shakeOffsetY = random.nextInt(6) - 3;
        } else {
            shakeOffsetX = 0;
            shakeOffsetY = 0;
        }
    }

    /**
     * Trigger a shake effect on the health bar
     */
    protected void triggerShake() {
        shakeTimer = 200; // Shake duration in milliseconds
    }

    /**
     * Calculate the total health based on the segments
     * @param segments int[][] segments of health
     * @return int total health
     */
    private int calculateTotalHealth(int[][] segments) {
        int totalHealth = 0;
        if (segments[0][0] == 14) totalHealth += 1; // Left edge
        if (segments[3][0] == 14) totalHealth += 1; // Right edge
        if (segments[1][0] == 15) totalHealth += 4 - (segments[1][1] - 8); // Body 1
        if (segments[2][0] == 15) totalHealth += 4 - (segments[2][1] - 8); // Body 2
        return totalHealth;
    }

    /**
     * Calculate the health segments based on the current health
     * @param health int current health of the entity
     * @return int[][] segments of health
     */
    public static int[][] calculateHealthSegments(int health) {
        int[][] segments = new int[4][2]; // [leftEdge, body1, body2, rightEdge]

        // Left edge
        if (health > 0) {
            segments[0][0] = 14;
            segments[0][1] = 8;
            health -= 1;
        } else {
            segments[0][0] = 17;
            segments[0][1] = 8;
        }

        // Body segments
        if (health > 0) {
            int remainingHealth = Math.min(4, health);

            switch (remainingHealth) {
                case 4 -> {
                    segments[1][0] = 15;
                    segments[1][1] = 8;
                }
                case 3 -> {
                    segments[1][0] = 15;
                    segments[1][1] = 9;
                }
                case 2 -> {
                    segments[1][0] = 15;
                    segments[1][1] = 10;
                }
                case 1 -> {
                    segments[1][0] = 15;
                    segments[1][1] = 11;
                }
            }

            health -= remainingHealth;
        } else {
            segments[1][0] = 16;
            segments[1][1] = 8;
        }

        if (health > 0) {
            int remainingHealth = Math.min(4, health);

            switch (remainingHealth) {
                case 4 -> {
                    segments[2][0] = 15;
                    segments[2][1] = 8;
                }
                case 3 -> {
                    segments[2][0] = 15;
                    segments[2][1] = 9;
                }
                case 2 -> {
                    segments[2][0] = 15;
                    segments[2][1] = 10;
                }
                case 1 -> {
                    segments[2][0] = 15;
                    segments[2][1] = 11;
                }
            }
            health -= remainingHealth;
        } else {
            segments[2][0] = 16;
            segments[2][1] = 8;
        }

        // Right edge
        if (health > 0) {
            segments[3][0] = 14;
        } else {
            segments[3][0] = 17;
        }
        segments[3][1] = 8;

        return segments;
    }

    /**
     * Draw the health bar
     * @param g2 Graphics2D object to draw on
     */
    public void draw(Graphics2D g2) {
        int col, row;

        // health bar
        for (int i = 0; i < 4; i++) {
            col = segments[i][0];
            row = segments[i][1];

            AffineTransform originalTransform = g2.getTransform();

            if ((i == 3 && segments[i][0] == 14) || (i == 0 && segments[i][0] == 17)) {
                g2.scale(-1, 1);
                g2.drawImage(
                        imageGlow,
                    -(x + shakeOffsetX + i * 16 * scale + 16 * scale), y + shakeOffsetY, -(x + shakeOffsetX + i * 16 * scale), y + shakeOffsetY + 16 * scale, // Adjusted for flipped coordinates
                    col * 16, row * 16,
                    (col + 1) * 16, (row + 1) * 16,    // src rectangle
                    null
                );
            } else {
                g2.drawImage(
                        imageGlow,
                    x + shakeOffsetX + i * 16 * scale, y + shakeOffsetY, x + shakeOffsetX + (16 * scale) + (i * 16 * scale), y + shakeOffsetY + 16 * scale,    // dest rectangle
                    col * 16, row * 16,
                    (col + 1) * 16, (row + 1) * 16,    // src rectangle
                    null
                );
            }

            g2.setTransform(originalTransform);
        }

        // emblem

        g2.drawImage(
                image,
                x + shakeOffsetX + 24, y + 45, (x + shakeOffsetX + (69 * 3)) + 24, (y + 14 * 3) + 45,
                206, 39, 273, 52, null);

        float alpha = (float) (0.75 + 0.25 * Math.sin(System.currentTimeMillis() * 0.002));
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        g2.drawImage(
                imageGlow,
                x + shakeOffsetX + 23, y + 44,
                (x + shakeOffsetX + (69 * 3)) + 25, (y + 14 * 3) + 46,
                206, 39, 273, 52, null);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
}