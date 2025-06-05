package Handlers;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class ScoreHandler {

    private static String[] names;
    private static int[] scores = new int[10];

    private static String[] adjectives = {
        "Brave", "Calm", "Chilly", "Clever", "Cool", "Cozy", "Crafty", "Daring", "Eager", "Famous",
        "Fancy", "Fast", "Fierce", "Gentle", "Giant", "Glad", "Graceful", "Grand", "Happy", "Heroic",
        "Honest", "Humble", "Jolly", "Joyful", "Kind", "Lively", "Lucky", "Loyal", "Magic", "Mighty",
        "Mild", "Mirthy", "Modest", "Nimble", "Noble", "Patient", "Peaceful", "Proud", "Quick", "Quiet",
        "Rapid", "Rare", "Ready", "Regal", "Rich", "Robust", "Royal", "Shy", "Silly", "Simple",
        "Skillful", "Smart", "Snappy", "Solid", "Speedy", "Spunky", "Sturdy", "Sunny", "Superb", "Swift",
        "Tame", "Tidy", "Timid", "Tricky", "Trusty", "Valiant", "Vast", "Vivid", "Witty", "Wise",
        "Zany", "Zesty", "Agile", "Bright", "Chic", "Chummy", "Dapper", "Dazzle", "Dutiful", "Eager",
        "Elated", "Fabled", "Feisty", "Gallant", "Gleeful", "Gutsy", "Hearty", "Jovial", "Jumpy", "Keen",
        "Lush", "Mellow", "Merry", "Mighty", "Mirthy", "Plucky", "Polite", "Prompt", "Quaint", "Sincere"
    };

    private static String[] nouns = {
        "Falcon", "Tiger", "Wolf", "Eagle", "Lion", "Panther", "Dragon", "Shark", "Bear", "Hawk",
        "Fox", "Otter", "Raven", "Stag", "Bull", "Cobra", "Elk", "Frog", "Goose", "Heron",
        "Ibis", "Jaguar", "Koala", "Lynx", "Moose", "Newt", "Owl", "Puma", "Quail", "Ram",
        "Seal", "Toad", "Urchin", "Viper", "Yak", "Zebra", "Bison", "Crane", "Deer", "Egret",
        "Ferret", "Gull", "Hound", "Ibex", "Jay", "Kite", "Lizard", "Mole", "Mouse", "Otter",
        "Pig", "Rabbit", "Robin", "Sheep", "Skunk", "Snail", "Swan", "Turtle", "Weasel", "Wren",
        "Ant", "Bat", "Bee", "Boar", "Bug", "Calf", "Clam", "Crow", "Dingo", "Dove", "Duck",
        "Eel", "Finch", "Gnat", "Goat", "Grouse", "Horse", "Lamb", "Lark", "Mink", "Myna",
        "Osprey", "Panda", "Parrot", "Perch", "Pigeon", "Plover", "Prawn", "Quokka", "Rook", "Shrew",
        "Snipe", "Sole", "Sparrow", "Stoat", "Tapir", "Tern", "Trout", "Vole", "Wasp", "Yak"
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

    public static String generateRandomName() {
        java.util.Random rand = new java.util.Random();
        String adjective = adjectives[rand.nextInt(adjectives.length)];
        String noun = nouns[rand.nextInt(nouns.length)];
        return adjective + " " + noun;
    }

    public static String[] getNames() {
        return Arrays.copyOf(names, names.length);
    }

    public static int[] getScores() {
        return Arrays.copyOf(scores, scores.length);
    }
}
