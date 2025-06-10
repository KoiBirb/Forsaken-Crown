/*
 * GamePanel.java
 * Leo Bogaert, Benjamin Weir
 * May 7, 2025,
 * Main game loop
 */

package Main.Panels;

import Attacks.MeleeAttack;
import Entitys.Enemies.Enemy;
import Entitys.Player;
import Handlers.CollisionHandler;
import Handlers.ImageHandler;
import Handlers.Sound.SoundHandlers.BackgroundMusicHandler;
import Handlers.Sound.SoundHandlers.EnemySoundHandler;
import Handlers.Sound.SoundHandlers.PlayerSoundHandler;
import Handlers.Vector2;
import Handlers.EnemySpawnHandler;
import Main.UI.UIManager;
import Main.UI.VFX.Effect;
import Map.Checkpoints.CheckpointManager;
import Map.TiledMap;

import javax.swing.*;
import java.awt.*;
import java.awt.image.VolatileImage;
import java.util.ArrayList;

import static Main.Main.keyI;

public class GamePanel extends JPanel implements Runnable{

    public final static double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    public final static double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    public final static double scale = 2;

    public static TiledMap tileMap;
    public static Player player;
    public static UIManager ui;
    public static CheckpointManager checkpointManager;
    public static BackgroundMusicHandler backgroundMusic = new BackgroundMusicHandler();

    private float helpAlpha = 0f, titleAlpha = 1f;
    private static final float movementFactor = 0.15f;
    public static boolean help = false;

    public static ArrayList<MeleeAttack> playerAttacks = new ArrayList<>();
    public static ArrayList<MeleeAttack> enemyAttacks = new ArrayList<>();
    public static ArrayList<Enemy> enemies = new ArrayList<>();
    public static ArrayList<Effect> effects = new ArrayList<>();
    public static ArrayList<Enemy> activeEnemies = new ArrayList<>();

    private VolatileImage title = ImageHandler.loadImage("Assets/Images/UI/Words/paused.png");
    private VolatileImage Help = ImageHandler.loadImage("Assets/Images/UI/Words/help.png");
    public static final Color backgroundColor = new Color(11, 11, 11);

    public static long initialTime, pauseTime;
    public static int points;

    public static boolean fading = false, fadeIn = true;
    private static double fadeAlpha = 0.0;
    private int fadeDelay = 10, fadeDelayCounter = 0;
    public static boolean isPaused = false;

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

        tileMap = new TiledMap();
    }

    /**
     * Set up the game.
     */
    public void setupGame() {
        this.requestFocusInWindow();
        player = new Player(new Vector2(100,400));
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
        final double drawInterval = 1_000_000_000.0 / 60.0;
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
        ui.update();

        if(keyI.lPressed){

            if(isPaused){
                pauseTime = System.currentTimeMillis();
                backgroundMusic.unmuteCurrent();
                EnemySoundHandler.unmuteAll();
                PlayerSoundHandler.unmuteAll();
            } else {
                initialTime -= (System.currentTimeMillis() - pauseTime);
                backgroundMusic.muteCurrent();
                EnemySoundHandler.muteAll();
                PlayerSoundHandler.muteAll();
            }

            isPaused = !isPaused;
            keyI.lPressed = false;

        }

        if (!isPaused) {
            if (player != null) {
                player.update();
                tileMap.update();
            }

            EnemySpawnHandler.updateAll();
            checkpointManager.update();

            for (int i = 0; i < playerAttacks.size(); i++) {
                playerAttacks.get(i).update();
            }

            for (int i = 0; i < enemyAttacks.size(); i++) {
                enemyAttacks.get(i).update();
            }

            for (int i = 0; i < effects.size(); i++) {
                effects.get(i).update();
            }

            for (int i = 0; i < enemies.size(); i++) {
                for (int j = 0; j < playerAttacks.size(); j++) {
                    if (CollisionHandler.attackCollision(playerAttacks.get(j), enemies.get(i))) {
                        enemies.get(i).hit(playerAttacks.get(j).getDamage(), 3, 3);
                    }
                }
            }

            backgroundMusic.update();

            for (int j = 0; j < enemyAttacks.size(); j++) {
                if (CollisionHandler.attackCollision(enemyAttacks.get(j), player)) {
                    player.hit(enemyAttacks.get(j).getDamage(), 0, 0);
                }
            }

            if (fading) {
                if (fadeIn) {
                    fadeAlpha += 0.09;
                    if (fadeAlpha >= 1.0) {
                        fadeAlpha = 1.0;
                        fadeIn = false;
                        fadeDelayCounter = fadeDelay;
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
        } else {

            float targetHelpAlpha = help ? 1f : 0f;
            helpAlpha += (targetHelpAlpha - helpAlpha) * movementFactor;

            float targetTitleAlpha = 1f - helpAlpha;
            titleAlpha += (targetTitleAlpha - titleAlpha) * movementFactor;
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

        // Hitboxes for debugging

//        CollisionHandler.draw(g2, player);
//        CollisionHandler.draw(g2, enemies.getFirst());
//
//        for (int i = 0; i < playerAttacks.size(); i++) {
//            playerAttacks.get(i).draw(g2);
//        }
//
//        for (int i = 0; i < enemyAttacks.size(); i++) {
//            enemyAttacks.get(i).draw(g2);
//        }

        if (player != null) {
            tileMap.coverScreen(g2);
            ui.draw(g2);
        }

        if (fading) {
            g2.setColor(new Color(0, 0, 0, (float) fadeAlpha));
            g2.fillRect(0, 0, (int) screenWidth, (int) screenHeight);
        }
        if(isPaused) {
            ui.draw(g2);
            if (helpAlpha > 0.01f) {
                Composite old = g2.getComposite();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, helpAlpha));
                g2.drawImage(Help, (int) (screenWidth / 2 - Help.getWidth() * 1.5),
                        150, (int) (screenWidth / 2 + Help.getWidth() * 1.5),
                        150 + (Help.getHeight() * 3), 0, 0,
                        Help.getWidth(), Help.getHeight(), null);
                g2.setComposite(old);
            }
            if (titleAlpha > 0.01f) {
                Composite old = g2.getComposite();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, titleAlpha));
                g2.drawImage(title, (int) (screenWidth / 2 - title.getWidth() * 2.5),
                        150, (int) (screenWidth / 2 + title.getWidth() * 2.5),
                        150 + (int) (title.getHeight() * 5), 0, 0,
                        title.getWidth(), title.getHeight(), null);
                g2.setComposite(old);
            }
        }
            g2.dispose();
    }
}
