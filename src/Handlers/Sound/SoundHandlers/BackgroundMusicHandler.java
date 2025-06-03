package Handlers.Sound.SoundHandlers;


import Handlers.Sound.Sound;
import Main.Panels.GamePanel;
import Map.TiledMap;

public class BackgroundMusicHandler {

    private final Sound musicDarkMain = new Sound();
    private final Sound musicDarkAction = new Sound();

    private final Sound musicCastleMain = new Sound();
    private final Sound musicCastleAction = new Sound();

    private final Sound musicBloodMain = new Sound();
    private final Sound musicBloodAction = new Sound();

    private final Sound musicBossMain = new Sound();

    private final java.util.concurrent.atomic.AtomicBoolean isTransitioning = new java.util.concurrent.atomic.AtomicBoolean(false);
    private volatile Thread transitionThread = null;

    private enum MusicType {DARK, CASTLE, BLOOD, BOSS}

    private enum MusicState {MAIN, ACTION}

    private MusicType currentMusicType = MusicType.DARK;
    private MusicState currentMusicState = MusicState.MAIN;


    public BackgroundMusicHandler() {
        playDarkMain();
        musicDarkMain.setVolume(1.0f);

        playDarkAction();
        musicDarkAction.setVolume(0.0f);

        playCastleMain();
        musicCastleMain.setVolume(0.0f);

        playCastleAction();
        musicCastleAction.setVolume(0.0f);

        playBloodMain();
        musicBloodMain.setVolume(0.0f);

        playBloodAction();
        musicBloodAction.setVolume(0.0f);
    }

    public void update(){
        int room = TiledMap.getPlayerRoomId();

        if (room <= 5) {
            if (currentMusicType != MusicType.DARK) {
                transitionToMusic(musicDarkMain, 2000);
                currentMusicType = MusicType.DARK;
                currentMusicState = MusicState.MAIN;
            }

            if (GamePanel.activeEnemies.isEmpty() && currentMusicState == MusicState.ACTION) {
                transitionToMusic(musicDarkMain, 500);
                currentMusicType = MusicType.DARK;
                currentMusicState = MusicState.MAIN;
            } else if (!GamePanel.activeEnemies.isEmpty() && currentMusicState != MusicState.ACTION){
                transitionToMusic(musicDarkAction, 500);
                currentMusicType = MusicType.DARK;
                currentMusicState = MusicState.ACTION;
            }
        } else if (room != 9 && room != 17 && room != 19) {
            if (currentMusicType != MusicType.CASTLE) {
                transitionToMusic(musicCastleMain, 2000);
                currentMusicType = MusicType.CASTLE;
                currentMusicState = MusicState.MAIN;
            }

            if (GamePanel.activeEnemies.isEmpty() && currentMusicState == MusicState.ACTION) {
                transitionToMusic(musicCastleMain, 500);
                currentMusicType = MusicType.CASTLE;
                currentMusicState = MusicState.MAIN;
            } else if (!GamePanel.activeEnemies.isEmpty() && currentMusicState != MusicState.ACTION){
                transitionToMusic(musicCastleAction, 500);
                currentMusicType = MusicType.CASTLE;
                currentMusicState = MusicState.ACTION;
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

            if (GamePanel.activeEnemies.isEmpty() && currentMusicState == MusicState.ACTION) {
                transitionToMusic(musicBloodMain, 500);
                currentMusicType = MusicType.BLOOD;
                currentMusicState = MusicState.MAIN;
            } else if (!GamePanel.activeEnemies.isEmpty() && currentMusicState != MusicState.ACTION){
                transitionToMusic(musicBloodAction, 500);
                currentMusicType = MusicType.BLOOD;
                currentMusicState = MusicState.ACTION;
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
            int steps = 80;
            float fromVolume = from.getVolume();
            float toVolume = 1.0f;
            to.setVolume(0f);

            if (to.getClip() != null && !to.isPlaying()) {
                to.play();
                to.loop();
            }

            for (int i = 0; i <= steps; i++) {
                if (Thread.currentThread().isInterrupted()) break;
                float t = i / (float) steps;
                float smooth = t;
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
            int steps = 80;
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

    public void playDarkAction() {
        if (musicDarkAction.setFile("/Audio/Background/Dark/DarkAction.wav")) {
            musicDarkAction.play();
            musicDarkAction.loop();
        }
    }

    public void playCastleMain() {
        if (musicCastleMain.setFile("/Audio/Background/Castle/CastleMain.wav")) {
            musicCastleMain.play();
            musicCastleMain.loop();
        }
    }

    public void playCastleAction() {
        if (musicCastleAction.setFile("/Audio/Background/Castle/CastleAction.wav")) {
            musicCastleAction.play();
            musicCastleAction.loop();
        }
    }

    public void playBloodMain() {
        if (musicBloodMain.setFile("/Audio/Background/Blood/BloodMain.wav")) {
            musicBloodMain.play();
            musicBloodMain.loop();
        }
    }

    public void playBloodAction() {
        if (musicBloodAction.setFile("/Audio/Background/Blood/BloodAction.wav")) {
            musicBloodAction.play();
            musicBloodAction.loop();
        }
    }

    public void playBossMain() {
        if (musicBossMain.setFile("/Audio/Background/Boss/BossMain.wav")) {
            musicBloodMain.setVolume(0.8f);
            musicBossMain.play();
            musicBossMain.loop();
        }
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
