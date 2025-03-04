package Main.Panels;

import Main.KeyInput;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable{

    public double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    public double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();

    private final int FPS = 60;

    KeyInput keyI = new KeyInput(this);

    Thread gameThread;

    public GamePanel(){
        this.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyI);
        this.setFocusable(true);
    }



    public void setupGame() {
        loadMaps();
    }

    public void loadMaps(){

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

    }

    public void paintComponent(Graphics g){

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D)g;

            g2.dispose();
        }
}
