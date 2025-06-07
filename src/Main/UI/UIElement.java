/*
 * UIElement.java
 * Leo Bogaert
 * May 7, 2025,
 * Parent element class for all UI elements
 */

package Main.UI;

import Handlers.ImageHandler;

import java.awt.*;
import java.awt.image.VolatileImage;

public abstract class UIElement {

    protected int x, y, width, height;
    protected VolatileImage imageGlow, image;

    /**
     * Constructor for UIElement
     * @param x int x position of the UI element
     * @param y int y position of the UI element
     * @param width int width of the UI element
     * @param height int height of the UI element
     */
    public UIElement(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        image = ImageHandler.loadImage("Images/UI/Borders and HP/UI - 16x16 UI Tileset.png");
        imageGlow = ImageHandler.loadImage("Images/UI/Borders and HP/UI - 16x16 UI Tileset with glow.png");
    }

    /**
     * Draw the UI element
     * @param g2 Graphics2D object to draw on
     */
    public abstract void draw(Graphics2D g2);

    /**
     * Returns the width of an element
     * @return double the width of the element
     */
    public double getWidth(){
        return width;
    }

    /**
     * Returns the height of an element
     * @return double the height of the element
     */
    public double getHeight(){
        return height;
    }
}
