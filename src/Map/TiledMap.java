/*
 * TiledMap.java
 * Leo Bogaert
 * April 19, 2025,
 * This class reads map data from a jar file and calculates
 * the correct camera position to display the map and backgrounds.
 */

package Map;

    import java.awt.*;
    import java.awt.geom.AffineTransform;
    import java.awt.image.BufferedImage;
    import java.io.FileNotFoundException;
    import java.io.FileReader;
    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.HashMap;

    import Handlers.CollisionHandler;
    import Handlers.ImageHandler;
    import Handlers.Vector2;
    import Main.Panels.GamePanel;
    import org.json.simple.JSONArray;
    import org.json.simple.JSONObject;
    import org.json.simple.parser.JSONParser;
    import org.json.simple.parser.ParseException;

    import static Main.Panels.GamePanel.*;

public class TiledMap {

    // File reading
    private final ArrayList<BufferedImage> tileSets;
    private final String mapPath;
    private final JSONParser parser;
    private ArrayList<int[][]> mapLayers;
    private int mapWidth;
    private int mapHeight;
    private static int tileSetTileSize;
    private JSONArray roomData;
    private ArrayList<BufferedImage[]> backgrounds;
    private HashMap<BufferedImage, Integer> tilesetOffset;

    // Camera room switching
    private final int CAMERADELAY = 15;
    private int cameraDelayCounter;
    private boolean roomChanged;
    private int oldRoomWidth;
    private int oldRoomHeight;

    private final double SCALE = GamePanel.scale;
    private Vector2 cameraPosition;

    // Room data
    private Vector2 roomScreenPos;
    private int roomWidth;
    private int roomHeight;
    private int minX, maxX, minY, maxY;

    /**
     * Constructor
     * Initializes the map path and loads the map data.
     */
    public TiledMap() {
        this.mapPath = "src/Assets/Map/forsakenMap.tmj";
        this.parser = new JSONParser();
        this.tileSets = new ArrayList<>();

        this.cameraDelayCounter = 0;
        this.roomChanged = false;
        this.oldRoomWidth = 0;
        this.oldRoomHeight = 0;

        this.cameraPosition = new Vector2(0, 0);

        mapLayers = new ArrayList<>();
        tilesetOffset = new HashMap<>();

        // Add each tileset image to the list
        tileSets.add(ImageHandler.loadImage("Assets/Images/Tilesets/Map/pixil-frame-0 (2).png"));
        tileSets.add(ImageHandler.loadImage("Assets/Images/Tilesets/Map/pixil-frame-0.png"));
        tileSets.add(ImageHandler.loadImage("Assets/Images/Tilesets/Map/DARK Edition Tileset No background.png"));

        loadMap();
        loadBackgrounds();
    }

    /**
     * Loads the background images for the map.
     */
    private void loadBackgrounds(){
        backgrounds = new ArrayList<>();

        // The columns
        BufferedImage[] backgroundLayers = new BufferedImage[4];
        backgroundLayers[0] = ImageHandler.loadImage("Assets/Images/Backgrounds/The Columns/Horizontal/layer 1.png");
        backgroundLayers[1] = ImageHandler.loadImage("Assets/Images/Backgrounds/The Columns/Horizontal/layer 2.png");
        backgroundLayers[2] = ImageHandler.loadImage("Assets/Images/Backgrounds/The Columns/Horizontal/layer 3.png");
        backgroundLayers[3] = ImageHandler.loadImage("Assets/Images/Backgrounds/The Columns/Horizontal/layer 4.png");
        backgrounds.add(backgroundLayers);

    }



