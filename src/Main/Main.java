/*
 * Main.java
 * Leo Bogaert
 * May 7, 2025,
 * Runnable class for Game
 */

package Main;

import Main.Panels.DeathPanel;
import Main.Panels.GamePanel;
import Main.Panels.MenuPanel;

import javax.swing.*;
import java.awt.*;

public class Main {

    public enum GameState {MENU, GAME, DEATH}
    public static GameState gameState = GameState.MENU;
    private static final CardLayout cardLayout = new CardLayout();
    private static final JPanel mainPanel = new JPanel(cardLayout);

    public static KeyInput keyI = new KeyInput();

    public static GamePanel gamePanel = new GamePanel();
    public static MenuPanel menuPanel = new MenuPanel();
    public static DeathPanel deathPanel = new DeathPanel();

    public static JFrame window = new JFrame();

    /**
     * Main method
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // Change window settings
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Forsaken Crown");
        window.setUndecorated(true);

        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(gamePanel, "GAME");
        mainPanel.add(deathPanel, "DEATH");

        window.add(mainPanel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        switchToDeath();
    }

    /**
     * Switch to the menu
     */
    public static void switchToMenu() {
        gameState = GameState.MENU;
        cardLayout.show(mainPanel, "MENU");
        menuPanel.setup();
    }

    /**
     * Switch to the death screen
     */
    public static void switchToDeath() {
        gameState = GameState.DEATH;
        cardLayout.show(mainPanel, "DEATH");
        deathPanel.setup();
    }

    /**
     * Switch to the game
     */
    public static void switchToGame() {
        gameState = GameState.GAME;
        cardLayout.show(mainPanel, "GAME");
        gamePanel.setupGame();
    }
}