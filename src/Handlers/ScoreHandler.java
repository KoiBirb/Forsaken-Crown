/*
 * ScoreHandler.java
 * Leo Bogaert
 * Jun 7, 2025,
 * Handles leader board scores
 */
package Handlers;

import java.io.*;
import java.util.Arrays;

public class ScoreHandler {

    private static final String LEADERBOARD_PATH = System.getProperty("user.home") + "/ForsakenCrown_Leaderboard.txt";
    private static String[] names = new String[10];
    private static final int[] scores = new int[10];

    private static final String[] adjectives = {
            "Brave", "Calm", "Chilly", "Clever", "Cool", "Cozy", "Crafty", "Daring", "Eager", "Famous",
            "Fancy", "Fast", "Fierce", "Gentle", "Giant", "Glad", "Graceful", "Grand", "Happy", "Heroic",
            "Honest", "Humble", "Jolly", "Joyful", "Kind", "Lively", "Lucky", "Loyal", "Magic", "Mighty",
            "Mild", "Mirthy", "Modest", "Nimble", "Noble", "Patient", "Peaceful", "Proud", "Quick", "Quiet",
            "Rapid", "Rare", "Ready", "Regal", "Rich", "Robust", "Royal", "Shy", "Silly", "Simple",
            "Skillful", "Smart", "Snappy", "Solid", "Speedy", "Spunky", "Sturdy", "Sunny", "Superb", "Swift",
            "Tame", "Tidy", "Timid", "Tricky", "Trusty", "Valiant", "Vast", "Vivid", "Witty", "Wise",
            "Zany", "Zesty", "Agile", "Bright", "Chic", "Chummy", "Dapper", "Dazzle", "Dutiful", "Eager",
            "Elated", "Fabled", "Feisty", "Gallant", "Gleeful", "Gutsy", "Hearty", "Jovial", "Jumpy", "Keen",
            "Lush", "Mellow", "Merry", "Mighty", "Mirthy", "Plucky", "Polite", "Prompt", "Quaint", "Sincere",
            "Able", "Absent", "Absolute", "Accurate", "Adorable", "Adventurous", "Afraid", "Aggressive", "Alert", "Alive",
            "Amused", "Angry", "Annoyed", "Anxious", "Arrogant", "Ashamed", "Attractive", "Average", "Awful", "Bad",
            "Beautiful", "Better", "Bewildered", "Big", "Bitter", "Black", "Blue", "Blushing", "Bored", "Brainy",
            "Breakable", "Brief", "Broad", "Broken", "Busy", "Cautious", "Charming", "Cheerful", "Clean", "Clear",
            "Cloudy", "Clumsy", "Colorful", "Combative", "Comfortable", "Concerned", "Confused", "Cooperative", "Courageous", "Crazy",
            "Creepy", "Crowded", "Cruel", "Curious", "Cute", "Dangerous", "Dark", "Defeated", "Defiant", "Delightful",
            "Depressed", "Determined", "Different", "Difficult", "Disgusted", "Distinct", "Disturbed", "Dizzy", "Doubtful", "Drab",
            "Dull", "Eager", "Easy", "Elegant", "Embarrassed", "Enchanting", "Encouraging", "Energetic", "Enthusiastic", "Envious",
            "Evil", "Excited", "Expensive", "Exuberant", "Fair", "Faithful", "Famous", "Fancy", "Fantastic", "Foolish",
            "Fragile", "Frail", "Friendly", "Frustrated", "Funny", "Fuzzy", "Gentle", "Gifted", "Glamorous", "Gleaming",
            "Glorious", "Good", "Goofy", "Graceful", "Grieving", "Grotesque", "Grumpy", "Handsome", "Happy", "Healthy",
            "Helpful", "Helpless", "Hilarious", "Homeless", "Homely", "Horrible", "Hungry", "Hurt", "Ill", "Important",
            "Impossible", "Inexpensive", "Innocent", "Inquisitive", "Itchy", "Jealous", "Jittery", "Jolly", "Joyous", "Kind",
            "Lazy", "Light", "Lively", "Lonely", "Long", "Lovely", "Lucky", "Magnificent", "Misty", "Modern",
            "Motionless", "Muddy", "Mushy", "Mysterious", "Nasty", "Naughty", "Nervous", "Nice", "Nutty", "Obedient",
            "Obnoxious", "Odd", "Old", "Open", "Outrageous", "Outstanding", "Panicky", "Perfect", "Plain", "Pleasant",
            "Poised", "Poor", "Powerful", "Precious", "Prickly", "Proud", "Puzzled", "Quaint", "Real", "Relieved",
            "Repulsive", "Rich", "Scary", "Selfish", "Shiny", "Shy", "Silly", "Sleepy", "Smiling", "Smoggy",
            "Sore", "Sparkling", "Splendid", "Spotless", "Stormy", "Strange", "Stupid", "Successful", "Super", "Talented",
            "Tame", "Tender", "Tense", "Terrible", "Thankful", "Thoughtful", "Thoughtless", "Tired", "Tough", "Troubled",
            "Ugliest", "Ugly", "Uninterested", "Unsightly", "Unusual", "Upset", "Uptight", "Vast", "Victorious", "Vivacious",
            "Wandering", "Weary", "Wicked", "Wide-eyed", "Wild", "Witty", "Worried", "Worrisome", "Wrong", "Zany",
            "Abandoned", "Able-bodied", "Absolute", "Academic", "Acceptable", "Accessible", "Accidental", "Acclaimed", "Accomplished", "Accurate",
            "Achy", "Acrobatic", "Active", "Actual", "Acute", "Admirable", "Admired", "Adolescent", "Adopted", "Adventurous",
            "Affectionate", "Agonizing", "Agreeable", "Ajar", "Alarming", "Alert", "Alien", "Alive", "All", "Altruistic",
            "Amazing", "Ambitious", "Ample", "Amused", "Ancient", "Angelic", "Annual", "Antique", "Apprehensive", "Appropriate",
            "Aromatic", "Artistic", "Ashen", "Aspiring", "Assertive", "Assured", "Astonishing", "Athletic", "Attached", "Attentive",
            "Attractive", "Authentic", "Authorized", "Automatic", "Available", "Avaricious", "Average", "Aware", "Awesome", "Awkward",
            "Babyish", "Back", "Bad-tempered", "Baggy", "Bare", "Barren", "Basic", "Beautiful", "Belated", "Beloved",
            "Beneficial", "Best", "Better", "Bewitched", "Big-hearted", "Biodegradable", "Bitter", "Bizarre", "Black-and-white", "Blank",
            "Bland", "Bleak", "Blessed", "Blind", "Blissful", "Blond", "Bloody", "Blue-eyed", "Blushing", "Boiling",
            "Bold", "Bony", "Boring", "Bossy", "Both", "Bouncy", "Bountiful", "Bowed", "Brave", "Breakable",
            "Brief", "Bright-eyed", "Brilliant", "Brisk", "Broken", "Bronze", "Brown", "Bruised", "Bubbly", "Bulky",
            "Bumpy", "Buoyant", "Burly", "Bustling", "Busy", "Buttery", "Buzzing", "Calculating", "Calm", "Candid",
            "Canine", "Capital", "Carefree", "Careful", "Careless", "Caring", "Cautious", "Celebrated", "Challenging", "Chance",
            "Changeable", "Charismatic", "Charming", "Cheap", "Cheeky", "Cheerful", "Cheery", "Chief", "Childish", "Childlike",
            "Chilly", "Chubby", "Circular", "Classic", "Clean-cut", "Clear-cut", "Clever", "Close", "Closed", "Cloudless",
            "Clueless", "Clumsy", "Cluttered", "Coarse", "Cold-blooded", "Colorless", "Colossal", "Combative", "Comfortable", "Commanding",
            "Common", "Compassionate", "Competent", "Complete", "Complicated", "Composed", "Concerned", "Concrete", "Confident", "Confused"
    };


