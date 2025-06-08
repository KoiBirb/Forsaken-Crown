/*
 * BackgroundMusicHandler.java
 * Leo Bogaert
 * Jun 7, 2025,
 * Handles background music
 */

package Handlers.Sound.SoundHandlers;

import Handlers.Sound.Sound;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BackgroundMusicHandler {

   private final Sound musicDarkMain = new Sound();
   private final Sound musicDarkAction = new Sound();
   private final Sound musicCastleMain = new Sound();
   private final Sound musicCastleAction = new Sound();
   private final Sound musicBloodMain = new Sound();
   private final Sound musicBloodAction = new Sound();
   private final Sound musicBossMain = new Sound();

   private final java.util.concurrent.atomic.AtomicBoolean isTransitioning = new java.util.concurrent.atomic.AtomicBoolean(false);

   private enum MusicType {DARK, CASTLE, BLOOD, BOSS}
   private enum MusicState {MAIN, ACTION}

   private MusicType currentMusicType = MusicType.DARK;
   private MusicState currentMusicState = MusicState.MAIN;

   private long actionSwitchRequestTime = 0;
   private boolean actionSwitchPending = false;
   private MusicState requestedMusicState = MusicState.MAIN;
   private static final long ACTION_SWITCH_GRACE_MS = 800;

    /**
     * Constructor for BackgroundMusicHandler
     * Loads all music and starts playing them
     */
    public BackgroundMusicHandler() {

       musicDarkMain.setFile("/Audio/Background/Dark/DarkMain.wav");
       musicDarkAction.setFile("/Audio/Background/Dark/DarkAction.wav");
       musicCastleMain.setFile("/Audio/Background/Castle/CastleMain.wav");
       musicCastleAction.setFile("/Audio/Background/Castle/CastleAction.wav");
       musicBloodMain.setFile("/Audio/Background/Blood/BloodMain.wav");
       musicBloodAction.setFile("/Audio/Background/Blood/BloodAction.wav");
       musicBossMain.setFile("/Audio/Background/Boss/BossMain.wav");

       musicDarkMain.play();
       musicDarkMain.loop();
       musicDarkMain.setVolume(0.0f);

       musicDarkAction.play();
       musicDarkAction.loop();
       musicDarkAction.setVolume(0.0f);

       musicCastleMain.play();
       musicCastleMain.loop();
       musicCastleMain.setVolume(0.0f);

       musicCastleAction.play();
       musicCastleAction.loop();
       musicCastleAction.setVolume(0.0f);

       musicBloodMain.play();
       musicBloodMain.loop();
       musicBloodMain.setVolume(0.0f);

       musicBloodAction.play();
       musicBloodAction.loop();
       musicBloodAction.setVolume(0.0f);
   }

    /**
     * Updates the background music based on the current room and enemy state
     * Handles transitions between different music types and states
     */
    public void update() {
        int room = TiledMap.getPlayerRoomId();
        long now = System.currentTimeMillis();

        MusicType desiredType;
        MusicState desiredState;

        if (room <= 5) {
            desiredType = MusicType.DARK;
            desiredState = GamePanel.activeEnemies.isEmpty() ? MusicState.MAIN : MusicState.ACTION;
        } else if (room != 8 && room != 17 && room != 19) {
            desiredType = MusicType.CASTLE;
            desiredState = GamePanel.activeEnemies.isEmpty() ? MusicState.MAIN : MusicState.ACTION;
        } else if (room == 19) {
            desiredType = MusicType.BOSS;
            desiredState = MusicState.MAIN;
        } else {
            desiredType = MusicType.BLOOD;
            desiredState = GamePanel.activeEnemies.isEmpty() ? MusicState.MAIN : MusicState.ACTION;
        }

        if (desiredType != currentMusicType) {
            if (!isTransitioning.get()) {
                transitionToMusic(getMusic(desiredType, MusicState.MAIN), 2000);
                currentMusicType = desiredType;
                currentMusicState = MusicState.MAIN;
                actionSwitchPending = false;
            }
            return;
        }


        if (desiredState != currentMusicState) {
            if (!actionSwitchPending || requestedMusicState != desiredState) {
                actionSwitchPending = true;
                actionSwitchRequestTime = now;
                requestedMusicState = desiredState;
            } else if (now - actionSwitchRequestTime >= ACTION_SWITCH_GRACE_MS) {
                if (!isTransitioning.get()) {
                    transitionToMusic(getMusic(currentMusicType, desiredState), 1000);
                    currentMusicState = desiredState;
                    actionSwitchPending = false;
                }
            }
        } else {
            actionSwitchPending = false;
        }
    }

    /**
     * Plays the main music for the boss fight
     */
   public void playBossMain(){
       musicBossMain.play();
       musicBloodAction.loop();
   }

   /**
    * Transitions to a new music track smoothly over a specified duration
    * @param to The new Sound object to transition to
    * @param durationMs Duration of the transition in milliseconds
    */
    public void transitionToMusic(Sound to, int durationMs) {
        Sound from = getMusic(currentMusicType, currentMusicState);

        if (isTransitioning.get()) {
            return;
        }

        isTransitioning.set(true);
        int steps = 500;
        float fromVolume = from.getVolume();
        float toVolume = 1.0f;
        to.setVolume(0f);

        if (!to.isPlaying()) {
            to.play();
            to.loop();
        }

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        final int[] currentStep = {0};

        Runnable transitionTask = () -> {
            if (currentStep[0] > steps || Thread.currentThread().isInterrupted()) {
                from.setVolume(0f);
                to.setVolume(toVolume);
                isTransitioning.set(false);
                scheduler.shutdown();
                return;
            }

            float t = currentStep[0] / (float) steps;
            float smooth = (1 - (float) Math.cos(Math.PI * t)) / 2f;
            from.setVolume(fromVolume * (1 - smooth));
            to.setVolume(toVolume * smooth);
            currentStep[0]++;
        };

        long stepDelay = durationMs / steps;
        scheduler.scheduleAtFixedRate(transitionTask, 0, stepDelay, TimeUnit.MILLISECONDS);
    }

    /**
     * Fades out the current music smoothly over a specified duration
     * @param durationMs Duration of the fade-out in milliseconds
     */
    public void fadeOut(int durationMs) {
        Sound from = getMusic(currentMusicType, currentMusicState);

        if (isTransitioning.get()) {
            return;
        }

        isTransitioning.set(true);
        int steps = 500;
        float fromVolume = from.getVolume();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        final int[] currentStep = {0};

        Runnable fadeTask = () -> {
            if (currentStep[0] > steps || Thread.currentThread().isInterrupted()) {
                from.setVolume(0f);
                if (from.isPlaying()) from.stop();
                isTransitioning.set(false);
                scheduler.shutdown();
                return;
            }

            float t = currentStep[0] / (float) steps;
            float smooth = (1 - (float) Math.cos(Math.PI * t)) / 2f;
            from.setVolume(fromVolume * (1 - smooth));
            currentStep[0]++;
        };

        long stepDelay = durationMs / steps;
        scheduler.scheduleAtFixedRate(fadeTask, 0, stepDelay, TimeUnit.MILLISECONDS);
    }

    /**
     * Fades in a music track smoothly over a specified duration
     * @param durationMs Duration of the fade-out in milliseconds
     */
    public void fadeIn(Sound to, int durationMs) {
        if (isTransitioning.get()) {
            return;
        }

        isTransitioning.set(true);
        int steps = 500;
        to.setVolume(0f);

        if (!to.isPlaying()) {
            to.play();
            to.loop();
        }

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        final int[] currentStep = {0};

        Runnable fadeTask = () -> {
            if (currentStep[0] > steps || Thread.currentThread().isInterrupted()) {
                to.setVolume(1f);
                isTransitioning.set(false);
                scheduler.shutdown();
                return;
            }

            float t = currentStep[0] / (float) steps;
            float smooth = (1 - (float) Math.cos(Math.PI * t)) / 2f;
            to.setVolume(smooth);
            currentStep[0]++;
        };

        long stepDelay = durationMs / steps;
        scheduler.scheduleAtFixedRate(fadeTask, 0, stepDelay, TimeUnit.MILLISECONDS);
    }

    /**
     * Gets the current music based on type and state
     * @param type current MusicType
     * @param state current MusicState
     * @return Sound object for the specified type and state
     */
    public Sound getMusic(MusicType type, MusicState state) {
       return switch (type) {
           case DARK -> switch (state) {
               case MAIN -> musicDarkMain;
               case ACTION -> musicDarkAction;
           };
           case CASTLE -> switch (state) {
               case MAIN -> musicCastleMain;
               case ACTION -> musicCastleAction;
           };
           case BLOOD -> switch (state) {
               case MAIN -> musicBloodMain;
               case ACTION -> musicBloodAction;
           };
           case BOSS -> musicBossMain;
       };
    }

    /**
     * gets Dark main music
     * @return Sound object for Dark main music
     */
    public Sound getMusicMusicDarkMain() {
        return musicDarkMain;
    }

    /**
     * Mutes the current music
     */
    public void muteCurrent() {
        Sound current = getMusic(currentMusicType, currentMusicState);
        current.setVolume(0f);
    }

    /**
     * Unmutes the current music
     */
    public void unmuteCurrent() {
        Sound current = getMusic(currentMusicType, currentMusicState);
        current.setVolume(1f);
    }
}