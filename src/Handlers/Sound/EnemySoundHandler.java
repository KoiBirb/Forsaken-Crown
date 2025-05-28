package Handlers.Sound;

     public class EnemySoundHandler {

         private static final Sound ghoulSteps = new Sound();
         private static final Sound ghoulEffect = new Sound();

         private static final Sound summonerSteps = new Sound();
         private static final Sound summonerEffect = new Sound();

         private static int walkingGhouls = 0, attackingGhouls = 0;
         private static int walkingSummoners = 0, attackingSummoners = 0;

         public static void summonerDeath() {
             playSoundEffect("/Audio/Enemy/Summoner/Death.wav", ghoulEffect);
         }

         public static synchronized void summonerAttack() {
             if (attackingSummoners == 0) {
                 summonerEffect.setFile("/Audio/Enemy/Summoner/Swing.wav");
                 summonerEffect.play();
             }
             attackingSummoners++;
         }

         public static synchronized void stopSummonerAttack() {
             if (attackingSummoners > 0) {
                 attackingSummoners--;
                 if (attackingSummoners == 0) {
                     summonerEffect.stop();
                 }
             }
         }

         public static void summonerHit() {
             playSoundEffect("/Audio/Enemy/Summoner/Hit.wav", summonerEffect);
         }

         public static void summonerSlam() {
             playSoundEffect("/Audio/Enemy/Summoner/HitGround.wav", summonerEffect);
         }

         public static void summonerSummon() {
             playSoundEffect("/Audio/Enemy/Summoner/Summon.wav", summonerEffect);
         }

         public static synchronized void summonerFootsteps() {
             if (walkingSummoners == 0) {
                 summonerSteps.setFile("/Audio/Enemy/Summoner/Footsteps.wav");
                 summonerSteps.play();
                 summonerSteps.loop();
             }
             walkingSummoners++;
         }

         public static synchronized void stopSummonerFootsteps() {
             if (walkingSummoners > 0) {
                 walkingSummoners--;
                 if (walkingSummoners == 0) {
                     summonerSteps.stop();
                 }
             }
         }

         public static void ghoulDeath() {
             playSoundEffect("/Audio/Enemy/Ghoul/Ghoul_Death.wav", ghoulEffect);
         }

         public static synchronized void ghoulAttack() {
             if (attackingGhouls == 0) {
                 ghoulEffect.setFile("/Audio/Enemy/Ghoul/Ghoul_Attack.wav");
                 ghoulEffect.play();
             }
             attackingGhouls++;
         }

         public static synchronized void stopGhoulAttack() {
             if (attackingGhouls > 0) {
                 attackingGhouls--;
                 if (attackingGhouls == 0) {
                     ghoulEffect.stop();
                 }
             }
         }

         public static synchronized void ghoulFootsteps() {
             if (walkingGhouls == 0) {
                 ghoulSteps.setFile("/Audio/Enemy/Ghoul/Ghoul_Footsteps.wav");
                 ghoulSteps.play();
                 ghoulSteps.loop();
             }
             walkingGhouls++;
         }

         public static synchronized void stopGhoulFootsteps() {
             if (walkingGhouls > 0) {
                 walkingGhouls--;
                 if (walkingGhouls == 0) {
                     ghoulSteps.stop();
                 }
             }
         }

         public static void ghoulHit() {
             playSoundEffect("/Audio/Enemy/Ghoul/Ghoul_Hit.wav", ghoulEffect);
         }

         /**
          * Plays a given sound effect on a sound object
          * @param path String file path from Assets
          * @param sound Sound object used to play effect
          */
         private static void playSoundEffect(String path, Sound sound) {
             if (sound.setFile(path)) {
                 sound.play();
             }
         }
     }