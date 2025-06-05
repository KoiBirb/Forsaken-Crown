/*
 * GamePanel.java
 * Leo Bogaert
 * May 7, 2025,
 * Main game loop
 */

package Main.Panels;

import Attacks.MeleeAttacks.MeleeAttack;
import Entitys.Enemies.Enemy;
import Entitys.Player;
import Handlers.CollisionHandler;
import Handlers.Sound.SoundHandlers.BackgroundMusicHandler;
import Handlers.Vector2;
import Handlers.EnemySpawnHandler;
import Main.UI.UIManager;
import Main.UI.VFX.Effect;
import Map.Checkpoints.CheckpointManager;
import Map.TiledMap;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static Main.Main.keyI;

public class GamePanel extends JPanel implements Runnable{

    // Screen settings
    public final static double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    public final static double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    public final static double scale = 2;

    // initialize classes
    public static TiledMap tileMap;
    public static Player player;
    public static UIManager ui;
    public static CheckpointManager checkpointManager;
    public static BackgroundMusicHandler backgroundMusic = new BackgroundMusicHandler();;

    public static ArrayList<MeleeAttack> playerAttacks = new ArrayList<>();
    public static ArrayList<MeleeAttack> enemyAttacks = new ArrayList<>();
    public static ArrayList<Enemy> enemies = new ArrayList<>();
    public static ArrayList<Effect> effects = new ArrayList<>();

    public static ArrayList<Enemy> activeEnemies = new ArrayList<>();

    public static long initialTime;
    public static int points;

    // Room change effect
    public static boolean fading = false;
    private static double fadeAlpha = 0.0;
    private static boolean fadeIn = true;
    private int fadeDelay = 10;
    private int fadeDelayCounter = 0;

    public static Thread gameThread;

    /**
     * Constructor for the GamePanel class.
     */
    public GamePanel(){
        this.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);

        this.addKeyListener(keyI);

        // initialize classes

        tileMap = new TiledMap();
    }

    /**
     * Set up the game.
     */
    public void setupGame() {
        this.requestFocusInWindow();
        player = new Player(new Vector2(100,100));
//        player = new Player(new Vector2(2300,2500));
//        player = new Player(new Vector2(2993,1200));
        ui = new UIManager(player, true);
        checkpointManager = new CheckpointManager();
        EnemySpawnHandler.setup();
        initialTime = System.currentTimeMillis();
        points = 0;
        startThread();
    }

    /**
     * Start room change animation.
     */
    public static void roomTransition() {
        fading = true;
        fadeAlpha = 0.0;
        fadeIn = true;
    }

    /**
     * Starts game thread
     */
    public void startThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Delta FPS clock to call update and repaint
     */
    @Override
    public void run() {
        final double drawInterval = 1_000_000_000.0 / 60.0; // 60 FPS
        long lastTime = System.nanoTime();
        double delta = 0;

        while (gameThread != null) {
            long now = System.nanoTime();
            delta += (now - lastTime) / drawInterval;
            lastTime = now;

            while (delta >= 1) {
                update();
                repaint();
                delta--;
            }

            // Optional: sleep a bit to reduce CPU usage
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Update game objects
     */
    public void update() {
        if (player != null) {
            player.update();
            tileMap.update();
        }

        backgroundMusic.update();
        EnemySpawnHandler.updateAll();
        checkpointManager.update();
        ui.update();

        for (int i = 0; i < playerAttacks.size(); i++) {
            playerAttacks.get(i).update();
        }

        for (int i = 0; i < enemyAttacks.size(); i++) {
            enemyAttacks.get(i).update();
        }

        for (int i = 0; i < effects.size(); i++) {
            effects.get(i).update();
        }

        for (int i = 0; i < activeEnemies.size(); i++) {
            if (!enemies.contains(activeEnemies.get(i))){
                activeEnemies.remove(activeEnemies.get(i));
            }
        }

        for (int i = 0; i < enemies.size(); i++) {
            for (int j = 0; j < playerAttacks.size(); j++) {
                if (CollisionHandler.attackCollision(playerAttacks.get(j), enemies.get(i))) {
                    enemies.get(i).hit(playerAttacks.get(j).getDamage(), 3, 3);
                }
            }
        }

        for (int j = 0; j < enemyAttacks.size(); j++) {
            if (CollisionHandler.attackCollision(enemyAttacks.get(j), player)) {
                player.hit(enemyAttacks.get(j).getDamage(), 0, 0);
            }
        }

        // Fading
        if (fading) {
            if (fadeIn) {
                fadeAlpha += 0.09;
                if (fadeAlpha >= 1.0) {
                    fadeAlpha = 1.0;
                    fadeIn = false;
                    fadeDelayCounter = fadeDelay;
                    tileMap.updatePlayerRoom();
                }
            } else if (fadeDelayCounter > 0) {
                fadeDelayCounter--;
            } else {
                fadeAlpha -= 0.09;
                if (fadeAlpha <= 0.0) {
                    fadeAlpha = 0.0;
                    fading = false;

                }
            }
        }
    }


    /**
     * Draw game objects
     * @param g Graphics object
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (player != null){
            tileMap.drawMap(g2);
            checkpointManager.draw(g2);
            player.draw(g2);
        }

        EnemySpawnHandler.drawAll(g2);

        for (int i = 0; i < effects.size(); i++) {
            effects.get(i).draw(g2);
        }

        // Draw player hit box and colidable tiles
//        CollisionHandler.draw(g2, player);
//        CollisionHandler.draw(g2, enemies.getFirst());
//
//        for (int i = 0; i < playerAttacks.size(); i++) {
//            playerAttacks.get(i).draw(g2);
//        }
//
        for (int i = 0; i < enemyAttacks.size(); i++) {
            enemyAttacks.get(i).draw(g2);
        }

        if (player != null) {
            tileMap.coverScreen(g2);
            ui.draw(g2);
        }

        if (fading) {
            g2.setColor(new Color(0, 0, 0, (float) fadeAlpha));
            g2.fillRect(0, 0, (int) screenWidth, (int) screenHeight);
        }

        g2.dispose();
    }
}
