/*
 * Sound.java
 * Leo Bogaert
 * Jun 7, 2025,
 * Creates and manages sound clips
 */

package Handlers.Sound;

import javax.sound.sampled.*;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Sound {

    private static final Map<String, Clip> clipCache = new ConcurrentHashMap<>();
    private Clip clip;
    private float volume = 1.0f;

    /**
     * Sets the sound file to be played
     * @param path String path to the sound file
     * @return boolean indicating success or failure
     */
    public boolean setFile(String path) {
        try {
            clip = clipCache.computeIfAbsent(path, p -> {
                try {
                    AudioInputStream ais = AudioSystem.getAudioInputStream(
                        Objects.requireNonNull(getClass().getResource(p)));
                    Clip c = AudioSystem.getClip();
                    c.open(ais);
                    return c;
                } catch (Exception e) {
                    System.out.println("Error loading sound file: " + p + e.getMessage());
                    return null;
                }
            });
            return clip != null;
        } catch (Exception e) {
            System.out.println("Error with sound file. " + path + e.getMessage());
            return false;
        }
    }

    /**
     * Sets the sound file to be played from a Clip object
     * @param volume float volume level (0.0 to 1.0)
     */
    public void setVolume(float volume) {
        this.volume = volume;
        if (clip != null && clip.isOpen()) {
            try {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float min = gainControl.getMinimum();
                float max = gainControl.getMaximum();
                float gain = min + (max - min) * volume;
                gainControl.setValue(gain);
            } catch (Exception ignored) {}
        }
    }

    /**
     * Gets the current volume level
     * @return float current volume level (0.0 to 1.0)
     */
    public float getVolume() {
        return volume;
    }

    /**
     * Checks if the sound is currently playing
     * @return boolean true if playing, false otherwise
     */
    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }

    /**
     * Plays the sound clip.
     */
    public void play() {
        if (clip != null) {
            if (clip.isRunning()) clip.stop();
            clip.setFramePosition(0);
            clip.start();
        }
    }

    /**
     * Loops the sound clip continuously.
     */
    public void loop() {
        if (clip != null) {
            if (clip.isRunning()) clip.stop();
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    /**
     * Stops the sound clip.
     */
    public void stop() {
        if (clip != null) {
            clip.stop();
        }
    }
}