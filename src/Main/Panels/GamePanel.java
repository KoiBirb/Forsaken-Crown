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

    private TiledMap tileMap;
    private Player player;

    private final int FPS = 60;

    public KeyInput keyI = new KeyInput(this);

    Thread gameThread;

    public GamePanel(){
        this.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.setBackground(Color.WHITE);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyI);
        this.setFocusable(true);

        // initialize classes

        tileMap = new TiledMap();
        player = new Player(new Vector2(100,100), 32,16, keyI);
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
        player.update();
    }

    public void paintComponent(Graphics g){

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D)g;

            tileMap.drawMap(g2, player);
            player.draw(g2);

            g2.dispose();
        }
}
