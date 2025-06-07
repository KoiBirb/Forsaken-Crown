/*
 * Main.java
 * Leo Bogaert
 * May 7, 2025,
 * Runnable class for Game
 */

package Main;

import Handlers.ScoreHandler;
import Main.Panels.EndPanel;
import Main.Panels.GamePanel;
import Main.Panels.MenuPanel;

import javax.swing.*;
import java.awt.*;

public class Main {

    public enum GameState {MENU, GAME, DEATH, VICTORY}
    public static GameState gameState = GameState.MENU;
    private static final CardLayout cardLayout = new CardLayout();
    private static final JPanel mainPanel = new JPanel(cardLayout);

    public static KeyInput keyI = new KeyInput();

    public static GamePanel gamePanel = new GamePanel();
    public static MenuPanel menuPanel = new MenuPanel();
    public static EndPanel endPanel = new EndPanel();
    public static String name = ScoreHandler.generateRandomName();

    public static JFrame window = new JFrame();

    /**
     * Main method
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        ScoreHandler.readScoresFromFile("src/Assets/Map/Leaderboard.txt");

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Forsaken Crown");
        window.setUndecorated(true);

        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(gamePanel, "GAME");
        mainPanel.add(endPanel, "END");

        window.add(mainPanel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        switchToMenu();
    }

    /**
     * Switch to the menu
     */
    public static void switchToMenu() {
        gameState = GameState.MENU;
        EndPanel.endThread = null;
        GamePanel.gameThread = null;
        cardLayout.show(mainPanel, "MENU");
        menuPanel.setup();
    }

    /**
     * Switch to the death screen
     */
    public static void switchToEnd(boolean victory) {
        if (victory) {
            EndPanel.victory = true;
            gameState = GameState.VICTORY;
        } else {
            EndPanel.victory = false;
            gameState = GameState.DEATH;
        }

        MenuPanel.menuThread = null;
        GamePanel.gameThread = null;
        cardLayout.show(mainPanel, "END");
        endPanel.setup();
    }

    /**
     * Switch to the game
     */
    public static void switchToGame() {
        gameState = GameState.GAME;
        MenuPanel.menuThread = null;
        EndPanel.endThread = null;
        cardLayout.show(mainPanel, "GAME");
        gamePanel.setupGame();
    }
}