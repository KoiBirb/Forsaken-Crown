/*
 * GamePanel.java
 * Leo Bogaert
 * May 7, 2025,
 * Main game loop
 */

package Main.Panels;

import Main.KeyInput;
import Main.UI.UIManager;

import javax.swing.*;
import java.awt.*;

import static Main.Main.keyI;

public class MenuPanel extends JPanel implements Runnable{

    // Screen settings
    public final static double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    public final static double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();

    public static UIManager ui = new UIManager();

    public static Thread menuThread;

    /**
     * Constructor for the GamePanel class.
     */
    public MenuPanel(){
        this.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.setBackground(Color.BLACK);
        this.addKeyListener(keyI);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
    }

    /**
     * Set up the game.
     */
    public void setupGame() {
        this.requestFocusInWindow();
        startThread();
    }



    /**
     * Starts game thread
     */
    public void startThread() {
        menuThread = new Thread(this);
        menuThread.start();
    }

    /**
     * Delta FPS clock to call update and repaint
     */
    @Override
    public void run() {
        double drawInterval = 1000000000.0/60; // 60 FPS
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while (menuThread != null) {

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if(delta >= 1) {
                update();
                repaint();
                delta--;
                drawCount++;
            }

            if(timer>= 1000000000) {
                // FPS counter
//                System.out.println("FPS:" + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }

    /**
     * Update game objects
     */
    public void update() {
        ui.update();
    }


    /**
     * Draw game objects
     * @param g Graphics object
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        ui.draw(g2);

        g2.dispose();
    }
}
