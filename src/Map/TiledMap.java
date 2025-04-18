package Map;

    import java.awt.*;
    import java.awt.image.BufferedImage;
    import java.io.FileReader;
    import java.util.ArrayList;

    import Entitys.Player;
    import Handlers.ImageHandler;
    import Handlers.Vector2;
    import Main.Panels.GamePanel;
    import org.json.simple.JSONArray;
    import org.json.simple.JSONObject;
    import org.json.simple.parser.JSONParser;

    import static Main.Panels.GamePanel.player;

public class TiledMap {

    private ArrayList<BufferedImage> tileSets;
    private String mapPath;
    private JSONParser parser;
    private int[][] baseLayerTiles;
    private int mapWidth, mapHeight, tileSetTileSize;
    private JSONArray roomData;

    private int cameraDelay = 15; // Delay in frames before the camera starts moving
    private int cameraDelayCounter = 0;
    private boolean roomChanged = false;

    private final double scale = GamePanel.scale;

    private int minX, maxX, minY, maxY;

    public TiledMap() {
        this.mapPath = "src/Assets/Map/forsakenMap.json";
        this.parser = new JSONParser();
        this.tileSets = new ArrayList<>();

        tileSets.add(ImageHandler.loadImage("Assets/Images/Tilesets/Map/pixil-frame-0.png"));

        loadMap();
    }

    public Vector2 roomPosition;
    public int roomWidth;
    public int roomHeight;

