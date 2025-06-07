/*
 * ImageHandler.java
 * Leo Bogaert
 * May 2, 2025,
 * Loads images
 */
package Handlers;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.IOException;
import java.util.Objects;

public class ImageHandler {

    /**
     * Loads an image from the given path
     * @param path Path to the image
     * @return VolatileImage of the loaded image
     */
    public static VolatileImage loadImage(String path) {
        try {
            return createVolatileImage(toCompatibleImage(ImageIO.read(Objects.requireNonNull(ImageHandler.class.getClassLoader().getResourceAsStream(path)))));
        } catch (IOException | NullPointerException e) {
            System.out.println("Error loading image: " + path + e.getMessage());
            return null;
        }
    }

    /**
     * Converts a BufferedImage to a compatible image type for rendering
     * @param image BufferedImage to convert
     * @return BufferedImage that is compatible for rendering
     */
    private static BufferedImage toCompatibleImage(BufferedImage image) {
        if (image.getType() == BufferedImage.TYPE_INT_ARGB || image.getType() == BufferedImage.TYPE_INT_RGB) {
            return image;
        }
        BufferedImage compatible = new BufferedImage(
                image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g2d = compatible.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return compatible;
    }

    /**
     * Creates a VolatileImage from a BufferedImage
     * @param src BufferedImage to convert
     * @return VolatileImage created from the BufferedImage
     */
    public static VolatileImage createVolatileImage(BufferedImage src) {
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getDefaultScreenDevice().getDefaultConfiguration();
        VolatileImage vImg = gc.createCompatibleVolatileImage(
            src.getWidth(), src.getHeight(), Transparency.TRANSLUCENT
        );
        Graphics2D g = vImg.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return vImg;
    }
}