    /**
     * Reads data from JSON file and stores it in JSON arrays
     */
    private void loadMap() {
        try (FileReader reader = new FileReader(mapPath)) {

            tilesetOffset.put (tileSets.get(0), 775);
            tilesetOffset.put(tileSets.get(1), 0);
            tilesetOffset.put(tileSets.get(2), 307);

            JSONObject mapData = (JSONObject) parser.parse(reader);
            mapWidth = ((Long) mapData.get("width")).intValue();
            mapHeight = ((Long) mapData.get("height")).intValue();
            tileSetTileSize = ((Long) mapData.get("tilewidth")).intValue();

            JSONArray layers = (JSONArray) mapData.get("layers");

            // door glow layer
            JSONObject layer = (JSONObject) layers.get(0);
            JSONArray data = (JSONArray) layer.get("data");

            mapLayers.add(new int[mapHeight][mapWidth]);
            for (int i = 0; i < mapHeight; i++) {
                for (int j = 0; j < mapWidth; j++) {
                    mapLayers.get(0)[i][j] = ((Long) data.get(i * mapWidth + j)).intValue();
                }
            }

            // base tile layer
            layer = (JSONObject) layers.get(1);
            data = (JSONArray) layer.get("data");

            mapLayers.add(new int[mapHeight][mapWidth]);
            for (int i = 0; i < mapHeight; i++) {
                for (int j = 0; j < mapWidth; j++) {
                    mapLayers.get(1)[i][j] = ((Long) data.get(i * mapWidth + j)).intValue();
                }
            }

            // dark edition tile layer
            layer = (JSONObject) layers.get(2);
            data = (JSONArray) layer.get("data");

            mapLayers.add(new int[mapHeight][mapWidth]);
            for (int i = 0; i < mapHeight; i++) {
                for (int j = 0; j < mapWidth; j++) {
                    mapLayers.get(2)[i][j] = ((Long) data.get(i * mapWidth + j)).intValue();
                }
            }

            // Room positions
            JSONObject roomLayer = (JSONObject) layers.get(3);
            roomData = (JSONArray) roomLayer.get("data");

            minX = mapWidth; minY = mapHeight;
            maxX = 0; maxY = 0;

            // set initial room boundaries
            for (int i = 0; i < mapHeight; i++) {
                for (int j = 0; j < mapWidth; j++) {
                    int tileId = ((Long) roomData.get(i * mapWidth + j)).intValue();
                    if (tileId != 0) {
                        minX = Math.min(minX, j);
                        minY = Math.min(minY, i);
                        maxX = Math.max(maxX, j);
                        maxY = Math.max(maxY, i);
                    }
                }
            }

            JSONObject collidables = (JSONObject) layers.get(4);
            JSONArray collidablesData = (JSONArray) collidables.get("data");

            int[][] collidablesTiles = new int[mapHeight][mapWidth];
            for (int i = 0; i < mapHeight; i++) {
                for (int j = 0; j < mapWidth; j++) {
                    collidablesTiles[i][j] = ((Long) collidablesData.get(i * mapWidth + j)).intValue();
                }
            }

            CollisionHandler.setCollidableTiles(collidablesTiles);

            roomScreenPos = new Vector2(minX * tileSetTileSize, minY * tileSetTileSize);
            roomWidth = (maxX - minX + 1) * tileSetTileSize;
            roomHeight = (maxY - minY + 1) * tileSetTileSize;

        } catch (FileNotFoundException e) {
            System.out.println("Error finding map file: " + e.getMessage());
        } catch (IOException e){
            System.out.println("Error reading map file: " + e.getMessage());
        } catch (ParseException e) {
            System.out.println("Error parsing map file: " + e.getMessage());
        }
    }


