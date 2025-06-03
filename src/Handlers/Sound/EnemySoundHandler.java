package Handlers.Sound;

     public class EnemySoundHandler {

         private static final Sound ghoulSteps = new Sound();
         private static final Sound ghoulEffect = new Sound();

         private static final Sound summonerSteps = new Sound();
         private static final Sound summonerEffect = new Sound();

         private static final Sound skeletonSteps = new Sound();
         private static final Sound skeletonEffect = new Sound();

         private static final Sound kingSteps = new Sound();
         private static final Sound kingEffect = new Sound();
         private static final Sound kingHit = new Sound();

         private static int walkingGhouls = 0, attackingGhouls = 0;
         private static int walkingSummoners = 0, attackingSummoners = 0;
         private static int walkingSkeletons = 0, attackingSkeletons = 0;
         private static int walkingKings = 0;
         private static boolean skeletonSpawn = false;

         public static void dodgeDash() {
             playSoundEffect("/Audio/Enemy/BloodKing/Attacks/Dodge/Dash.wav", kingEffect);
         }

         public static void dodgeSlash1() {
             playSoundEffect("/Audio/Enemy/BloodKing/Attacks/Dodge/Slash1.wav", kingEffect);
         }

         public static void dodgeSlash2() {
             playSoundEffect("/Audio/Enemy/BloodKing/Attacks/Dodge/Slash2.wav", kingEffect);
         }

         public static void dodgeStepBack() {
             playSoundEffect("/Audio/Enemy/BloodKing/Attacks/Dodge/StepBack.wav", kingEffect);
         }

         public static void finisherCharge() {
             playSoundEffect("/Audio/Enemy/BloodKing/Attacks/Finisher/Charge.wav", kingEffect);
         }

         public static void finisherHitGround() {
             playSoundEffect("/Audio/Enemy/BloodKing/Attacks/Finisher/HitGround.wav", kingEffect);
         }

         public static void finisherSwing() {
             playSoundEffect("/Audio/Enemy/BloodKing/Attacks/Finisher/Swing.wav", kingEffect);
         }

         public static void heartKingAppear() {
             playSoundEffect("/Audio/Enemy/BloodKing/Attacks/Heart/Appear.wav", kingEffect);
         }

         public static void heartAppear() {
             playSoundEffect("/Audio/Enemy/BloodKing/Attacks/Heart/HeartAppear.wav", kingEffect);
         }

         public static void heartKingSink() {
             playSoundEffect("/Audio/Enemy/BloodKing/Attacks/Heart/Sink.wav", kingEffect);
         }

         public static void heartSplash() {
             playSoundEffect("/Audio/Enemy/BloodKing/Attacks/Heart/Splash.wav", kingEffect);
         }

         public static void slamHitGround() {
             playSoundEffect("/Audio/Enemy/BloodKing/Attacks/Slam/HitGround.wav", kingEffect);
         }

         public static void slamJump() {
             playSoundEffect("/Audio/Enemy/BloodKing/Attacks/Slam/Jump.wav", kingEffect);
         }

         public static void slamSwing() {
             playSoundEffect("/Audio/Enemy/BloodKing/Attacks/Slam/Swing.wav", kingEffect);
         }

         public static void slashSwing1() {
             playSoundEffect("/Audio/Enemy/BloodKing/Attacks/Slash/Swing1.wav", kingEffect);
         }

         public static void slashSwing2() {
             playSoundEffect("/Audio/Enemy/BloodKing/Attacks/Slash/Swing2.wav", kingEffect);
         }

         public static void stabSlice() {
             playSoundEffect("/Audio/Enemy/BloodKing/Attacks/Stab/Slice.wav", kingEffect);
         }

         public static void stabStab() {
             playSoundEffect("/Audio/Enemy/BloodKing/Attacks/Stab/Stab.wav", kingEffect);
         }

         public static void stabWarn() {
             playSoundEffect("/Audio/Enemy/BloodKing/Attacks/Stab/Warn.wav", kingEffect);
         }

         public static synchronized void kingFootsteps() {
             if (walkingKings == 0) {
                 kingSteps.setFile("/Audio/Enemy/BloodKing/Footsteps.wav");
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

         public static void kingDeath() {
            playSoundEffect("/Audio/Enemy/BloodKing/Death.wav", kingEffect);
         }

         public static void kingHit() {
             playSoundEffect("/Audio/Enemy/BloodKing/Hit.wav", kingHit);
         }


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


         public static void skeletonDeath() {
             playSoundEffect("/Audio/Enemy/Skeleton/Death.wav", skeletonEffect);
         }

         public static synchronized void skeletonAttack() {
             if (attackingSkeletons == 0) {
                 skeletonEffect.setFile("/Audio/Enemy/Skeleton/Attack.wav");
                 skeletonEffect.play();
             }
             attackingSkeletons++;
         }

         public static synchronized void stopSkeletonAttack() {
             if (attackingSkeletons > 0) {
                 attackingSkeletons--;
                 if (attackingSkeletons == 0) {
                     skeletonEffect.stop();
                 }
             }
         }

         public static synchronized void skeletonFootsteps() {
             if (walkingSkeletons == 0) {
                 skeletonSteps.setFile("/Audio/Enemy/Skeleton/Footsteps.wav");
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

         public static void skeletonHit() {
             playSoundEffect("/Audio/Enemy/Skeleton/Hit.wav", skeletonEffect);
         }

         public static void skeletonSpawn() {
             if (!skeletonSpawn) {
                    skeletonSpawn = true;
                    playSoundEffect("/Audio/Enemy/Skeleton/Spawn.wav", skeletonEffect);
                }
         }

         public static void resetSkeletonSpawn() {
                skeletonSpawn = false;
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