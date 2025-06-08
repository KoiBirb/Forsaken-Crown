/*
 * TiledMap.java
 * Leo Bogaert
 * May 7, 2025,
 * This class reads map data from a jar file and calculates
 * the correct camera position to display the map and backgrounds.
 */

package Map;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.VolatileImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

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

    private final ArrayList<VolatileImage> tileSets;
    private ArrayList<VolatileImage[]> backgrounds;
    private final ArrayList<int[][]> mapLayers;
    private final HashMap<VolatileImage, Integer> tilesetOffset;
    private JSONArray roomData;
    public int[][] collidablesTiles;

    private final String mapPath;
    private final JSONParser parser;

    private int mapWidth, mapHeight;
    private static int tileSetTileSize;

    private int[][] roomIds;
    private int nextRoomId = 1;
    private int activeRoomId = -1;


    private final int CAMERADELAY = 10;
    private int cameraDelayCounter, oldRoomWidth, oldRoomHeight;
    private boolean roomChanged;
    private Vector2 cameraPosition;
    private static Vector2 cameraShakeOffset = new Vector2(0, 0);
    private static int shakeDuration = 0;
    private static final Random random = new Random();

    private final double SCALE = GamePanel.scale;
    private Vector2 roomScreenPos;
    private int roomWidth,roomHeight;
    private int minX, maxX, minY, maxY;

    private static final VolatileImage CastleBones = ImageHandler.loadImage("Assets/Images/Tilesets/Map/Castle of Bones Tileset.png");
    private static final VolatileImage DarkEdition = ImageHandler.loadImage("Assets/Images/Tilesets/Map/DARK Edition Tileset No background.png");
    private static final VolatileImage BloodTemple = ImageHandler.loadImage("Assets/Images/Tilesets/Map/Blood Temple Tileset.png");
    private static final VolatileImage Glow1 = ImageHandler.loadImage("Assets/Images/Tilesets/Map/pixil-frame-0 (6).png");
    private static final VolatileImage Glow2 = ImageHandler.loadImage("Assets/Images/Tilesets/Map/pixil-frame-0 (5).png");
    private static final VolatileImage Victorian = ImageHandler.loadImage("Assets/Images/Tilesets/Map/victorian tileset.png");
    private static final VolatileImage Spikes = ImageHandler.loadImage("Assets/Images/Traps/Spikes 48x16.png");

    /**
     * Constructor
     * Initializes the map path and loads the map data.
     */
    public TiledMap() {
        this.mapPath = "Assets/Map/forsakenMap.tmj";
        this.parser = new JSONParser();
        this.tileSets = new ArrayList<>();

        this.cameraDelayCounter = 0;
        this.roomChanged = false;
        this.oldRoomWidth = 0;
        this.oldRoomHeight = 0;

        this.cameraPosition = new Vector2(0, 0);

        mapLayers = new ArrayList<>();
        tilesetOffset = new HashMap<>();


        tileSets.add(CastleBones);
        tileSets.add(CastleBones);
        tileSets.add(DarkEdition);
        tileSets.add(DarkEdition);
        tileSets.add(BloodTemple);
        tileSets.add(Glow1);
        tileSets.add(Glow2);
        tileSets.add(null);
        tileSets.add(DarkEdition);
        tileSets.add(DarkEdition);
        tileSets.add(DarkEdition);
        tileSets.add(DarkEdition);
        tileSets.add(DarkEdition);
        tileSets.add(BloodTemple);
        tileSets.add(CastleBones);
        tileSets.add(CastleBones);
        tileSets.add(CastleBones);
        tileSets.add(CastleBones);
        tileSets.add(CastleBones);
        tileSets.add(Victorian);
        tileSets.add(Victorian);
        tileSets.add(BloodTemple);
        tileSets.add(BloodTemple);
        tileSets.add(BloodTemple);
        tileSets.add(Spikes);
        tileSets.add(Spikes);

        loadMap();
        loadBackgrounds();
    }

    /**
     * Assigns unique room IDs to each room in the map.
     */
    private void assignRoomIds() {
        roomIds = new int[mapHeight][mapWidth];
        boolean[][] visited = new boolean[mapHeight][mapWidth];

        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                int tile = ((Long) roomData.get(y * mapWidth + x)).intValue();
                if (tile == 0 && !visited[y][x]) {
                    floodFillRoom(x, y, nextRoomId++, visited);
                }
            }
        }
    }

    /**
     * Flood fill to assign room IDs.
     * @param startX Starting x coordinate
     * @param startY Starting y coordinate
     * @param roomId ID to assign to the room
     * @param visited 2D array to track visited tiles
     */
    private void floodFillRoom(int startX, int startY, int roomId, boolean[][] visited) {
        int[] dx = {0, 1, 0, -1};
        int[] dy = {-1, 0, 1, 0};
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startX, startY});
        visited[startY][startX] = true;
        roomIds[startY][startX] = roomId;

        while (!queue.isEmpty()) {
            int[] pos = queue.poll();
            int x = pos[0], y = pos[1];
            for (int d = 0; d < 4; d++) {
                int nx = x + dx[d], ny = y + dy[d];
                if (nx >= 0 && nx < mapWidth && ny >= 0 && ny < mapHeight &&
                        !visited[ny][nx] && ((Long) roomData.get(ny * mapWidth + nx)).intValue() == 0) {
                    visited[ny][nx] = true;
                    roomIds[ny][nx] = roomId;
                    queue.add(new int[]{nx, ny});
                }
            }
        }
    }

    /**
     * Loads the background images for the map.
     */
    private void loadBackgrounds(){
        backgrounds = new ArrayList<>();

        VolatileImage[] backgroundLayers = new VolatileImage[6];
        backgroundLayers[0] = ImageHandler.loadImage("Assets/Images/Backgrounds/Beneath Parallax/background.png");
        backgroundLayers[1] = ImageHandler.loadImage("Assets/Images/Backgrounds/Beneath Parallax/pillars.png");
        backgroundLayers[2] = ImageHandler.loadImage("Assets/Images/Backgrounds/Beneath Parallax/bridge.png");
        backgroundLayers[3] = ImageHandler.loadImage("Assets/Images/Backgrounds/Beneath Parallax/cages.png");
        backgroundLayers[4] = ImageHandler.loadImage("Assets/Images/Backgrounds/Beneath Parallax/fog.png");
        backgroundLayers[5] = ImageHandler.loadImage("Assets/Images/Backgrounds/Beneath Parallax/fogin front.png");
        backgrounds.add(backgroundLayers);

        backgroundLayers = new VolatileImage[3];
        backgroundLayers[0] = ImageHandler.loadImage("Assets/Images/Backgrounds/Beneath Parallax/background.png");
        backgroundLayers[1] = ImageHandler.loadImage("Images/Backgrounds/Caves Parallax/layer 6.png");
        backgroundLayers[2] = ImageHandler.loadImage("Images/Backgrounds/Caves Parallax/layer 7.png");
        backgrounds.add(backgroundLayers);

        backgroundLayers = new VolatileImage[3];
        backgroundLayers[0] = ImageHandler.loadImage("Assets/Images/Backgrounds/The Columns/background.png");
        backgroundLayers[1] = ImageHandler.loadImage("Images/Backgrounds/The Columns/layer 3.png");
        backgroundLayers[2] = ImageHandler.loadImage("Images/Backgrounds/The Columns/layer 4.png");
        backgrounds.add(backgroundLayers);


        backgroundLayers = new VolatileImage[5];
        backgroundLayers[0] = ImageHandler.loadImage("Images/Backgrounds/Boss Parallax/1.png");
        backgroundLayers[1] = ImageHandler.loadImage("Images/Backgrounds/Boss Parallax/2.png");
        backgroundLayers[2] = ImageHandler.loadImage("Images/Backgrounds/Boss Parallax/3.png");
        backgroundLayers[3] = ImageHandler.loadImage("Images/Backgrounds/Boss Parallax/4.png");
        backgroundLayers[4] = ImageHandler.loadImage("Images/Backgrounds/Boss Parallax/5.png");
        backgrounds.add(backgroundLayers);

        backgroundLayers = new VolatileImage[3];
        backgroundLayers[0] = ImageHandler.loadImage("Images/Backgrounds/Boss Parallax/1.png");
        backgroundLayers[1] = ImageHandler.loadImage("Images/Backgrounds/Boss Parallax/2.png");
        backgroundLayers[2] = ImageHandler.loadImage("Images/Backgrounds/Boss Parallax/3.png");
        backgrounds.add(backgroundLayers);
    }



    /**
     * Reads data from a JSON file and stores it in JSON arrays
     */
    private void loadMap() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(mapPath)) {
            if (inputStream == null) {
                throw new FileNotFoundException("Resource not found: " + mapPath);
            }

            tilesetOffset.put(CastleBones, 504);
            tilesetOffset.put(DarkEdition, 36);
            tilesetOffset.put(BloodTemple, 1624);
            tilesetOffset.put(Glow1, 1988);
            tilesetOffset.put(Glow2, 1907);
            tilesetOffset.put(Victorian, 324);
            tilesetOffset.put(Spikes, 1880);

            JSONObject mapData = (JSONObject) parser.parse(new InputStreamReader(inputStream));
            mapWidth = ((Long) mapData.get("width")).intValue();
            mapHeight = ((Long) mapData.get("height")).intValue();
            tileSetTileSize = ((Long) mapData.get("tilewidth")).intValue();

            JSONArray layers = (JSONArray) mapData.get("layers");
            JSONObject layer;
            JSONArray data;

            // tile layers
            for (int x = 0; x < 26; x++) {
                layer = (JSONObject) layers.get(x);
                data = (JSONArray) layer.get("data");

                mapLayers.add(new int[mapHeight][mapWidth]);
                for (int i = 0; i < mapHeight; i++) {
                    for (int j = 0; j < mapWidth; j++) {
                        mapLayers.get(x)[i][j] = ((Long) data.get(i * mapWidth + j)).intValue();
                    }
                }
            }

            // Room positions
            JSONObject roomLayer = (JSONObject) layers.get(26);
            roomData = (JSONArray) roomLayer.get("data");

            minX = mapWidth; minY = mapHeight;
            maxX = 0; maxY = 0;

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

            JSONObject collidables = (JSONObject) layers.get(27);
            JSONArray collidablesData = (JSONArray) collidables.get("data");

            collidablesTiles = new int[mapHeight][mapWidth];
            for (int i = 0; i < mapHeight; i++) {
                for (int j = 0; j < mapWidth; j++) {
                    collidablesTiles[i][j] = ((Long) collidablesData.get(i * mapWidth + j)).intValue();
                }
            }

            CollisionHandler.setCollidableTiles(collidablesTiles);

            roomScreenPos = new Vector2(minX * tileSetTileSize, minY * tileSetTileSize);
            roomWidth = (maxX - minX + 1) * tileSetTileSize;
            roomHeight = (maxY - minY + 1) * tileSetTileSize;

            assignRoomIds();

        } catch (FileNotFoundException e) {
            System.out.println("Error finding map file: " + e.getMessage());
        } catch (IOException e){
            System.out.println("Error reading map file: " + e.getMessage());
        } catch (ParseException e) {
            System.out.println("Error parsing map file: " + e.getMessage());
        }
    }

    /**
     * Applies a camera shake effect.
     * @param intensity The maximum offset for the shake.
     * @param duration The duration of the shake in frames.
     */
    public static void cameraShake(int intensity, int duration) {
        if (shakeDuration == 0) {
            shakeDuration = duration;
            cameraShakeOffset = new Vector2(
                    random.nextInt(intensity * 2 + 1) - intensity,
                    random.nextInt(intensity * 2 + 1) - intensity
            );
        }
    }


    /**
     * Finds room dimensions, and updates camera target position
     */
    public void update() {
        if (shakeDuration > 0) {
            shakeDuration--;
        } else {
            cameraShakeOffset = new Vector2(0, 0);
        }

        int scaledTileSize = getScaledTileSize();
        double playerX = player.getPosition().x;
        double playerY = player.getPosition().y + scaledTileSize;
        int playerTileX = (int) (playerX / scaledTileSize);
        int playerTileY = (int) (playerY / scaledTileSize);

        if (playerTileX < 0 || playerTileX >= mapWidth || playerTileY < 0 || playerTileY >= mapHeight ||
                ((Long) roomData.get(playerTileY * mapWidth + playerTileX)).intValue() == 1) {
            return;
        }

        int[] roomBounds = findRoomBounds(playerTileX, playerTileY);
        int newMinX = roomBounds[0], newMaxX = roomBounds[1], newMinY = roomBounds[2], newMaxY = roomBounds[3];
        int newRoomWidth = (newMaxX - newMinX + 1) * scaledTileSize;
        int newRoomHeight = (newMaxY - newMinY + 1) * scaledTileSize;

        if (player.isOnGround() && (roomWidth != newRoomWidth || roomHeight != newRoomHeight)) {
            roomWidth = newRoomWidth;
            roomHeight = newRoomHeight;
            GamePanel.roomTransition();
            roomChanged = true;
            cameraDelayCounter = CAMERADELAY;
        }

        double targetRoomX = (newMinX * scaledTileSize) - (GamePanel.screenWidth / 2.0 - roomWidth / 2.0);
        double targetRoomY = (newMinY * scaledTileSize) - (GamePanel.screenHeight / 2.0 - roomHeight / 2.0);

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
            roomScreenPos.x = roomScreenPos.x * (1 - movementFactor) + targetRoomX * movementFactor;
            roomScreenPos.y = roomScreenPos.y * (1 - movementFactor) + targetRoomY * movementFactor;

            if (Math.abs(roomScreenPos.x - targetRoomX) < 1 && Math.abs(roomScreenPos.y - targetRoomY) < 1) {
                roomChanged = false;
            }
        }

        cameraPosition = getCameraPos();
        cameraPosition.add(cameraShakeOffset);
    }

    /**
     * Helper to find room boundaries for a given tile position.
     */
    private int[] findRoomBounds(int tileX, int tileY) {
        int newMinX = tileX, newMaxX = tileX, newMinY = tileY, newMaxY = tileY;
        while (newMinX > 0 && ((Long) roomData.get(tileY * mapWidth + (newMinX - 1))).intValue() != 1) newMinX--;
        while (newMaxX < mapWidth - 1 && ((Long) roomData.get(tileY * mapWidth + (newMaxX + 1))).intValue() != 1) newMaxX++;
        while (newMinY > 0 && ((Long) roomData.get((newMinY - 1) * mapWidth + tileX)).intValue() != 1) newMinY--;
        while (newMaxY < mapHeight - 1 && ((Long) roomData.get((newMaxY + 1) * mapWidth + tileX)).intValue() != 1) newMaxY++;
        return new int[]{newMinX, newMaxX, newMinY, newMaxY};
    }

    /**
     * Calculates the correct camera position based on the player's position in a room
     * @return Vector2 camera screen position x,y
     */
    public Vector2 getCameraPos() {

        double cameraX = player.getPosition().x - GamePanel.screenWidth / 2;
        double cameraY = player.getPosition().y - GamePanel.screenHeight / 2;

        int cameraMargin = 400;

        if (roomWidth > GamePanel.screenWidth) {
            cameraX = (cameraX < roomScreenPos.x) ? Math.max(roomScreenPos.x - cameraMargin, cameraX) :
                    Math.min(cameraX, roomScreenPos.x + roomWidth - cameraMargin * 3.88);
        } else {
            cameraX = roomScreenPos.x;
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
    private void drawParallaxBackground(Graphics2D g2, VolatileImage[] layers, double[] parallaxFactors) {
        final int scaledTileSize = getScaledTileSize();
        Vector2 roomScreenPos = getScreenRoomPos();

        final double playerTileX = player.getPosition().x / scaledTileSize;
        final double playerTileY = player.getPosition().y / scaledTileSize;
        final double playerDistanceX = playerTileX - minX;
        final double playerDistanceY = playerTileY - minY;

        for (int i = 0; i < layers.length; i++) {
            VolatileImage layer = layers[i];
            double parallaxFactor = parallaxFactors[i];

            double offsetX = roomScreenPos.x - 2 * (playerDistanceX * parallaxFactor);
            double offsetY = roomScreenPos.y - (playerDistanceY * parallaxFactor);

            g2.drawImage(layer, (int) offsetX - scaledTileSize - 2, (int) offsetY - 2 - scaledTileSize,
                    oldRoomWidth + roomWidth / 16 + scaledTileSize,
                    oldRoomHeight + roomHeight / 16 + 2*scaledTileSize, null);
        }
    }


    /**
     * Covers the screen with a black rectangle
     * @param g2 Graphics2D object
     */
    public void coverScreen(Graphics2D g2) {

        Vector2 roomScreenPos = getScreenRoomPos();

        g2.setColor(GamePanel.backgroundColor);

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

        if (activeRoomId == 4)
            drawParallaxBackground(g2, backgrounds.get(1), new double[]{0.1, 0.4, 0.6});
        else if (activeRoomId == 1 || activeRoomId == 2 || activeRoomId == 3)
            drawParallaxBackground(g2, backgrounds.get(0), new double[]{0.2, 0.3, 0.4, 0.5, 0.6, 0.7});
        else if (activeRoomId == 5)
            drawParallaxBackground(g2, backgrounds.get(2), new double[]{0.3, 0.5, 0.6});
        else if (activeRoomId == 19)
            drawParallaxBackground(g2, backgrounds.get(3), new double[]{0.3,0.4, 0.5, 0.6,0.7});
        else if (activeRoomId == 17)
            drawParallaxBackground(g2, backgrounds.get(4), new double[]{0.3, 0.4, 0.5});


        int startX = Math.max(minX - 1, (int) (cameraPosition.x / scaledTileSize) - 1);
        int endX = Math.min(maxX + 1, (int) ((cameraPosition.x + screenWidth) / scaledTileSize) + 1);
        int startY = Math.max(minY - 1, (int) (cameraPosition.y / scaledTileSize) - 1);
        int endY = Math.min(maxY + 1, (int) ((cameraPosition.y + screenHeight) / scaledTileSize) + 1);

        int halfScaledTileSize = scaledTileSize / 2;
        for (int k = 0; k < 26; k++) {


            if (k == 7) k++;

            VolatileImage tileSetImage = tileSets.get(k);
            int tilesPerRow = tileSetImage.getWidth() / tileSetTileSize;
            int offset = tilesetOffset.get(tileSetImage);
            int[][] layer = mapLayers.get(k);

            for (int i = startY; i <= endY; i++) {
                if (i < 0 || i >= mapHeight) continue;
                int[] row = layer[i];
                for (int j = startX; j <= endX; j++) {
                    if (j < 0 || j >= mapWidth) continue;

                    int tileId = row[j];
                    if (tileId == 0) continue;

                    boolean flipHorizontally = (tileId & 0x80000000) != 0;
                    boolean flipVertically = (tileId & 0x40000000) != 0;
                    boolean flipDiagonally = (tileId & 0x20000000) != 0;

                    int tileIndex = (tileId & 0x1FFFFFFF) - 1 - offset;
                    if (tileIndex < 0) continue;

                    int tileCol = tileIndex % tilesPerRow;
                    int tileRow = tileIndex / tilesPerRow;

                    int tileWorldX = j * scaledTileSize;
                    int tileWorldY = i * scaledTileSize;

                    AffineTransform originalTransform = g2.getTransform();
                    g2.translate(tileWorldX - cameraPosition.x + scaledTileSize / 2.0,
                            tileWorldY - cameraPosition.y + scaledTileSize / 2.0);

                    if (flipDiagonally) {
                        g2.rotate(Math.PI / 2);
                        if (flipHorizontally && flipVertically) {
                            g2.scale(-1, 1);
                        } else if (flipHorizontally) {
                            g2.scale(1, 1);
                        } else if (flipVertically) {
                            g2.scale(-1, -1);
                        } else {
                            g2.scale(1, -1);
                        }
                    } else {
                        if (flipHorizontally && flipVertically) {
                            g2.scale(-1, -1);
                        } else if (flipHorizontally) {
                            g2.scale(-1, 1);
                        } else if (flipVertically) {
                            g2.scale(1, -1);
                        }
                    }

                    g2.drawImage(tileSetImage,
                            -halfScaledTileSize, -halfScaledTileSize,
                            halfScaledTileSize, halfScaledTileSize,
                            tileCol * tileSetTileSize, tileRow * tileSetTileSize,
                            (tileCol + 1) * tileSetTileSize, (tileRow + 1) * tileSetTileSize, null);

                    g2.setTransform(originalTransform);
                }
            }
        }
//        drawRoomIdDebug(g2);
    }

    /**
     * Updates the player's current room
     */
    public void updatePlayerRoom() {
        int scaledTileSize = getScaledTileSize();
        double playerX = player.getPosition().x;
        double playerY = player.getPosition().y + scaledTileSize;
        int playerTileX = (int) (playerX / scaledTileSize);
        int playerTileY = (int) (playerY / scaledTileSize);

        if (playerTileX < 0 || playerTileX >= mapWidth || playerTileY < 0 || playerTileY >= mapHeight) return;
        if (roomIds == null) return;

        activeRoomId = roomIds[playerTileY][playerTileX];
    }

    /**
     * Returns the current room ID of the player
     * @return int value of current room ID
     */
    public static int getPlayerRoomId(){
        TiledMap map = GamePanel.tileMap;
        return map.activeRoomId;
    }

    /**
     * Returns the room ID for the specified coordinates.
     * @param x X coordinate
     * @param y Y coordinate
     * @return int value of room ID, or -1 if out of bounds or no room data
     */
    public int getRoomId(double x, double y) {

        int scaledTileSize = getScaledTileSize();
        int tileX = (int) ((x + 5) / scaledTileSize);
        int tileY = (int) ((y + 5) / scaledTileSize);

        if (tileX < 0 || tileX >= mapWidth || tileY < 0 || tileY >= mapHeight) return -1;
        if (roomIds == null) return -1;

        int roomId = roomIds[tileY][tileX];

        if (roomId == 9 || roomId == 11)
            return 10;
        else
            return roomIds[tileY][tileX];
    }

    /**
     * Determines the size of one tile
     * @return int value, size of one tile in pixels
     */
    public static int getScaledTileSize() {
        return (int) (tileSetTileSize * scale);
    }

    /**
     * Returns the camera position
     * @return Vector2 camera position
     */
    public Vector2 returnCameraPos() {
        return cameraPosition;
    }

    /**
     * Draws the room IDs for debugging purposes.
     * Each room is filled with a color based on its ID.
     * @param g2 Graphics2D object
     */
    public void drawRoomIdDebug(Graphics2D g2) {
        if (roomIds == null) return;
        int scaledTileSize = getScaledTileSize();
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                int roomId = roomIds[y][x];
                if (roomId > 0) {
                    // Generate a color based on roomId
                    g2.setColor(Color.getHSBColor((roomId * 37) % 360 / 360f, 0.5f, 1f));
                    int drawX = x * scaledTileSize - (int) cameraPosition.x;
                    int drawY = y * scaledTileSize - (int) cameraPosition.y;
                    g2.fillRect(drawX, drawY, scaledTileSize, scaledTileSize);
                }
            }
        }
    }
}