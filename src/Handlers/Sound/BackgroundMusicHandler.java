package Handlers.Sound;


import Map.TiledMap;

public class BackgroundMusicHandler {

    public final Sound musicDarkMain = new Sound();
    public final Sound musicCastleMain = new Sound();
    public final Sound musicBloodMain = new Sound();
    public final Sound musicBossMain = new Sound();

    private final java.util.concurrent.atomic.AtomicBoolean isTransitioning = new java.util.concurrent.atomic.AtomicBoolean(false);
    private volatile Thread transitionThread = null;

    private enum MusicType {
        DARK, CASTLE, BLOOD, BOSS
    }

    private enum MusicState {
        MAIN, ACTION
    }

    private MusicType currentMusicType = MusicType.DARK;
    private MusicState currentMusicState = MusicState.MAIN;


    public BackgroundMusicHandler() {
        playDarkMain();
        musicDarkMain.setVolume(1.0f);

        playCastleMain();
        musicCastleMain.setVolume(0.0f);

        playBloodMain();
        musicBloodMain.setVolume(0.0f);
    }

    public void update(){
        int room = TiledMap.getPlayerRoomId();

        if (room <= 5) {
            if (currentMusicType != MusicType.DARK) {
                transitionToMusic(musicDarkMain, 2000);
                currentMusicType = MusicType.DARK;
                currentMusicState = MusicState.MAIN;
            }
        } else if (room != 9 && room != 17 && room != 19) {
            if (currentMusicType != MusicType.CASTLE) {
                transitionToMusic(musicCastleMain, 2000);
                currentMusicType = MusicType.CASTLE;
                currentMusicState = MusicState.MAIN;
            }
        } else if (room == 19){
            if (currentMusicType != MusicType.BOSS) {
                fadeOut(2000);
                currentMusicType = MusicType.BOSS;
                currentMusicState = MusicState.MAIN;
            }
       } else {
            if (currentMusicType != MusicType.BLOOD) {
                transitionToMusic(musicBloodMain, 2000);
                currentMusicType = MusicType.BLOOD;
                currentMusicState = MusicState.MAIN;
            }
       }
    }

    public void transitionToMusic(Sound to, int durationMs) {

        Sound from = getMusic(currentMusicType, currentMusicState);

        // Cancel any ongoing transition
        if (transitionThread != null && transitionThread.isAlive()) {
            transitionThread.interrupt();
            try { transitionThread.join(); } catch (InterruptedException ignored) {}
        }
        transitionThread = new Thread(() -> {
            int steps = 100;
            float fromVolume = from.getVolume();
            float toVolume = 0.8f;
            to.setVolume(0f);

            if (to.getClip() != null && !to.isPlaying()) {
                to.play();
                to.loop();
            }

            for (int i = 0; i <= steps; i++) {
                if (Thread.currentThread().isInterrupted()) break;
                float t = i / (float) steps;
                float smooth = (1 - (float)Math.cos(Math.PI * t)) / 2f;
                from.setVolume(fromVolume * (1 - smooth));
                to.setVolume(toVolume * smooth);
                try {
                    Thread.sleep(durationMs / steps);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            if (from.isPlaying())
                from.stop();
            to.setVolume(toVolume);
            isTransitioning.set(false);
        });
        isTransitioning.set(true);
        transitionThread.start();
    }

    public void fadeOut(int durationMs) {

        Sound from = getMusic(currentMusicType, currentMusicState);
        // Cancel any ongoing transition
        if (transitionThread != null && transitionThread.isAlive()) {
            transitionThread.interrupt();
            try { transitionThread.join(); } catch (InterruptedException ignored) {}
        }
        transitionThread = new Thread(() -> {
            int steps = 100;
            float fromVolume = from.getVolume();

            for (int i = 0; i <= steps; i++) {
                if (Thread.currentThread().isInterrupted()) break;
                float t = i / (float) steps;
                float smooth = (1 - (float)Math.cos(Math.PI * t)) / 2f;
                from.setVolume(fromVolume * (1 - smooth));
                try {
                    Thread.sleep(durationMs / steps);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            if (from.isPlaying())
                from.stop();
            isTransitioning.set(false);
        });
        isTransitioning.set(true);
        transitionThread.start();
    }

    public void playDarkMain() {
        if (musicDarkMain.setFile("/Audio/Background/Dark/DarkMain.wav")) {
            musicDarkMain.play();
            musicDarkMain.loop();
        }
    }

    public void playCastleMain() {
        if (musicCastleMain.setFile("/Audio/Background/Castle/CastleMain.wav")) {
            musicCastleMain.play();
            musicCastleMain.loop();
        }
    }

    public void playBossMain() {
        if (musicBossMain.setFile("/Audio/Background/Boss/BossMain.wav")) {
            musicBloodMain.setVolume(0.8f);
            musicBossMain.play();
            musicBossMain.loop();
        }
    }

    public void playBloodMain() {
        if (musicBloodMain.setFile("/Audio/Background/Blood/BloodMain.wav")) {
            musicBloodMain.play();
            musicBloodMain.loop();
        }
    }

    public Sound getMusic(MusicType type, MusicState state) {
        switch (type) {
            case DARK:
                switch (state) {
                    case MAIN:
                        return musicDarkMain;
                    case ACTION:
                        // Return a different sound for action state if needed
                        return musicDarkMain; // Placeholder, replace with actual action music if available
                }
            case CASTLE:
                return musicCastleMain;
            case BLOOD:
                return musicBloodMain;
            case BOSS:
                return musicBossMain;
            default:
                return musicDarkMain;
        }
    }
}
