/*
 * Main.java
 * Leo Bogaert
 * May 7, 2025,
 * Runnable class for Game
 */

package Main;

import Handlers.EnemySpawnHandler;
import Handlers.ScoreHandler;
import Handlers.Sound.SoundHandlers.EnemySoundHandler;
import Main.Panels.EndPanel;
import Main.Panels.GamePanel;
import Main.Panels.MenuPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

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

        ScoreHandler.readScoresFromFile();

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Forsaken Crown");
        window.setUndecorated(true);

        hideCursor(gamePanel);
        hideCursor(endPanel);
        hideCursor(menuPanel);

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
        EnemySoundHandler.muteAll();

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
        EnemySoundHandler.unmuteAll();
        gamePanel.setupGame();
    }

    public static void hideCursor(JPanel panel) {
        panel.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0),
                "null"));
    }
}