    private static final String[] nouns = {
            "Falcon", "Tiger", "Wolf", "Eagle", "Lion", "Panther", "Dragon", "Shark", "Bear", "Hawk",
            "Fox", "Otter", "Raven", "Stag", "Bull", "Cobra", "Elk", "Frog", "Goose", "Heron",
            "Ibis", "Jaguar", "Koala", "Lynx", "Moose", "Newt", "Owl", "Puma", "Quail", "Ram",
            "Seal", "Toad", "Urchin", "Viper", "Yak", "Zebra", "Bison", "Crane", "Deer", "Egret",
            "Ferret", "Gull", "Hound", "Ibex", "Jay", "Kite", "Lizard", "Mole", "Mouse", "Pig",
            "Rabbit", "Robin", "Sheep", "Skunk", "Snail", "Swan", "Turtle", "Weasel", "Wren", "Ant",
            "Bat", "Bee", "Boar", "Bug", "Calf", "Clam", "Crow", "Dingo", "Dove", "Duck",
            "Eel", "Finch", "Gnat", "Goat", "Grouse", "Horse", "Lamb", "Lark", "Mink", "Myna",
            "Osprey", "Panda", "Parrot", "Perch", "Pigeon", "Plover", "Prawn", "Quokka", "Rook", "Shrew",
            "Snipe", "Sole", "Sparrow", "Stoat", "Tapir", "Tern", "Trout", "Vole", "Wasp", "Asp",
            "Addax", "Ayeaye", "Baboon", "Badger", "Basilisk", "Beagle", "Beaver", "Beetle", "Binturong", "Bittern",
            "Blackbird", "Bluebird", "Bluejay", "Bobcat", "Bonobo", "Bream", "Buffalo", "Bullfrog", "Buzzard", "Camel",
            "Capybara", "Caracal", "Cardinal", "Caribou", "Cassowary", "Cat", "Caterpillar", "Cattle", "Chameleon", "Cheetah",
            "Chicken", "Chickadee", "Chimp", "Chinchilla", "Cicada", "Civet", "Clownfish", "Cobra", "Cockatoo", "Condor",
            "Cormorant", "Coyote", "Crocodile", "Cuckoo", "Cuttlefish", "Damselfly", "Darter", "Dinosaur", "Dog", "Dolphin",
            "Donkey", "Dormouse", "Dragonfly", "Duckling", "Dugong", "Echidna", "Elephant", "Emu", "Falconet", "Firefly",
            "Fish", "Flamingo", "Flicker", "Fly", "Gazelle", "Gecko", "Gerbil", "Gibbon", "Giraffe", "Gnatcatcher",
            "Goanna", "Goldfinch", "Goldfish", "Gopher", "Gorilla", "Grebe", "Greenfinch", "Greyhound", "Grizzly", "Grouper",
            "Guinea", "Guppy", "Hamster", "Harrier", "Hartebeest", "Hawkfish", "Hedgehog", "Heron", "Hippopotamus", "Hornet",
            "Horsefly", "Hummingbird", "Hyena", "Iguana", "Impala", "Jackal", "Jackrabbit", "Jaguarundi", "Jerboa", "Jellyfish",
            "Junco", "Kangaroo", "Katydid", "Kingfisher", "Kinkajou", "Kiwi", "Komodo", "Kookaburra", "Krill", "Ladybug",
            "Lapwing", "Leech", "Leopard", "Leopon", "Liger", "Limpet", "Lionfish", "Loach", "Loon", "Lynx",
            "Macaw", "Magpie", "Mallard", "Mandrill", "Manatee", "Mantis", "Marlin", "Marmoset", "Marmot", "Mayfly",
            "Meerkat", "Millipede", "Mole", "Mollusk", "Monkey", "Moorhen", "Mosquito", "Moth", "Mousebird", "Mule",
            "Muskox", "Nandu", "Narwhal", "Nautilus", "Nematode", "Nene", "Nighthawk", "Nightjar", "Nudibranch", "Numbat",
            "Ocelot", "Octopus", "Okapi", "Olingo", "Opossum", "Orangutan", "Orca", "Oriole", "Oryx", "Ostrich"
    };


