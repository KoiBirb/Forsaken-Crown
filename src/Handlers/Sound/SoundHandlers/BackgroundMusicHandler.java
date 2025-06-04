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



    public BackgroundMusicHandler() {

       // Preload all music files once
       musicDarkMain.setFile("/Audio/Background/Dark/DarkMain.wav");
       musicDarkAction.setFile("/Audio/Background/Dark/DarkAction.wav");
       musicCastleMain.setFile("/Audio/Background/Castle/CastleMain.wav");
       musicCastleAction.setFile("/Audio/Background/Castle/CastleAction.wav");
       musicBloodMain.setFile("/Audio/Background/Blood/BloodMain.wav");
       musicBloodAction.setFile("/Audio/Background/Blood/BloodAction.wav");
       musicBossMain.setFile("/Audio/Background/Boss/BossMain.wav");

       // Start all tracks, but only set volume to 1 for the initial one
       musicDarkMain.play();
       musicDarkMain.loop();
       musicDarkMain.setVolume(1.0f);

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

    public void update() {
        int room = TiledMap.getPlayerRoomId();
        long now = System.currentTimeMillis();

        // Determine the desired music type and state
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

        // Handle type change immediately
        if (desiredType != currentMusicType) {
            transitionToMusic(getMusic(desiredType, MusicState.MAIN), 2000);
            currentMusicType = desiredType;
            currentMusicState = MusicState.MAIN;
            actionSwitchPending = false;
            return;
        }


        if (desiredState != currentMusicState) {
            if (!actionSwitchPending || requestedMusicState != desiredState) {
                actionSwitchPending = true;
                actionSwitchRequestTime = now;
                requestedMusicState = desiredState;
            } else if (now - actionSwitchRequestTime >= ACTION_SWITCH_GRACE_MS) {
                transitionToMusic(getMusic(currentMusicType, desiredState), 1000);
                currentMusicState = desiredState;
                actionSwitchPending = false;
            }
        } else {
            actionSwitchPending = false;
        }
    }

   public void playBossMain(){
       musicBossMain.play();
   }

    public void transitionToMusic(Sound to, int durationMs) {
        Sound from = getMusic(currentMusicType, currentMusicState);

        if (isTransitioning.get()) {
            return; // Prevent overlapping transitions
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

    public void fadeOut(int durationMs) {
        Sound from = getMusic(currentMusicType, currentMusicState);

        if (isTransitioning.get()) {
            return; // Prevent overlapping transitions
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
}