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

    public class TiledMap {

        private ArrayList<BufferedImage> tileSets;
        private String mapPath;
        private JSONParser parser;
        private int[][] baseLayerTiles;
        private int mapWidth, mapHeight, tileSetTileSize;
        private JSONArray roomData;

        private final double scale = GamePanel.scale;

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
                JSONObject baseLayer = (JSONObject) layers.get(3);
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

//            // Check if the player is inside a wall
//            if (playerTileX < 0 || playerTileX >= mapWidth || playerTileY < 0 || playerTileY >= mapHeight ||
//                ((Long) roomData.get(playerTileY * mapWidth + playerTileX)).intValue() == 0) {
//                // Player is in an invalid position; keep the camera centered on the room
//                return;
//            }

            int tileId = 1;

            int minX = playerTileX, maxX = playerTileX;
            int minY = playerTileY, maxY = playerTileY;

            while (minX >= 0 && ((Long) roomData.get(playerTileY * mapWidth + (minX - 1))).intValue() != tileId) {
                minX--;
            }
            while (maxX < mapWidth - 1 && ((Long) roomData.get(playerTileY * mapWidth + (maxX + 1))).intValue() != tileId) {
                maxX++;
            }

            while (minY >= 0 && ((Long) roomData.get((minY - 1) * mapWidth + playerTileX)).intValue() != tileId) {
                minY--;
            }
            while (maxY < mapHeight - 1 && ((Long) roomData.get((maxY + 1) * mapWidth + playerTileX)).intValue() != tileId) {
                maxY++;
            }

            // Update room dimensions
            roomWidth = (maxX - minX + 1) * scaledTileSize;
            roomHeight = (maxY - minY + 1) * scaledTileSize;

            // Calculate target room position to center the room
            Vector2 targetRoomPosition = new Vector2(
                    (minX * scaledTileSize) - (GamePanel.screenWidth / 2 - (double) roomWidth / 2),
                    (minY * scaledTileSize) - (GamePanel.screenHeight / 2 - (double) roomHeight / 2)
            );

            // Smoothly interpolate the room position
            double lerpFactor = 0.1; // Adjust for smoother or faster transitions
            roomPosition.x += (targetRoomPosition.x - roomPosition.x) * lerpFactor;
            roomPosition.y += (targetRoomPosition.y - roomPosition.y) * lerpFactor;
        }

        public void drawMap(Graphics2D g2, Player player) {
            int scaledTileSize = (int) (tileSetTileSize * scale);

            // Calculate camera position to center on the player
            double cameraX = player.position.x - GamePanel.screenWidth / 2;
            double cameraY = player.position.y - GamePanel.screenHeight / 2;

            // Constrain camera within room boundaries
            cameraX = Math.max(roomPosition.x, Math.min(cameraX, roomPosition.x + roomWidth - GamePanel.screenWidth));
            cameraY = Math.max(roomPosition.y, Math.min(cameraY, roomPosition.y + roomHeight - GamePanel.screenHeight));

            for (int i = 0; i < mapHeight; i++) {
                for (int j = 0; j < mapWidth; j++) {
                    int tileId = baseLayerTiles[i][j];
                    if (tileId == 0) continue; // Skip empty tiles

                    int tileWorldX = j * scaledTileSize;
                    int tileWorldY = i * scaledTileSize;

                    // Check if the tile is within the camera's view
                    if (tileWorldX + scaledTileSize > cameraX &&
                        tileWorldX < cameraX + GamePanel.screenWidth &&
                        tileWorldY + scaledTileSize > cameraY &&
                        tileWorldY < cameraY + GamePanel.screenHeight) {

                        BufferedImage tileSetImage = tileSets.get(0);
                        int tileCol = (tileId - 1) % (tileSetImage.getWidth() / tileSetTileSize);
                        int tileRow = (tileId - 1) / (tileSetImage.getWidth() / tileSetTileSize);

                        // Draw the tile
                        g2.drawImage(tileSetImage,
                                (int) (tileWorldX - cameraX),
                                (int) (tileWorldY - cameraY),
                                (int) (tileWorldX - cameraX + scaledTileSize),
                                (int) (tileWorldY - cameraY + scaledTileSize),
                                tileCol * tileSetTileSize, tileRow * tileSetTileSize,
                                (tileCol + 1) * tileSetTileSize, (tileRow + 1) * tileSetTileSize, null);
                    }
                }
            }
        }
    }