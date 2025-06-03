
package Handlers.Sound;

import javax.sound.sampled.*;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Sound {

    private static final Map<String, Clip> clipCache = new ConcurrentHashMap<>();
    private Clip clip;
    private float volume = 1.0f;

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
                    System.out.println("Error loading sound file: " + p);
                    e.printStackTrace();
                    return null;
                }
            });
            return clip != null;
        } catch (Exception e) {
            System.out.println("Error with sound file. " + path);
            e.printStackTrace();
            return false;
        }
    }

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

    public float getVolume() {
        return volume;
    }

    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }

    public void play() {
        if (clip != null) {
            if (clip.isRunning()) clip.stop();
            clip.setFramePosition(0);
            clip.start();
        }
    }

    public void loop() {
        if (clip != null) {
            if (clip.isRunning()) clip.stop();
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
        }
    }

    public Clip getClip() {
        return clip;
    }
}