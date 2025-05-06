package Main.UI;

import Entitys.Entity;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Random;

public class ManaBar extends HealthBar {

    public ManaBar(Entity entity, int x, int y, int width, int height) {
        super(entity, x, y, width, height);
        this.entity = entity;
        this.scale = 4;
        this.shakeOffsetX = 0;
        this.shakeOffsetY = 0;
        this.shakeTimer = 0;
    }

    public void update() {
        int previousMana = segments != null ? calculateTotalMana(segments) : 0;
        segments = calculateManaSegments(entity.getCurrentMana());

        if (entity.getCurrentMana() < previousMana) {
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

    private int calculateTotalMana (int[][] segments) {
        int totalHealth = 0;
        if (segments[0][0] == 10) totalHealth += 1; // Left edge
        if (segments[3][0] == 10) totalHealth += 1; // Right edge
        if (segments[1][0] == 11) totalHealth += 4 - (segments[1][1] - 8); // Body 1
        if (segments[2][0] == 11) totalHealth += 4 - (segments[2][1] - 8); // Body 2
        return totalHealth;
    }

    public static int[][] calculateManaSegments(int health) {
        int[][] segments = new int[4][2]; // [leftEdge, body1, body2, rightEdge]

        // Left edge
        if (health > 0) {
            segments[0][0] = 10;
            segments[0][1] = 8;
            health -= 1;
        } else {
            segments[0][0] = 13;
            segments[0][1] = 8;
        }

        // Body segments
        if (health > 0) {
            int remainingHealth = Math.min(4, health);

            switch (remainingHealth) {
                case 4 -> {
                    segments[1][0] = 11;
                    segments[1][1] = 8;
                }
                case 3 -> {
                    segments[1][0] = 11;
                    segments[1][1] = 9;
                }
                case 2 -> {
                    segments[1][0] = 11;
                    segments[1][1] = 10;
                }
                case 1 -> {
                    segments[1][0] = 11;
                    segments[1][1] = 11;
                }
            }

            health -= remainingHealth;
        } else {
            segments[1][0] = 12;
            segments[1][1] = 8;
        }

        if (health > 0) {
            int remainingHealth = Math.min(4, health);

            switch (remainingHealth) {
                case 4 -> {
                    segments[2][0] = 11;
                    segments[2][1] = 8;
                }
                case 3 -> {
                    segments[2][0] = 11;
                    segments[2][1] = 9;
                }
                case 2 -> {
                    segments[2][0] = 11;
                    segments[2][1] = 10;
                }
                case 1 -> {
                    segments[2][0] = 11;
                    segments[2][1] = 11;
                }
            }
            health -= remainingHealth;
        } else {
            segments[2][0] = 12;
            segments[2][1] = 8;
        }

        // Right edge
        if (health > 0) {
            segments[3][0] = 10;
        } else {
            segments[3][0] = 13;
        }
        segments[3][1] = 8;

        return segments;
    }

    public void draw(Graphics2D g2) {
        int col, row;

        // health bar
        for (int i = 0; i < 4; i++) {
            col = segments[i][0];
            row = segments[i][1];

            AffineTransform originalTransform = g2.getTransform();

            if ((i == 3 && segments[i][0] == 10) || (i == 0 && segments[i][0] == 13)) {
                g2.scale(-1, 1);
                g2.drawImage(
                        imageGlow,
                        -(x + shakeOffsetX + i * 16 * scale + 16 * scale), y + shakeOffsetY, -(x + shakeOffsetX + i * 16 * scale), y + shakeOffsetY + 16 * scale, // Adjusted for flipped coordinates
                        col * 16, row * 16,
                        (col + 1) * 16, (row + 1) * 16,
                        null
                );
            } else {
                g2.drawImage(
                        imageGlow,
                        x + shakeOffsetX + i * 16 * scale, y + shakeOffsetY, x + shakeOffsetX + (16 * scale) + (i * 16 * scale), y + shakeOffsetY + 16 * scale,    // dest rectangle
                        col * 16, row * 16,
                        (col + 1) * 16, (row + 1) * 16,
                        null
                );
            }

            g2.setTransform(originalTransform);
        }
    }
}