    private void loadMap() {
        try (FileReader reader = new FileReader(mapPath)) {
            JSONObject mapData = (JSONObject) parser.parse(reader);
            mapWidth = ((Long) mapData.get("width")).intValue();
            mapHeight = ((Long) mapData.get("height")).intValue();
            tileSetTileSize = ((Long) mapData.get("tilewidth")).intValue();

            JSONArray layers = (JSONArray) mapData.get("layers");
            JSONObject baseLayer = (JSONObject) layers.get(0);
            JSONArray data = (JSONArray) baseLayer.get("data");

            baseLayerTiles = new int[mapHeight][mapWidth];
            for (int i = 0; i < mapHeight; i++) {
                for (int j = 0; j < mapWidth; j++) {
                    baseLayerTiles[i][j] = ((Long) data.get(i * mapWidth + j)).intValue();
                }
            }

            JSONObject roomLayer = (JSONObject) layers.get(3);
            roomData = (JSONArray) roomLayer.get("data");

            int minX = mapWidth, minY = mapHeight, maxX = 0, maxY = 0;

            for (int i = 0; i < mapHeight; i++) {
                for (int j = 0; j < mapWidth; j++) {
                    int tileId = ((Long) roomData.get(i * mapWidth + j)).intValue();
                    if (tileId != 0) { // Non-zero tiles represent the room
                        minX = Math.min(minX, j);
                        minY = Math.min(minY, i);
                        maxX = Math.max(maxX, j);
                        maxY = Math.max(maxY, i);
                    }
                }
            }

            // Convert tile indices to world coordinates
            roomPosition = new Vector2(minX * tileSetTileSize, minY * tileSetTileSize);
            roomWidth = (maxX - minX + 1) * tileSetTileSize;
            roomHeight = (maxY - minY + 1) * tileSetTileSize;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(Player player) {
        int scaledTileSize = (int) (tileSetTileSize * scale);

        // Calculate player position in tiles
        int playerTileX = (int) (player.position.x / scaledTileSize);
        int playerTileY = (int) (player.position.y / scaledTileSize);

        int tileId = 1;

        if (playerTileX < 0 || playerTileX >= mapWidth || playerTileY < 0 || playerTileY >= mapHeight ||
                ((Long) roomData.get(playerTileY * mapWidth + playerTileX)).intValue() == tileId) {
            return;
        }

        // Temporary variables to calculate new room boundaries
        int newMinX = playerTileX, newMaxX = playerTileX;
        int newMinY = playerTileY, newMaxY = playerTileY;

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

        // Update room dimensions
        if (roomWidth != (newMaxX - newMinX + 1) * scaledTileSize ||
                roomHeight != (newMaxY - newMinY + 1) * scaledTileSize) {

            roomWidth = (newMaxX - newMinX + 1) * scaledTileSize;
            roomHeight = (newMaxY - newMinY + 1) * scaledTileSize;

            GamePanel.roomTransition();
            roomChanged = true;
            cameraDelayCounter = cameraDelay; // Start delay
        }

        // Calculate target room position to center the room
        Vector2 targetRoomPosition = new Vector2(
                (newMinX * scaledTileSize) - (GamePanel.screenWidth / 2 - (double) roomWidth / 2),
                (newMinY * scaledTileSize) - (GamePanel.screenHeight / 2 - (double) roomHeight / 2)
        );

        if (cameraDelayCounter > 0) {
            cameraDelayCounter--; // Wait for the delay to finish
        } else if (roomChanged) {
            // Update the visible room boundaries after the delay
            minX = newMinX;
            maxX = newMaxX;
            minY = newMinY;
            maxY = newMaxY;

            double lerpFactor = 0.08;
            // Ensure linear interpolation for room position
            roomPosition.x = roomPosition.x * (1 - lerpFactor) + targetRoomPosition.x * lerpFactor;
            roomPosition.y = roomPosition.y * (1 - lerpFactor) + targetRoomPosition.y * lerpFactor;

            if (Math.abs(roomPosition.x - targetRoomPosition.x) < 1 &&
                    Math.abs(roomPosition.y - targetRoomPosition.y) < 1) {
                roomChanged = false; // Stop moving the camera once it reaches the target
            }
        }
    }

    public Vector2 getCameraPos() {
        // Calculate camera position to center on the player
        double cameraX = player.position.x - GamePanel.screenWidth / 2;
        double cameraY = player.position.y - GamePanel.screenHeight / 2;

        int cameraMargin = 400;

        if (roomWidth > GamePanel.screenWidth) {
            cameraX = (cameraX < roomPosition.x) ? Math.max(roomPosition.x - cameraMargin, cameraX) :
                    Math.min(cameraX, roomPosition.x + roomWidth - cameraMargin * 3.88);
        } else {
            cameraX = roomPosition.x;
        }

        if (roomHeight > GamePanel.screenHeight) {
            cameraY = (cameraY < roomPosition.y) ? Math.max(roomPosition.y - 2 * cameraMargin, cameraY) :
                    Math.min(cameraY, roomPosition.y + roomHeight - 3 * cameraMargin);
        } else {
            cameraY = roomPosition.y;
        }

        return new Vector2(cameraX, cameraY);
    }

    public void drawMap(Graphics2D g2) {
        int scaledTileSize = (int) (tileSetTileSize * scale);

        Vector2 cameraPos = getCameraPos();

        // Only draw tiles within the current room boundaries
        for (int i = minY; i <= maxY; i++) {
            for (int j = minX; j <= maxX; j++) {
                int tileId = baseLayerTiles[i][j];

                int tileWorldX = j * scaledTileSize;
                int tileWorldY = i * scaledTileSize;

                if (tileId == 0) {
                    // Draw a white tile
                    g2.setColor(Color.WHITE);
                    g2.fillRect(
                            (int) (tileWorldX - cameraPos.x),
                            (int) (tileWorldY - cameraPos.y),
                            scaledTileSize,
                            scaledTileSize
                    );
                    continue; // Skip further processing for this tile
                }

                // Check if the tile is within the camera's view
                if (tileWorldX + scaledTileSize > cameraPos.x &&
                        tileWorldX < cameraPos.x + GamePanel.screenWidth &&
                        tileWorldY + scaledTileSize > cameraPos.y &&
                        tileWorldY < cameraPos.y + GamePanel.screenHeight) {

                    BufferedImage tileSetImage = tileSets.get(0);
                    int tileCol = (tileId - 1) % (tileSetImage.getWidth() / tileSetTileSize);
                    int tileRow = (tileId - 1) / (tileSetImage.getWidth() / tileSetTileSize);

                    // Draw the tile
                    g2.drawImage(tileSetImage,
                            (int) (tileWorldX - cameraPos.x),
                            (int) (tileWorldY - cameraPos.y),
                            (int) (tileWorldX - cameraPos.x + scaledTileSize),
                            (int) (tileWorldY - cameraPos.y + scaledTileSize),
                            tileCol * tileSetTileSize, tileRow * tileSetTileSize,
                            (tileCol + 1) * tileSetTileSize, (tileRow + 1) * tileSetTileSize, null);
                }
            }
        }
    }
}