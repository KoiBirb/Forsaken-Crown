/*
 * ImageHandler.java
 * Leo Bogaert
 * May 2, 2025,
 * Loads images
 */
package Handlers;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class ImageHandler {

    /**
     * Loads an image from the given path
     * @param path Path to the image
     * @return BufferedImage of the loaded image
     */
    public static BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(Objects.requireNonNull(ImageHandler.class.getClassLoader().getResourceAsStream(path)));
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }
}
