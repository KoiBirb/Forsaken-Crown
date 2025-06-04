package Handlers;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class ScoreHandler {

    private static String[] names;
    private static int[] scores = new int[10];

    private static final String[] adjectives = {
            "Swift", "Brave", "Silent", "Clever", "Mighty", "Lucky", "Fierce", "Nimble", "Bold", "Wise",
            "Gentle", "Loyal", "Vivid", "Radiant", "Calm", "Daring", "Eager", "Fearless", "Gallant", "Heroic",
            "Jolly", "Keen", "Lively", "Majestic", "Noble", "Quick", "Royal", "Sturdy", "Valiant", "Witty",
            "Zesty", "Agile", "Bright", "Charming", "Diligent", "Energetic", "Faithful", "Graceful", "Humble", "Inventive",
            "Joyful", "Kind", "Lucky", "Merry", "Nimble", "Optimistic", "Patient", "Quiet", "Resourceful", "Steady"
    };

    private static final String[] nouns = {
            "Falcon", "Tiger", "Wolf", "Eagle", "Lion", "Panther", "Dragon", "Shark", "Bear", "Hawk",
            "Fox", "Otter", "Raven", "Stag", "Bull", "Cobra", "Dolphin", "Elk", "Frog", "Goose",
            "Heron", "Ibis", "Jaguar", "Koala", "Lynx", "Moose", "Newt", "Owl", "Puma", "Quail",
            "Ram", "Seal", "Toad", "Urchin", "Viper", "Walrus", "Yak", "Zebra", "Antelope", "Bison",
            "Crane", "Deer", "Egret", "Ferret", "Gull", "Hound", "Ibex", "Jay", "Kite", "Lizard"
    };

    public static void readScoresFromFile(String filename) {
        names = new String[scores.length];
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(filename))) {
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null && i < scores.length) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length >= 2) {
                    // Join all parts except the last as the name
                    StringBuilder nameBuilder = new StringBuilder();
                    for (int j = 0; j < parts.length - 1; j++) {
                        if (j > 0) nameBuilder.append(" ");
                        nameBuilder.append(parts[j]);
                    }
                    names[i] = nameBuilder.toString();
                    try {
                        scores[i] = Integer.parseInt(parts[parts.length - 1]);
                    } catch (NumberFormatException e) {
                        scores[i] = 0;
                    }
                } else {
                    names[i] = "";
                    scores[i] = 0;
                }
                i++;
            }
        } catch (IOException e) {
            System.out.println("Error reading scores from file: " + e.getMessage());
        }
    }

    public static void addScore(String name, int score) {
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] < score) {
                for (int j = scores.length - 1; j > i; j--) {
                    scores[j] = scores[j - 1];
                    names[j] = names[j - 1];
                }
                scores[i] = score;
                names[i] = name;
                break;
            }
        }
    }

    public static void writeScoresToFile(String filename) {

        try (FileWriter writer = new FileWriter(filename)) {
            for (int i = 0; i < 10; i++) {
                writer.write(names[i] + " " + scores[i] + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error writing scores to file: " + e.getMessage());
        }
    }

    private static void sortScores() {
        Arrays.sort(scores);
    }

    public static String generateRandomName() {
        java.util.Random rand = new java.util.Random();
        String adjective = adjectives[rand.nextInt(adjectives.length)];
        String noun = nouns[rand.nextInt(nouns.length)];
        return adjective + " " + noun;
    }
}
