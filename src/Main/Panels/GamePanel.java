package Main.Panels;

import Entitys.Player;
import Handlers.Vector2;
import Main.KeyInput;
import Map.TiledMap;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable{

    public final static double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    public final static double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    public final static double scale = 2;

    public static TiledMap tileMap;
    public static KeyInput keyI = new KeyInput();
    public static final Player player = new Player(new Vector2(200,150), 32,16);

    private final int FPS = 60;

    private static boolean fading = false;
    private static double fadeAlpha = 0.0;
    private static boolean fadeIn = true;
    private int fadeDelay = 20;
    private int fadeDelayCounter = 0;


    Thread gameThread;

    public GamePanel(){
        this.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.setBackground(Color.WHITE);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyI);
        this.setFocusable(true);

        // initialize classes

        tileMap = new TiledMap();
    }

    public static void roomTransition() {
        fading = true;
        fadeAlpha = 0.0;
        fadeIn = true;
    }

    public void setupGame() {
    }

    public void startThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        // Delta method FPS clock
        double drawInterval = 1000000000.0/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;

        while (gameThread != null) {

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if(delta >= 1) {
                update();
                repaint();
                delta--;
            }
            if(timer>= 1000000000) {
                timer = 0;
            }
        }
    }

    public void update() {
        // Update player and tile map regardless of fading
        player.update();
        tileMap.update();

        // Handle fade logic
        if (fading) {
            player.setCanMove(false);
            if (fadeIn) {
                fadeAlpha += 0.07;
                if (fadeAlpha >= 1.0) {
                    fadeAlpha = 1.0;
                    fadeIn = false;
                    fadeDelayCounter = fadeDelay;
                }
            } else if (fadeDelayCounter > 0) {
                fadeDelayCounter--; // Stay on pure black
            } else {
                fadeAlpha -= 0.07;
                if (fadeAlpha <= 0.0) {
                    fadeAlpha = 0.0;
                    fading = false;
                    player.setCanMove(true);

                }
            }
        }
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        tileMap.drawMap(g2);
        player.draw(g2);

        tileMap.coverScreen(g2);

        // Draw fade effect
        if (fading) {
            g2.setColor(new Color(0, 0, 0, (float) fadeAlpha));
            g2.fillRect(0, 0, (int) screenWidth, (int) screenHeight);
        }

        g2.dispose();
    }
}