    /**
     * Finds room dimensions, and updates cameras target position
     */
    public void update() {
        int scaledTileSize = (int) (tileSetTileSize * SCALE);

        int playerTileX = (int) (player.getPosition().x / scaledTileSize);
        int playerTileY = (int) (player.getPosition().y / scaledTileSize);

        int tileId = 1; // room tile number

        // check if inside a wall or out of bounds
        if (playerTileX < 0 || playerTileX >= mapWidth || playerTileY < 0 || playerTileY >= mapHeight ||
                ((Long) roomData.get(playerTileY * mapWidth + playerTileX)).intValue() == tileId) {
            return;
        }

        int newMinX = playerTileX, newMaxX = playerTileX;
        int newMinY = playerTileY, newMaxY = playerTileY;

        // Find room dimensions
        while (newMinX >= 0 && ((Long) roomData.get(playerTileY * mapWidth + (newMinX - 1))).intValue() != tileId) {
            newMinX--;
        }
        while (newMaxX < mapWidth - 1 && ((Long) roomData.get(playerTileY * mapWidth + (newMaxX + 1))).intValue() != tileId) {
            newMaxX++;
        }

        while (newMinY >= 0 && ((Long) roomData.get((newMinY - 1) * mapWidth + playerTileX)).intValue() != tileId) {
            newMinY--;
        }
        while (newMaxY < mapHeight - 1 && ((Long) roomData.get((newMaxY + 1) * mapWidth + playerTileX)).intValue() != tileId) {
            newMaxY++;
        }

        // Check if the room has changed
        if (roomWidth != (newMaxX - newMinX + 1) * scaledTileSize ||
                roomHeight != (newMaxY - newMinY + 1) * scaledTileSize) {

            roomWidth = (newMaxX - newMinX + 1) * scaledTileSize;
            roomHeight = (newMaxY - newMinY + 1) * scaledTileSize;

            GamePanel.roomTransition();
            roomChanged = true;
            cameraDelayCounter = CAMERADELAY;
        }

        Vector2 targetRoomPosition = new Vector2(
                (newMinX * scaledTileSize) - (GamePanel.screenWidth / 2 - (double) roomWidth / 2),
                (newMinY * scaledTileSize) - (GamePanel.screenHeight / 2 - (double) roomHeight / 2)
        );

        // Delay camera movement
        if (cameraDelayCounter > 0) {
            cameraDelayCounter--;
        } else if (roomChanged) {
            minX = newMinX;
            maxX = newMaxX;
            minY = newMinY;
            maxY = newMaxY;

            oldRoomHeight = roomHeight;
            oldRoomWidth = roomWidth;

            double movementFactor = 0.08;
            roomScreenPos.x = roomScreenPos.x * (1 - movementFactor) + targetRoomPosition.x * movementFactor;
            roomScreenPos.y = roomScreenPos.y * (1 - movementFactor) + targetRoomPosition.y * movementFactor;

            if (Math.abs(roomScreenPos.x - targetRoomPosition.x) < 1 &&
                    Math.abs(roomScreenPos.y - targetRoomPosition.y) < 1) {
                roomChanged = false;
            }
        }

        cameraPosition = getCameraPos();
    }

    /**
     * Calculates the correct camera position based on the player's position in a room
     * @return Vector2 camera screen position x,y
     */
    public Vector2 getCameraPos() {

        // Camera pos for player center
        double cameraX = player.getPosition().x - GamePanel.screenWidth / 2;
        double cameraY = player.getPosition().y - GamePanel.screenHeight / 2;

        int cameraMargin = 400;

        // Check what side of room player is on and see if camera is farther than max camera pan
        if (roomWidth > GamePanel.screenWidth) {
            cameraX = (cameraX < roomScreenPos.x) ? Math.max(roomScreenPos.x - cameraMargin, cameraX) :
                    Math.min(cameraX, roomScreenPos.x + roomWidth - cameraMargin * 3.88);
        } else {
            cameraX = roomScreenPos.x; // room fits screen
        }

        if (roomHeight > GamePanel.screenHeight) {
            cameraY = (cameraY < roomScreenPos.y) ? Math.max(roomScreenPos.y - 2 * cameraMargin, cameraY) :
                    Math.min(cameraY, roomScreenPos.y + roomHeight - 3 * cameraMargin);
        } else {
            cameraY = roomScreenPos.y;
        }

        return new Vector2(cameraX, cameraY);
    }

    /**
     * Calculates the screen position of the room
     * @return Vector2 room screen position x,y
     */
    private Vector2 getScreenRoomPos() {

        int scaledTileSize = (int) (tileSetTileSize * SCALE);

        int playerTileX = (int) player.getPosition().x / scaledTileSize;
        int playerTileY = (int) player.getPosition().y / scaledTileSize;

        return new Vector2((int) (((playerTileX - (playerTileX - minX)) * scaledTileSize) - cameraPosition.x),
                            (int) (((playerTileY - (playerTileY - minY)) * scaledTileSize) - cameraPosition.y));
    }

