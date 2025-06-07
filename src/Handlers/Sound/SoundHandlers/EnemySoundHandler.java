/*
 * EnemySoundHandler.java
 * Leo Bogaert
 * Jun 7, 2025,
 * Handles enemy sound effects
 */

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

     private static final Sound shockerSteps = new Sound();
     private static final Sound shockerSwing1 = new Sound();
     private static final Sound shockerSwing2 = new Sound();
     private static final Sound shockerDeath = new Sound();
     private static final Sound shockerHitGround = new Sound();
     private static final Sound shockerHit = new Sound();
     private static final Sound shockerCharge = new Sound();
     private static final Sound shockerEnd = new Sound();

     private static final Sound hiveIdle = new Sound();
     private static final Sound hiveExplode = new Sound();

     private static final Sound waspDeath = new Sound();
     private static final Sound waspSting = new Sound();
     private static final Sound waspFly = new Sound();
     private static final Sound waspHit = new Sound();

     private static final Sound slicerSwing = new Sound();
     private static final Sound slicerHit = new Sound();
     private static final Sound slicerDeath = new Sound();
     private static final Sound slicerFootsteps = new Sound();

     private static int walkingGhouls = 0, attackingGhouls = 0;
     private static int walkingSummoners = 0, attackingSummoners = 0;
     private static int walkingSkeletons = 0, attackingSkeletons = 0;
     private static int walkingBots = 0;
     private static int walkingShockers = 0;
     private static int walkingKings = 0;
     private static int idleHive = 0;
     private static int flyingWasps = 0;
     private static int walkingSlicers = 0;
     private static boolean skeletonSpawn = false;

     // Preload all sound files
     static {

        // Slicer
         slicerDeath.setFile("/Audio/Enemy/HeavySlicer/Death.wav");
         slicerHit.setFile("/Audio/Enemy/HeavySlicer/Hit.wav");
         slicerFootsteps.setFile("/Audio/Enemy/HeavySlicer/Walk.wav");
         slicerSwing.setFile("/Audio/Enemy/HeavySlicer/Swing.wav");


         // Wasp
         waspDeath.setFile("/Audio/Enemy/Wasp/Death.wav");
         waspFly.setFile("/Audio/Enemy/Wasp/Fly.wav");
         waspHit.setFile("/Audio/Enemy/Wasp/Hit.wav");
         waspSting.setFile("/Audio/Enemy/Wasp/Sting.wav");

         // Hive
         hiveIdle.setFile("/Audio/Enemy/Hive/Idle.wav");
         hiveExplode.setFile("/Audio/Enemy/Hive/Explode.wav");

         // Shocker
         shockerSteps.setFile("/Audio/Enemy/CagedShocker/Footsteps.wav");
         shockerSwing1.setFile("/Audio/Enemy/CagedShocker/Sword1.wav");
         shockerSwing2.setFile("/Audio/Enemy/CagedShocker/Sword2.wav");
         shockerDeath.setFile("/Audio/Enemy/CagedShocker/Death.wav");
         shockerHitGround.setFile("/Audio/Enemy/CagedShocker/DeathHitGround.wav");
         shockerHit.setFile("/Audio/Enemy/CagedShocker/Hit.wav");
         shockerCharge.setFile("/Audio/Enemy/CagedShocker/Charge.wav");
         shockerEnd.setFile("/Audio/Enemy/CagedShocker/End.wav");

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

     /**
      * Unmutes all enemy sounds
      */
     public static void unmuteAll() {
         ghoulSteps.setVolume(1.0f);
         ghoulAttack.setVolume(1.0f);
         ghoulDeath.setVolume(1.0f);
         ghoulHit.setVolume(1.0f);

         summonerSteps.setVolume(1.0f);
         summonerAttack.setVolume(1.0f);
         summonerDeath.setVolume(1.0f);
         summonerHit.setVolume(1.0f);
         summonerSlam.setVolume(1.0f);
         summonerSummon.setVolume(1.0f);

         skeletonSteps.setVolume(1.0f);
         skeletonAttack.setVolume(1.0f);
         skeletonDeath.setVolume(1.0f);
         skeletonHit.setVolume(1.0f);
         skeletonSpawnSound.setVolume(1.0f);

         kingSteps.setVolume(1.0f);
         kingHit.setVolume(1.0f);
         kingDeath.setVolume(1.0f);

         kingDodgeDash.setVolume(1.0f);
         kingDodgeSlash1.setVolume(1.0f);
         kingDodgeSlash2.setVolume(1.0f);
         kingDodgeStepBack.setVolume(1.0f);
         kingFinisherCharge.setVolume(1.0f);
         kingFinisherHitGround.setVolume(1.0f);
         kingFinisherSwing.setVolume(1.0f);
         kingHeartAppear.setVolume(1.0f);
         kingHeartKingAppear.setVolume(1.0f);
         kingHeartKingSink.setVolume(1.0f);
         kingHeartSplash.setVolume(1.0f);
         kingSlamHitGround.setVolume(1.0f);
         kingSlamJump.setVolume(1.0f);
         kingSlamSwing.setVolume(1.0f);
         kingSlashSwing1.setVolume(1.0f);
         kingSlashSwing2.setVolume(1.0f);
         kingStabSlice.setVolume(1.0f);
         kingStabStab.setVolume(1.0f);
         kingStabWarn.setVolume(1.0f);

         botSteps.setVolume(1.0f);
         botStab.setVolume(1.0f);
         botDeath.setVolume(1.0f);

         shockerSteps.setVolume(1.0f);
         shockerSwing1.setVolume(1.0f);
         shockerSwing2.setVolume(1.0f);
         shockerDeath.setVolume(1.0f);
         shockerHitGround.setVolume(1.0f);
         shockerHit.setVolume(1.0f);
         shockerCharge.setVolume(1.0f);
         shockerEnd.setVolume(1.0f);

         hiveIdle.setVolume(1.0f);
         hiveExplode.setVolume(1.0f);

         waspDeath.setVolume(1.0f);
         waspSting.setVolume(1.0f);
         waspFly.setVolume(1.0f);
         waspHit.setVolume(1.0f);

         slicerSwing.setVolume(1.0f);
         slicerHit.setVolume(1.0f);
         slicerDeath.setVolume(1.0f);
         slicerFootsteps.setVolume(1.0f);
     }

     /**
      * Mutes all enemy sounds
      */
     public static void muteAll() {
         ghoulSteps.setVolume(0f);
         ghoulAttack.setVolume(0f);
         ghoulDeath.setVolume(0f);
         ghoulHit.setVolume(0f);

         summonerSteps.setVolume(0f);
         summonerAttack.setVolume(0f);
         summonerDeath.setVolume(0f);
         summonerHit.setVolume(0f);
         summonerSlam.setVolume(0f);
         summonerSummon.setVolume(0f);

         skeletonSteps.setVolume(0f);
         skeletonAttack.setVolume(0f);
         skeletonDeath.setVolume(0f);
         skeletonHit.setVolume(0f);
         skeletonSpawnSound.setVolume(0f);

         kingSteps.setVolume(0f);
         kingHit.setVolume(0f);
         kingDeath.setVolume(0f);

         kingDodgeDash.setVolume(0f);
         kingDodgeSlash1.setVolume(0f);
         kingDodgeSlash2.setVolume(0f);
         kingDodgeStepBack.setVolume(0f);
         kingFinisherCharge.setVolume(0f);
         kingFinisherHitGround.setVolume(0f);
         kingFinisherSwing.setVolume(0f);
         kingHeartAppear.setVolume(0f);
         kingHeartKingAppear.setVolume(0f);
         kingHeartKingSink.setVolume(0f);
         kingHeartSplash.setVolume(0f);
         kingSlamHitGround.setVolume(0f);
         kingSlamJump.setVolume(0f);
         kingSlamSwing.setVolume(0f);
         kingSlashSwing1.setVolume(0f);
         kingSlashSwing2.setVolume(0f);
         kingStabSlice.setVolume(0f);
         kingStabStab.setVolume(0f);
         kingStabWarn.setVolume(0f);

         botSteps.setVolume(0f);
         botStab.setVolume(0f);
         botDeath.setVolume(0f);

         shockerSteps.setVolume(0f);
         shockerSwing1.setVolume(0f);
         shockerSwing2.setVolume(0f);
         shockerDeath.setVolume(0f);
         shockerHitGround.setVolume(0f);
         shockerHit.setVolume(0f);
         shockerCharge.setVolume(0f);
         shockerEnd.setVolume(0f);

         hiveIdle.setVolume(0f);
         hiveExplode.setVolume(0f);

         waspDeath.setVolume(0f);
         waspSting.setVolume(0f);
         waspFly.setVolume(0f);
         waspHit.setVolume(0f);

         slicerSwing.setVolume(0f);
         slicerHit.setVolume(0f);
         slicerDeath.setVolume(0f);
         slicerFootsteps.setVolume(0f);
     }

     /**
      * Plays Slicer hit sound
      */
     public static void slicerHit() {
         slicerHit.play();
     }

     /**
      * Plays Slicer death sound
      */
     public static void slicerDeath() {
         slicerDeath.play();
     }

     /**
      * Plays Slicer swing sound
      */
     public static void slicerSwing() {
         slicerSwing.play();
     }

     /**
      * Plays Slicer footsteps
      */
     public static synchronized void slicerFootsteps() {
         if (walkingSlicers == 0) {
             slicerFootsteps.play();
             slicerFootsteps.loop();
         }
         walkingSlicers++;
     }

     /**
      * Stops Slicer footsteps
      */
     public static synchronized void stopSlicerFootsteps() {
         if (walkingSlicers > 0) {
             walkingSlicers--;
             if (walkingSlicers == 0) {
                 slicerFootsteps.stop();
             }
         }
     }

     /**
      * Plays wasp hit sound
      */
     public static void waspHit() {
         waspHit.play();
     }

     /**
      * Plays wasp death sound
      */
     public static void waspDeath() {
         waspDeath.play();
     }

     /**
      * Plays wasp sting sound
      */
     public static void waspSting() {
         waspSting.play();
     }

     /**
      * Plays wasp flying sound
      */
     public static synchronized void waspFly() {
         if (flyingWasps == 0) {
             waspFly.play();
             waspFly.loop();
         }
         flyingWasps++;
     }

     /**
      * Stops wasp flying sound
      */
     public static synchronized void stopWaspFly() {
         if (flyingWasps > 0) {
             flyingWasps--;
             if (flyingWasps == 0) {
                 waspFly.stop();
             }
         }
     }

     /**
      * Plays hive explosion sound
      */
     public static void hiveExplode() {
         hiveExplode.play();
     }

     /**
      * Plays hive idle sound
      */
     public static synchronized void hiveIdle() {
         if (idleHive == 0) {
             hiveIdle.play();
             hiveIdle.loop();
         }
         idleHive++;
     }

     /**
      * Stops hive idle sound
      */
     public static synchronized void stopHiveIdle() {
         if (idleHive > 0) {
             idleHive--;
             if (idleHive == 0) {
                 hiveIdle.stop();
             }
         }
     }

     /**
      * Plays shocker footsteps sound
      */
     public static synchronized void shockerSteps() {
         if (walkingShockers == 0) {
             shockerSteps.play();
             shockerSteps.loop();
         }
         walkingShockers++;
     }

     /**
      * Stops shocker footsteps sound
      */
     public static synchronized void stopShockerSteps() {
         if (walkingShockers > 0) {
             walkingShockers--;
             if (walkingShockers == 0) {
                 shockerSteps.stop();
             }
         }
     }

     /**
      * Plays shockers first swing sound
      */
     public static void shockerSwing1() {
         shockerSwing1.play();
     }

     /**
      * Plays shocker second swing sound
      */
     public static void shockerSwing2() {
         shockerSwing2.play();
     }

     /**
      * Plays shocker death sound
      */
     public static void shockerDeath() {
         shockerDeath.play();
     }

     /**
      * Plays shocker hit sound
      */
     public static void shockerHit() {
         shockerHit.play();
     }

     /**
      * Plays shocker hit ground sound
      */
     public static void shockerHitGround() {
         shockerHitGround.play();
     }

     /**
      * Plays shocker charge up sound
      */
     public static void shockerCharge() {
         shockerCharge.play();
     }

     /**
      * Plays shocker 2nd charge up sound
      */
     public static void shockerEnd() {
         shockerEnd.play();
     }

     /**
      * Plays bot footsteps sound
      */
     public static synchronized void botSteps() {
         if (walkingBots == 0) {
             botSteps.play();
             botSteps.loop();
         }
         walkingBots++;
     }

     /**
      * Stops bot footsteps sound
      */
     public static synchronized void stopBotSteps() {
         if (walkingBots > 0) {
             walkingBots--;
             if (walkingBots == 0) {
                 botSteps.stop();
             }
         }
     }

     /**
      * Plays bot stab sound
      */
     public static void botStab() {
         botStab.play();
     }

     /**
      * Plays bot death sound
      */
     public static void botDeath() {
         botDeath.play();
     }

     /**
      * Plays king dodge sound
      */
     public static void dodgeDash() {
         kingDodgeDash.play();
     }

     /**
      * Plays king dodge first slash sound
      */
     public static void dodgeSlash1() {
         kingDodgeSlash1.play();
     }

     /**
      * Plays king dodge second slash sound
      */
     public static void dodgeSlash2() {
         kingDodgeSlash2.play();
     }

     /**
      * Plays king dodge stepback sound
      */
     public static void dodgeStepBack() {
         kingDodgeStepBack.play();
     }

     /**
      * Plays king finisher charge sound
      */
     public static void finisherCharge() {
         kingFinisherCharge.play();
     }

     /**
      * Plays king finisher hit ground sound
      */
     public static void finisherHitGround() {
         kingFinisherHitGround.play();
     }

     /**
      * Plays king finisher swing sound
      */
     public static void finisherSwing() {
         kingFinisherSwing.play();
     }

     /**
      * Plays king appear sound
      */
     public static void heartKingAppear() {
         kingHeartKingAppear.play();
     }

     /**
      * Plays king heart appear sound
      */
     public static void heartAppear() {
         kingHeartAppear.play();
     }

     /**
      * Plays king disappear sound
      */
     public static void heartKingSink() {
         kingHeartKingSink.play();
     }

     /**
      * Plays king heart splash sound
      */
     public static void heartSplash() {
         kingHeartSplash.play();
     }

    /**
    * Plays king slam hit ground sound
    */
     public static void slamHitGround() {
         kingSlamHitGround.play();
     }

    /**
    * Plays king slam jump sound
    */
     public static void slamJump() {
         kingSlamJump.play();
     }

     /**
      * Plays king slam swing sound
      */
     public static void slamSwing() {
         kingSlamSwing.play();
     }

    /**
    * Plays king slash first swing sound
    */
     public static void slashSwing1() {
         kingSlashSwing1.play();
     }

    /**
     * Plays king slash second swing sound
     */
     public static void slashSwing2() {
         kingSlashSwing2.play();
     }

     /**
      * Plays king stab second swing sound
      */
     public static void stabSlice() {
         kingStabSlice.play();
     }

     /**
      * Plays king stab first swing sound
      */
     public static void stabStab() {
         kingStabStab.play();
     }

     /**
      * Plays king warning for stab sound
      */
     public static void stabWarn() {
         kingStabWarn.play();
     }

     /**
      * Plays kings footsteps
      */
     public static synchronized void kingFootsteps() {
         if (walkingKings == 0) {
             kingSteps.play();
             kingSteps.loop();
         }
         walkingKings++;
     }

     /**
      * Stops kings footsteps
      */
     public static synchronized void stopKingFootsteps() {
         if (walkingKings > 0) {
             walkingKings--;
             if (walkingKings == 0) {
                 kingSteps.stop();
             }
         }
     }

     /**
      * Plays king death sound
      */
     public static void kingDeath() {
         kingDeath.play();
     }

     /**
      * Plays king hit sound
      */
     public static void kingHit() {
         kingHit.play();
     }

     /**
      * Plays summoner death sound
      */
     public static void summonerDeath() {
         summonerDeath.play();
     }

     /**
      * Plays summoner attack sound
      */
     public static synchronized void summonerAttack() {
         if (attackingSummoners == 0) {
             summonerAttack.play();
         }
         attackingSummoners++;
     }

     /**
      * Stops summoner attack sound
      */
     public static synchronized void stopSummonerAttack() {
         if (attackingSummoners > 0) {
             attackingSummoners--;
             if (attackingSummoners == 0) {
                 summonerAttack.stop();
             }
         }
     }

     /**
      * Plays summoner hit sound
      */
     public static void summonerHit() {
         summonerHit.play();
     }

     /**
      * Plays summoner slam sound
      */
     public static void summonerSlam() {
         summonerSlam.play();
     }

     /**
      * Plays summoner summon sound
      */
     public static void summonerSummon() {
         summonerSummon.play();
     }

     /**
      * Plays summoner footsteps
      */
     public static synchronized void summonerFootsteps() {
         if (walkingSummoners == 0) {
             summonerSteps.play();
             summonerSteps.loop();
         }
         walkingSummoners++;
     }

     /**
      * Stops summoner footsteps
      */
     public static synchronized void stopSummonerFootsteps() {
         if (walkingSummoners > 0) {
             walkingSummoners--;
             if (walkingSummoners == 0) {
                 summonerSteps.stop();
             }
         }
     }

     /**
      * Plays ghoul death sound
      */
     public static void ghoulDeath() {
         ghoulDeath.play();
     }

     /**
      * Plays ghoul attack sound
      */
     public static synchronized void ghoulAttack() {
         if (attackingGhouls == 0) {
             ghoulAttack.play();
         }
         attackingGhouls++;
     }

     /**
      * Stops ghoul attack sound
      */
     public static synchronized void stopGhoulAttack() {
         if (attackingGhouls > 0) {
             attackingGhouls--;
             if (attackingGhouls == 0) {
                 ghoulAttack.stop();
             }
         }
     }

     /**
      * Plays ghoul footsteps
      */
     public static synchronized void ghoulFootsteps() {
         if (walkingGhouls == 0) {
             ghoulSteps.play();
             ghoulSteps.loop();
         }
         walkingGhouls++;
     }

     /**
      * Stops ghoul footsteps
      */
     public static synchronized void stopGhoulFootsteps() {
         if (walkingGhouls > 0) {
             walkingGhouls--;
             if (walkingGhouls == 0) {
                 ghoulSteps.stop();
             }
         }
     }

     /**
      * Plays ghoul hit sound
      */
     public static void ghoulHit() {
         ghoulHit.play();
     }

     /**
      * Plays Skeleton death sound
      */
     public static void skeletonDeath() {
         skeletonDeath.play();
     }

     /**
      * Plays Skeleton attack sound
      */
     public static synchronized void skeletonAttack() {
         if (attackingSkeletons == 0) {
             skeletonAttack.play();
         }
         attackingSkeletons++;
     }

     /**
      * Stops Skeleton attack sound
      */
     public static synchronized void stopSkeletonAttack() {
         if (attackingSkeletons > 0) {
             attackingSkeletons--;
             if (attackingSkeletons == 0) {
                 skeletonAttack.stop();
             }
         }
     }

     /**
      * Plays Skeleton footsteps
      */
     public static synchronized void skeletonFootsteps() {
         if (walkingSkeletons == 0) {
             skeletonSteps.play();
             skeletonSteps.loop();
         }
         walkingSkeletons++;
     }

     /**
      * Stops Skeleton footsteps
      */
     public static synchronized void stopSkeletonFootsteps() {
         if (walkingSkeletons > 0) {
             walkingSkeletons--;
             if (walkingSkeletons == 0) {
                 skeletonSteps.stop();
             }
         }
     }

     /**
      * Plays Skeleton hit sound
      */
     public static void skeletonHit() {
         skeletonHit.play();
     }

     /**
      * Plays Skeleton spawn sound
      */
     public static void skeletonSpawn() {
         if (!skeletonSpawn) {
             skeletonSpawn = true;
             skeletonSpawnSound.play();
         }
     }

    /**
    * Resets the skeleton spawns sound state
    */
     public static void resetSkeletonSpawn() { skeletonSpawn = false; }
 }