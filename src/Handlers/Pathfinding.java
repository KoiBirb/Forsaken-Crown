package Handlers;

import Map.TiledMap;
import java.util.*;

public class Pathfinding {

    private static class Node {
        Vector2 pos;
        double g, h;
        Node parent;

        Node(Vector2 pos, double g, double h, Node parent) {
            this.pos = pos;
            this.g = g;
            this.h = h;
            this.parent = parent;
        }

        double f() {
            return g + h;
        }
    }

    public static List<Vector2> aStarPath(TiledMap map, Vector2 start, Vector2 goal) {
        int ts = TiledMap.getScaledTileSize();
        int sx = (int)(start.x / ts);
        int sy = (int)(start.y / ts);
        int gx = (int)(goal.x / ts);
        int gy = (int)(goal.y / ts);

        HashSet<String> visited = new HashSet<>();
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(Node::f));
        open.add(new Node(new Vector2(sx, sy), 0, heuristic(sx, sy, gx, gy), null));

        while (!open.isEmpty()) {
            Node current = open.poll();
            int cx = (int) current.pos.x;
            int cy = (int) current.pos.y;

            if (cx == gx && cy == gy) {
                return reconstruct(current, ts);
            }

            String key = cx + "," + cy;
            if (visited.contains(key)) continue;
            visited.add(key);

            for (int[] d : new int[][]{{1,0},{-1,0},{0,1},{0,-1}}) {
                int nx = cx + d[0];
                int ny = cy + d[1];
                if (!map.isWalkable(nx, ny)) continue;
                String neighborKey = nx + "," + ny;
                if (visited.contains(neighborKey)) continue;

                double newG = current.g + 1;
                double h = heuristic(nx, ny, gx, gy);
                Node neighbor = new Node(new Vector2(nx, ny), newG, h, current);
                open.add(neighbor);
            }
        }

        return new ArrayList<>();
    }

    private static List<Vector2> reconstruct(Node node, int ts) {
        List<Vector2> path = new ArrayList<>();
        while (node != null) {
            path.add(0, new Vector2(node.pos.x * ts + ts / 2.0, node.pos.y * ts + ts / 2.0));
            node = node.parent;
        }
        return path;
    }

    private static double heuristic(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }
}