    /**
     * Draws the parallax background layers
     * @param g2 Graphics2D object
     * @param layers BufferedImage array of background layers
     * @param parallaxFactors double array of parallax factors for each layer
     */
   private void drawParallaxBackground(Graphics2D g2, BufferedImage[] layers, double[] parallaxFactors) {

       int scaledTileSize = (int) (tileSetTileSize * SCALE);

       Vector2 roomScreenPos = getScreenRoomPos();

       int playerTileX = (int) player.getPosition().x / scaledTileSize;
       int playerTileY = (int) player.getPosition().y / scaledTileSize;

       for (int i = 0; i < layers.length; i++) {
           BufferedImage layer = layers[i];
           double parallaxFactor = parallaxFactors[i];

           int playerDistanceX = playerTileX - minX;
           int playerDistanceY = playerTileY - minY;

           roomScreenPos.x = roomScreenPos.x - 2 * (playerDistanceX * parallaxFactor);
           roomScreenPos.y = roomScreenPos.y - (playerDistanceY * parallaxFactor);


           g2.drawImage(layer, (int) roomScreenPos.x - scaledTileSize - 2, (int) roomScreenPos.y - 2 - scaledTileSize, oldRoomWidth + roomWidth/15 + 2 * scaledTileSize, oldRoomHeight + roomHeight/15 + 2 *scaledTileSize, null);
       }
   }


   /**
    * Covers the screen with a black rectangle
    * @param g2 Graphics2D object
    */
   public void coverScreen(Graphics2D g2) {

       Vector2 roomScreenPos = getScreenRoomPos();

       g2.setColor(Color.BLACK);

       g2.fillRect(0, 0, (int) screenWidth, (int) roomScreenPos.y - getScaledTileSize() + 2);

       g2.fillRect(0, (int) (roomScreenPos.y + oldRoomHeight - 2 + getScaledTileSize()),
               (int) screenWidth, (int) (screenHeight));
       g2.fillRect(0, 0,
               (int) roomScreenPos.x - getScaledTileSize() + 2, (int) screenHeight);

       g2.fillRect((int) (roomScreenPos.x + oldRoomWidth - 2 + getScaledTileSize()), 0,
               (int) screenWidth,(int) screenHeight);
   }


    /**
     * Draws Map and backgrounds
     * @param g2 Graphics2D object
     */
    public void drawMap(Graphics2D g2) {
        int scaledTileSize = (int) (tileSetTileSize * SCALE);

        drawParallaxBackground(g2, backgrounds.get(0), new double[]{0.1, 0.2, 0.4, 0.6});

        // loop through layers
        for (int k = 0; k < 3; k++) {

            // Only draw tiles in room boundaries
            for (int i = minY - 1; i <= maxY + 1; i++) {
                for (int j = minX - 1; j <= maxX + 1; j++) {

                    if (i < 0 || i >= mapHeight || j < 0 || j >= mapWidth)
                        continue;

                    int tileId = mapLayers.get(k)[i][j];


                    if (tileId == 0)
                        continue;

                    int tileWorldX = j * scaledTileSize;
                    int tileWorldY = i * scaledTileSize;


                    BufferedImage tileSetImage = tileSets.get(k);

                    boolean flipHorizontally = (tileId & 0x80000000) != 0;
                    boolean flipVertically = (tileId & 0x40000000) != 0;
                    boolean flipDiagonally = (tileId & 0x20000000) != 0;

                    AffineTransform originalTransform = g2.getTransform();

                    g2.translate(tileWorldX - cameraPosition.x + scaledTileSize / 2.0,
                            tileWorldY - cameraPosition.y + scaledTileSize / 2.0);

                    if (flipDiagonally) {
                        g2.rotate(Math.PI / 2); // Rotate 90 degrees
                    }
                    if (flipHorizontally) {
                        g2.scale(-1, 1); // Flip horizontally
                    }
                    if (flipVertically) {
                        g2.scale(1, -1); // Flip vertically
                    }

                    int tileCol = ((tileId & 0x1FFFFFFF) - 1) % (tileSetImage.getWidth() / tileSetTileSize);
                    int tileRow = ((tileId & 0x1FFFFFFF) - 1 - tilesetOffset.get(tileSetImage)) / (tileSetImage.getWidth() / tileSetTileSize);

                    g2.drawImage(tileSetImage,
                            -scaledTileSize / 2, -scaledTileSize / 2,
                            scaledTileSize / 2, scaledTileSize / 2,
                            tileCol * tileSetTileSize, tileRow * tileSetTileSize,
                            (tileCol + 1) * tileSetTileSize, (tileRow + 1) * tileSetTileSize, null);

                    g2.setTransform(originalTransform);
                }
            }
        }
    }

    public static int getScaledTileSize() {
        return (int) (tileSetTileSize * scale);
    }
}