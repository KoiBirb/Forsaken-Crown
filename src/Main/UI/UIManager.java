/*
 * UIManager.java
 * Leo Bogaert
 * May 7, 2025,
 * Manages UI elements
 */
package Main.UI;

import Main.Main;
import Main.Panels.GamePanel;
import Main.UI.Buttons.ButtonManager;

import java.awt.*;

import static Main.Main.gameState;

public class UIManager {

    private final HealthBar healthBar;
    private final ManaBar manaBar;
    private final ButtonManager buttonManager;

    private int selectedButton;

    /**
     * Constructor for UIManager
     * Creates UI elements
     */
    public UIManager() {
        healthBar = new HealthBar(GamePanel.player, 55, 160, 400, 100);
        manaBar = new ManaBar(GamePanel.player, 55, 110, 400, 100);
        buttonManager = new ButtonManager();
        selectedButton = 0;
    }

    /**
     * Update the UI elements
     */
    public void update() {
        if (gameState == Main.GameState.GAME) {
            healthBar.update();
            manaBar.update();
        } else {
            buttonManager.update();
            selectedButton = buttonManager.getSelectedIndex();
        }
    }

    /**
     * Get the selected button
     * @return int index of the selected button
     */
    public int getSelectedButton() {
        return selectedButton;
    }

    /**
     * Draw the UI elements
     * @param g2 Graphics2D object to draw on
     */
    public void draw(Graphics2D g2) {
        if (gameState == Main.GameState.GAME) {
            healthBar.draw(g2);
            manaBar.draw(g2);
        } else {
            buttonManager.draw(g2);
        }
    }
}
