package Handlers;

import Main.Panels.GamePanel;
import Map.TiledMap;
import java.util.*;
public class Pathfinding {

    private static class Node {
        int x, y;
        int gCost, hCost;
        Node parent;
        Node(int x, int y) {
            this.x = x;
            this.y = y;
        }
        int fCost() {
            return gCost + hCost;
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node)) return false;
            Node oN = (Node)o;
            return x == oN.x && y == oN.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    /**
     * Finds a path from startPos to targetPos
     * @param startPos pixel coordinates of start
     * @param targetPos pixel coordinates of goal
     * @return list of Vector2 waypoints, or null if no path exists
     */
    public static List<Vector2> findPath(Vector2 startPos, Vector2 targetPos) {
        TiledMap map = GamePanel.tileMap;
        int tileSize = map.getScaledTileSize();

        // Convert pixel coordinates to grid coordinates
        Node start = new Node((int)(startPos.x / tileSize), (int)(startPos.y / tileSize));
        Node goal = new Node((int)(targetPos.x / tileSize), (int)(targetPos.y / tileSize));

        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(Node::fCost));
        Set<Node> closedSet = new HashSet<>();

        start.gCost = 0;
        start.hCost = heuristic(start, goal);
        openSet.add(start);

        while(!openSet.isEmpty()) {
            Node current = openSet.poll();
            if (current.equals(goal)) {
                return buildPath(current, tileSize);
            }
            closedSet.add(current);

            for (Node neighbour : getNeighbours(current, map)) {
                if (closedSet.contains(neighbour)) continue;
                int tempG = current.gCost + 1; // cost between adjacent nodes = 1

                boolean inOpen = openSet.contains(neighbour);
                if (!inOpen || tempG < neighbour.gCost) {
                    neighbour.parent = current;
                    neighbour.gCost = tempG;
                    neighbour.hCost = heuristic(neighbour, goal);
                    if(!inOpen) openSet.add(neighbour);
                }
            }
        }

        // no path found
        return null;
    }

    /**
     * Determines Manhattan distance through heuristic estimate
     * @param a the starting node
     * @param b the goal node
     * @return the estimated cost (Manhattan distance) from node a to node b
     */
    private static int heuristic(Node a, Node b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    /**
     * Retrieves adjacent, walkable neighbours (up, down, left, right)
     * @param n   the current node whose neighbours are being retrieved
     * @param map the TiledMap used to check which grid cells are walkable
     * @return a List of Node objects representing each walkable neighbour in grid coordinates adjacent to the given node
     */
    private static List<Node> getNeighbours(Node n, TiledMap map) {
        int[][] dirs = {{1,0}, {-1, 0}, {0, 1}, {0, -1}};
        List<Node> list = new ArrayList<>(4);

        for (int[] d : dirs) {
            int nx = n.x + d[0], ny = n.y + d[1];
            if (map.isWalkable(nx, ny)) {
                list.add(new Node(nx, ny));
            }
        }
        return list;
    }

    /**
     * Reconstructs the path by following parent links from goal to start and converts grid coordinates back to pixel-space Vector2 waypoints
     * @param goalNode the end node of the path
     * @param tileSize the size of one tile in pixels
     * @return a list of Vector2 waypoints (in pixel coordinates), ordered from the start position to the goal position
     */
    private static List<Vector2> buildPath(Node goalNode, int tileSize) {
        LinkedList<Vector2> path = new LinkedList<>();
        for (Node cur = goalNode; cur != null; cur = cur.parent) {
            path.addFirst(new Vector2(cur.x * tileSize, cur.y * tileSize));
        }
        return path;
    }
}