    /**
     * Initializes the scores and names arrays.
     */
    public static void readScoresFromFile() {
        Arrays.fill(names, "");
        Arrays.fill(scores, 0);
        File file = new File(LEADERBOARD_PATH);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null && i < scores.length) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length >= 2) {
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

    /**
     * Adds a score to the leaderboard.
     * @param name String value of the player's name
     * @param score int value of the player's score
     */
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

    /**
     * Writes the scores to a file.
     */
    public static void writeScoresToFile() {
        try (FileWriter writer = new FileWriter(LEADERBOARD_PATH)) {
            for (int i = 0; i < 10; i++) {
                writer.write((names[i] == null ? "" : names[i]) + " " + scores[i] + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error writing scores to file: " + e.getMessage());
        }
    }

    /**
     * Generates a random name
     * @return String value of the generated name
     */
    public static String generateRandomName() {
        java.util.Random rand = new java.util.Random();
        String adjective = adjectives[rand.nextInt(adjectives.length)];
        String noun = nouns[rand.nextInt(nouns.length)];
        return adjective + " " + noun;
    }

    /**
     * Gets the names arrays.
     * @return String[] array of names
     */
    public static String[] getNames() {
        return Arrays.copyOf(names, names.length);
    }

    /**
     * Gets the scores array.
     * @return int[] array of scores
     */
    public static int[] getScores() {
        return Arrays.copyOf(scores, scores.length);
    }
}
