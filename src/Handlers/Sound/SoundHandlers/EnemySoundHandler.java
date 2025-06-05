
 package Handlers.Sound.SoundHandlers;

 import Handlers.Sound.Sound;

 public class EnemySoundHandler {

     private static final Sound ghoulSteps = new Sound();
     private static final Sound ghoulAttack = new Sound();
     private static final Sound ghoulDeath = new Sound();
     private static final Sound ghoulHit = new Sound();

     private static final Sound summonerSteps = new Sound();
     private static final Sound summonerAttack = new Sound();
     private static final Sound summonerDeath = new Sound();
     private static final Sound summonerHit = new Sound();
     private static final Sound summonerSlam = new Sound();
     private static final Sound summonerSummon = new Sound();

     private static final Sound skeletonSteps = new Sound();
     private static final Sound skeletonAttack = new Sound();
     private static final Sound skeletonDeath = new Sound();
     private static final Sound skeletonHit = new Sound();
     private static final Sound skeletonSpawnSound = new Sound();

     private static final Sound kingSteps = new Sound();
     private static final Sound kingHit = new Sound();
     private static final Sound kingDeath = new Sound();

     // King attack sounds
     private static final Sound kingDodgeDash = new Sound();
     private static final Sound kingDodgeSlash1 = new Sound();
     private static final Sound kingDodgeSlash2 = new Sound();
     private static final Sound kingDodgeStepBack = new Sound();
     private static final Sound kingFinisherCharge = new Sound();
     private static final Sound kingFinisherHitGround = new Sound();
     private static final Sound kingFinisherSwing = new Sound();
     private static final Sound kingHeartAppear = new Sound();
     private static final Sound kingHeartKingAppear = new Sound();
     private static final Sound kingHeartKingSink = new Sound();
     private static final Sound kingHeartSplash = new Sound();
     private static final Sound kingSlamHitGround = new Sound();
     private static final Sound kingSlamJump = new Sound();
     private static final Sound kingSlamSwing = new Sound();
     private static final Sound kingSlashSwing1 = new Sound();
     private static final Sound kingSlashSwing2 = new Sound();
     private static final Sound kingStabSlice = new Sound();
     private static final Sound kingStabStab = new Sound();
     private static final Sound kingStabWarn = new Sound();

     private static final Sound botSteps = new Sound();
     private static final Sound botStab = new Sound();
     private static final Sound botDeath = new Sound();

     private static int walkingGhouls = 0, attackingGhouls = 0;
     private static int walkingSummoners = 0, attackingSummoners = 0;
     private static int walkingSkeletons = 0, attackingSkeletons = 0;
     private static int walkingBots = 0;
     private static int walkingKings = 0;
     private static boolean skeletonSpawn = false;

     static {
         // Bot
         botSteps.setFile("/Audio/Enemy/SlicerBot/Move.wav");
         botStab.setFile("/Audio/Enemy/SlicerBot/Swing.wav");
         botDeath.setFile("/Audio/Enemy/SlicerBot/Death.wav");

         // Ghoul
         ghoulSteps.setFile("/Audio/Enemy/Ghoul/Ghoul_Footsteps.wav");
         ghoulAttack.setFile("/Audio/Enemy/Ghoul/Ghoul_Attack.wav");
         ghoulDeath.setFile("/Audio/Enemy/Ghoul/Ghoul_Death.wav");
         ghoulHit.setFile("/Audio/Enemy/Ghoul/Ghoul_Hit.wav");

         // Summoner
         summonerSteps.setFile("/Audio/Enemy/Summoner/Footsteps.wav");
         summonerAttack.setFile("/Audio/Enemy/Summoner/Swing.wav");
         summonerDeath.setFile("/Audio/Enemy/Summoner/Death.wav");
         summonerHit.setFile("/Audio/Enemy/Summoner/Hit.wav");
         summonerSlam.setFile("/Audio/Enemy/Summoner/HitGround.wav");
         summonerSummon.setFile("/Audio/Enemy/Summoner/Summon.wav");

         // Skeleton
         skeletonSteps.setFile("/Audio/Enemy/Skeleton/Footsteps.wav");
         skeletonAttack.setFile("/Audio/Enemy/Skeleton/Attack.wav");
         skeletonDeath.setFile("/Audio/Enemy/Skeleton/Death.wav");
         skeletonHit.setFile("/Audio/Enemy/Skeleton/Hit.wav");
         skeletonSpawnSound.setFile("/Audio/Enemy/Skeleton/Spawn.wav");

         // King
         kingSteps.setFile("/Audio/Enemy/BloodKing/Footsteps.wav");
         kingHit.setFile("/Audio/Enemy/BloodKing/Hit.wav");
         kingDeath.setFile("/Audio/Enemy/BloodKing/Death.wav");

         kingDodgeDash.setFile("/Audio/Enemy/BloodKing/Attacks/Dodge/Dash.wav");
         kingDodgeSlash1.setFile("/Audio/Enemy/BloodKing/Attacks/Dodge/Slash1.wav");
         kingDodgeSlash2.setFile("/Audio/Enemy/BloodKing/Attacks/Dodge/Slash2.wav");
         kingDodgeStepBack.setFile("/Audio/Enemy/BloodKing/Attacks/Dodge/StepBack.wav");
         kingFinisherCharge.setFile("/Audio/Enemy/BloodKing/Attacks/Finisher/Charge.wav");
         kingFinisherHitGround.setFile("/Audio/Enemy/BloodKing/Attacks/Finisher/HitGround.wav");
         kingFinisherSwing.setFile("/Audio/Enemy/BloodKing/Attacks/Finisher/Swing.wav");
         kingHeartAppear.setFile("/Audio/Enemy/BloodKing/Attacks/Heart/HeartAppear.wav");
         kingHeartKingAppear.setFile("/Audio/Enemy/BloodKing/Attacks/Heart/Appear.wav");
         kingHeartKingSink.setFile("/Audio/Enemy/BloodKing/Attacks/Heart/Sink.wav");
         kingHeartSplash.setFile("/Audio/Enemy/BloodKing/Attacks/Heart/Splash.wav");
         kingSlamHitGround.setFile("/Audio/Enemy/BloodKing/Attacks/Slam/HitGround.wav");
         kingSlamJump.setFile("/Audio/Enemy/BloodKing/Attacks/Slam/Jump.wav");
         kingSlamSwing.setFile("/Audio/Enemy/BloodKing/Attacks/Slam/Swing.wav");
         kingSlashSwing1.setFile("/Audio/Enemy/BloodKing/Attacks/Slash/Swing1.wav");
         kingSlashSwing2.setFile("/Audio/Enemy/BloodKing/Attacks/Slash/Swing2.wav");
         kingStabSlice.setFile("/Audio/Enemy/BloodKing/Attacks/Stab/Slice.wav");
         kingStabStab.setFile("/Audio/Enemy/BloodKing/Attacks/Stab/Stab.wav");
         kingStabWarn.setFile("/Audio/Enemy/BloodKing/Attacks/Stab/Warn.wav");
     }

     public static synchronized void botSteps() {
         if (walkingBots == 0) {
             botSteps.play();
             botSteps.loop();
         }
         walkingBots++;
     }
     public static synchronized void stopBotSteps() {
         if (walkingBots > 0) {
             walkingBots--;
             if (walkingBots == 0) {
                 botSteps.stop();
             }
         }
     }

     public static void botStab() { botStab.play(); }
     public static void botDeath() { botDeath.play(); }

     public static void dodgeDash() { kingDodgeDash.play(); }
     public static void dodgeSlash1() { kingDodgeSlash1.play(); }
     public static void dodgeSlash2() { kingDodgeSlash2.play(); }
     public static void dodgeStepBack() { kingDodgeStepBack.play(); }
     public static void finisherCharge() { kingFinisherCharge.play(); }
     public static void finisherHitGround() { kingFinisherHitGround.play(); }
     public static void finisherSwing() { kingFinisherSwing.play(); }
     public static void heartKingAppear() { kingHeartKingAppear.play(); }
     public static void heartAppear() { kingHeartAppear.play(); }
     public static void heartKingSink() { kingHeartKingSink.play(); }
     public static void heartSplash() { kingHeartSplash.play(); }
     public static void slamHitGround() { kingSlamHitGround.play(); }
     public static void slamJump() { kingSlamJump.play(); }
     public static void slamSwing() { kingSlamSwing.play(); }
     public static void slashSwing1() { kingSlashSwing1.play(); }
     public static void slashSwing2() { kingSlashSwing2.play(); }
     public static void stabSlice() { kingStabSlice.play(); }
     public static void stabStab() { kingStabStab.play(); }
     public static void stabWarn() { kingStabWarn.play(); }

     public static synchronized void kingFootsteps() {
         if (walkingKings == 0) {
             kingSteps.play();
             kingSteps.loop();
         }
         walkingKings++;
     }
     public static synchronized void stopKingFootsteps() {
         if (walkingKings > 0) {
             walkingKings--;
             if (walkingKings == 0) {
                 kingSteps.stop();
             }
         }
     }
     public static void kingDeath() { kingDeath.play(); }
     public static void kingHit() { kingHit.play(); }

     // Summoner
     public static void summonerDeath() { summonerDeath.play(); }
     public static synchronized void summonerAttack() {
         if (attackingSummoners == 0) {
             summonerAttack.play();
         }
         attackingSummoners++;
     }
     public static synchronized void stopSummonerAttack() {
         if (attackingSummoners > 0) {
             attackingSummoners--;
             if (attackingSummoners == 0) {
                 summonerAttack.stop();
             }
         }
     }
     public static void summonerHit() { summonerHit.play(); }
     public static void summonerSlam() { summonerSlam.play(); }
     public static void summonerSummon() { summonerSummon.play(); }
     public static synchronized void summonerFootsteps() {
         if (walkingSummoners == 0) {
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

     // Ghoul
     public static void ghoulDeath() { ghoulDeath.play(); }
     public static synchronized void ghoulAttack() {
         if (attackingGhouls == 0) {
             ghoulAttack.play();
         }
         attackingGhouls++;
     }
     public static synchronized void stopGhoulAttack() {
         if (attackingGhouls > 0) {
             attackingGhouls--;
             if (attackingGhouls == 0) {
                 ghoulAttack.stop();
             }
         }
     }
     public static synchronized void ghoulFootsteps() {
         if (walkingGhouls == 0) {
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
     public static void ghoulHit() { ghoulHit.play(); }

     // Skeleton
     public static void skeletonDeath() { skeletonDeath.play(); }
     public static synchronized void skeletonAttack() {
         if (attackingSkeletons == 0) {
             skeletonAttack.play();
         }
         attackingSkeletons++;
     }
     public static synchronized void stopSkeletonAttack() {
         if (attackingSkeletons > 0) {
             attackingSkeletons--;
             if (attackingSkeletons == 0) {
                 skeletonAttack.stop();
             }
         }
     }
     public static synchronized void skeletonFootsteps() {
         if (walkingSkeletons == 0) {
             skeletonSteps.play();
             skeletonSteps.loop();
         }
         walkingSkeletons++;
     }
     public static synchronized void stopSkeletonFootsteps() {
         if (walkingSkeletons > 0) {
             walkingSkeletons--;
             if (walkingSkeletons == 0) {
                 skeletonSteps.stop();
             }
         }
     }
     public static void skeletonHit() { skeletonHit.play(); }
     public static void skeletonSpawn() {
         if (!skeletonSpawn) {
             skeletonSpawn = true;
             skeletonSpawnSound.play();
         }
     }
     public static void resetSkeletonSpawn() { skeletonSpawn = false; }
 }