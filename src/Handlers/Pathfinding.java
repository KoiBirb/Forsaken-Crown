package Handlers;

import Map.TiledMap;
import java.util.*;

public class Pathfinding {
    // Node used in A* search
    private static class Node {
        Vector2 pos; // Position in tile coordinates
        double g, h; // g = cost from start, h = heuristic cost to goal
        Node parent; // Link to previous node for path reconstruction

        Node(Vector2 pos, double g, double h, Node parent) {
            this.pos = pos;
            this.g = g;
            this.h = h;
            this.parent = parent;
        }

        /**
         * get f cost
         * @return double value of sum of g and h
         */
        double f() {
            return g + h;
        }
    }

    // A* pathfinding from start to goal on tile map
    public static List<Vector2> aStarPath(TiledMap map, Vector2 start, Vector2 goal) {
        int ts = TiledMap.getScaledTileSize();

        // Convert from pixel to tile coordinates
        int sx = (int)(start.x / ts);
        int sy = (int)(start.y / ts);
        int gx = (int)(goal.x / ts);
        int gy = (int)(goal.y / ts);

        HashSet<String> visited = new HashSet<>(); // Track visited tiles
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(Node::f));

        // Add start node to open set
        open.add(new Node(new Vector2(sx, sy), 0, heuristic(sx, sy, gx, gy), null));

        while (!open.isEmpty()) {
            Node current = open.poll(); // Select node with lowest f-score
            int cx = (int) current.pos.x;
            int cy = (int) current.pos.y;

            // Check if goal reached
            if (cx == gx && cy == gy) {
                return reconstruct(current, ts); // Reconstruct path
            }

            String key = cx + "," + cy;
            if (visited.contains(key)) continue;
            visited.add(key);

            // Check four cardinal directions (no diagonals)
            for (int[] d : new int[][]{{1,0},{-1,0},{0,1},{0,-1}}) {
                int nx = cx + d[0];
                int ny = cy + d[1];

                // Skip if tile is not walkable or already visited
                if (!map.isWalkable(nx, ny)) continue;
                String neighborKey = nx + "," + ny;
                if (visited.contains(neighborKey)) continue;

                double newG = current.g + 1; // Add movement cost
                double h = heuristic(nx, ny, gx, gy); // Estimate to goal
                Node neighbor = new Node(new Vector2(nx, ny), newG, h, current);
                open.add(neighbor);
            }
        }

        return new ArrayList<>(); // no path found, return empty path
    }

    // Reconstructs the path from goal to start and converts to world pixel positions
    private static List<Vector2> reconstruct(Node node, int ts) {
        List<Vector2> path = new ArrayList<>();
        while (node != null) {
            path.add(0, new Vector2(node.pos.x * ts + ts / 2.0, node.pos.y * ts + ts / 2.0));
            node = node.parent;
        }
        return path;
    }

    // Get manhattan distance heuristic
    private static double heuristic(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }
}
