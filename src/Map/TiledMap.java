package Map;

    import java.awt.*;
    import java.awt.image.BufferedImage;
    import java.io.FileReader;
    import java.io.InputStream;
    import java.util.ArrayList;

    import Entitys.Player;
    import Handlers.ImageHandler;
    import org.json.simple.JSONArray;
    import org.json.simple.JSONObject;
    import org.json.simple.parser.JSONParser;

    public class TiledMap {

        private ArrayList<BufferedImage> tileSets;
        private String mapPath;
        private JSONParser parser;
        private int[][] baseLayerTiles;
        private int mapWidth, mapHeight, tileSetTileSize;

        public TiledMap() {
            this.mapPath = "src/Assets/Map/forsakenMap.json";
            this.parser = new JSONParser();
            this.tileSets = new ArrayList<>();

            tileSets.add(ImageHandler.loadImage("Assets/Images/Tilesets/Map/pixil-frame-0.png"));

            loadMap();
        }

        private void loadMap() {
            try (FileReader reader = new FileReader(mapPath)) {
                JSONObject mapData = (JSONObject) parser.parse(reader);
                mapWidth = ((Long) mapData.get("width")).intValue();
                mapHeight = ((Long) mapData.get("height")).intValue();
                tileSetTileSize = ((Long) mapData.get("tilewidth")).intValue();

                JSONArray layers = (JSONArray) mapData.get("layers");
                JSONObject baseLayer = (JSONObject) layers.get(0); // Assuming the first layer is the base layer
                JSONArray data = (JSONArray) baseLayer.get("data");

                baseLayerTiles = new int[mapHeight][mapWidth];
                for (int i = 0; i < mapHeight; i++) {
                    for (int j = 0; j < mapWidth; j++) {
                        baseLayerTiles[i][j] = ((Long) data.get(i * mapWidth + j)).intValue();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void drawMap(Graphics2D g2, Player player) {
            for (int i = 0; i < mapHeight; i++) {
                for (int j = 0; j < mapWidth; j++) {
                    int tileId = baseLayerTiles[i][j];
                    if (tileId == 0) continue; // Skip empty tiles

                    int tileWorldX = j * tileSetTileSize;
                    int tileWorldY = i * tileSetTileSize;

                    if (tileWorldX + tileSetTileSize > player.position.x - player.screenPosition.x && tileWorldX - tileSetTileSize < player.position.x + player.screenPosition.x &&
                            tileWorldY + tileSetTileSize > player.position.y - player.screenPosition.y && tileWorldY - tileSetTileSize < player.position.x + player.screenPosition.y) {
                        BufferedImage tileSetImage = tileSets.get(0); // Assuming a single tileset for simplicity
                        int tileCol = (tileId - 1) % (tileSetImage.getWidth() / tileSetTileSize);
                        int tileRow = (tileId - 1) / (tileSetImage.getWidth() / tileSetTileSize);

                        g2.drawImage(tileSetImage, (int) (tileWorldX - player.position.x + player.screenPosition.x), (int) (tileWorldY - player.position.y + player.screenPosition.y),
                                (int) (tileWorldX - player.position.x + player.screenPosition.x + tileSetTileSize), (int) (tileWorldY - player.position.y + player.screenPosition.y + tileSetTileSize),
                                tileCol * tileSetTileSize, tileRow * tileSetTileSize, (tileCol + 1) * tileSetTileSize, (tileRow + 1) * tileSetTileSize, null);
                    }
                }
            }
        }
    }