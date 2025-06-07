/*
 * Button.java
 * Leo Bogaert
 * May 20, 2025,
 * Creates a menu button
 */

package Main.UI.Buttons;

import Handlers.ImageHandler;
import Main.UI.UIElement;

import java.awt.*;
import java.awt.image.VolatileImage;

public class Button extends UIElement {

    private final VolatileImage imageButton;

    /**
     * Constructor for Button
     * @param x int x position of the button
     * @param y int y position of the button
     * @param height int height of the button
     * @param imagePath String path to the image
     */
    public Button(int x, int y, int height, String imagePath) {
        super(x, y, (int) (height * 2.818181), height);

        this.x = x;
        this.y = y;
        this.height = height;
        this.width = (int) (height * 2.818181);

        imageButton = ImageHandler.loadImage(imagePath);
    }


    /**
     * Basic draw method
     * @param g2 Graphics2D object to draw on
     */
    public void draw(Graphics2D g2) {
        g2.drawImage(imageButton, x, y, width, height, null);
    }

    /**
     * Returns the image of the button
     * @return BufferedImage of the button
     */
    public VolatileImage getImage() {
        return imageButton;
    }

    /**
     * Returns x position
     * @return int x position
     */
    public int getX() {
        return x;
    }

    /**
     * Returns y position
     * @return int y position
     */
    public int getY() {
        return y;
    }
}
