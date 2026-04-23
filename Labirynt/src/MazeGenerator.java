import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import java.util.List;
import java.util.Queue;


public class MazeGenerator extends JPanel implements KeyListener {

    //------------------------------------------------------------------
    private static final int cellSize = 20;
    private static final int GRID_WIDTH = 20;
    private static final int GRID_HEIGHT = 20;
    private final Graph[][] graphs;
    private Graph entryPoint;
    private Graph exitPoint;
    private List<Graph> shortestPath;
    private List<Letter> letters;
    private Graph playerPosition;
    private int collectedLetters;
    //---------------------------------------------------------------------------


    public MazeGenerator() {
        JFrame frame = new JFrame("Generator Labiryntu");
        frame.add(this);
        frame.setSize(GRID_WIDTH * cellSize + 20 + cellSize + cellSize, GRID_HEIGHT * cellSize + 40 + cellSize + cellSize);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);


        graphs = new Graph[GRID_WIDTH][GRID_HEIGHT];

        generateGraphs();
        setEntryAndExitPoints();

        // !!!!wybór algorytmu!!!!
        //generateMazeKruskal();
        // generateMazeDFS();
        // generateMazePrim();
        generateMazeBFS();


        placePlayerAndLetters();
        this.shortestPath = null;

        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();
        setPreferredSize(new Dimension(GRID_WIDTH * cellSize + 2 * cellSize, GRID_HEIGHT * cellSize + 2 * cellSize));
    }

    public static void main(String[] args) {

        MazeGenerator maze = new MazeGenerator();

    }

    private void generateGraphs() {
        for (int i = 0; i < GRID_WIDTH; i++) {
            for (int j = 0; j < GRID_HEIGHT; j++) {
                graphs[i][j] = new Graph(i, j);
            }
        }
    }

    private void setEntryAndExitPoints() {

        int entryX = 0;
        int entryY = (int) (Math.random() * GRID_HEIGHT);
        if (entryY == 0 || entryY == GRID_HEIGHT - 1) {
            entryX = (int) (Math.random() * GRID_WIDTH);
            entryY = 0;
        }
        entryPoint = graphs[entryX][entryY];


        int exitX = GRID_WIDTH - 1;
        int exitY = (int) (Math.random() * GRID_HEIGHT);
        if (exitY == 0 || exitY == GRID_HEIGHT - 1) {
            exitX = (int) (Math.random() * GRID_WIDTH);
            exitY = GRID_HEIGHT - 1;
        }
        exitPoint = graphs[exitX][exitY];
    }

    private void placePlayerAndLetters() {

        playerPosition = entryPoint;


        letters = new ArrayList<>();
        char[] letterChars = {'A', 'B', 'C', 'D'};
        Random random = new Random();

        for (char letter : letterChars) {
            while (true) {
                int x = random.nextInt(GRID_WIDTH);
                int y = random.nextInt(GRID_HEIGHT);
                Graph letterPos = graphs[x][y];

                if (letterPos != entryPoint && letterPos != exitPoint && letters.stream().noneMatch(l -> l.position == letterPos)) {
                    letters.add(new Letter(letterPos, letter));
                    break;
                }
            }
        }

        collectedLetters = 0;

    }

    public void generateMazeDFS() {
        boolean[][] visited = new boolean[GRID_WIDTH][GRID_HEIGHT];
        dfsTraversal(entryPoint, visited);
    }

    private void dfsTraversal(Graph current, boolean[][] visited) {
        int x = current.x;
        int y = current.y;
        visited[x][y] = true;


        List<int[]> shuffledDirections = Arrays.asList(new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}});
        Collections.shuffle(shuffledDirections);

        for (int[] dir : shuffledDirections) {
            int newX = x + dir[0];
            int newY = y + dir[1];


            if (newX >= 0 && newX < GRID_WIDTH &&
                    newY >= 0 && newY < GRID_HEIGHT &&
                    !visited[newX][newY]) {

                Graph neighbor = graphs[newX][newY];
                current.addNeighbor(neighbor);
                try {
                    Thread.sleep(10);
                    repaint();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                dfsTraversal(neighbor, visited);
            }
        }
    }

    public void generateMazeKruskal() {
        List<Graph> allGraphs = new ArrayList<>();
        for (Graph[] row : graphs) {
            allGraphs.addAll(Arrays.asList(row));
        }

        DisjointSet ds = new DisjointSet(allGraphs.size());


        List<Edge> edges = new ArrayList<>();
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                if (x + 1 < GRID_WIDTH)
                    edges.add(new Edge(graphs[x][y], graphs[x + 1][y]));
                if (y + 1 < GRID_HEIGHT)
                    edges.add(new Edge(graphs[x][y], graphs[x][y + 1]));
            }
        }


        Collections.shuffle(edges);

        for (Edge edge : edges) {
            Graph g1 = edge.graph1;
            Graph g2 = edge.graph2;
            int index1 = g1.x * GRID_WIDTH + g1.y;
            int index2 = g2.x * GRID_WIDTH + g2.y;

            if (!ds.connected(index1, index2)) {
                g1.addNeighbor(g2);
                ds.union(index1, index2);
            }
            try {
                Thread.sleep(10);
                repaint();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void generateMazePrim() {
        Set<Graph> mst = new HashSet<>();
        PriorityQueue<Edge> edges = new PriorityQueue<>();

        Graph start = entryPoint;
        mst.add(start);


        addEdges(start, edges, mst);

        while (!edges.isEmpty()) {
            Edge minEdge = edges.poll();
            Graph g1 = minEdge.graph1;
            Graph g2 = minEdge.graph2;

            if (!mst.contains(g2)) {
                g1.addNeighbor(g2);
                mst.add(g2);
                addEdges(g2, edges, mst);
            }
            try {
                Thread.sleep(10);
                repaint();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void addEdges(Graph graph, PriorityQueue<Edge> edges, Set<Graph> mst) {
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        for (int[] dir : directions) {
            int newX = graph.x + dir[0];
            int newY = graph.y + dir[1];

            if (newX >= 0 && newX < GRID_WIDTH &&
                    newY >= 0 && newY < GRID_HEIGHT) {
                Graph neighbor = graphs[newX][newY];
                if (!mst.contains(neighbor)) {
                    edges.add(new Edge(graph, neighbor));
                }
            }
        }
    }

    public void generateMazeBFS() {
        Queue<Graph> queue = new LinkedList<>();
        boolean[][] visited = new boolean[GRID_WIDTH][GRID_HEIGHT];
        Random random = new Random();


        Graph start = entryPoint;
        queue.add(start);
        visited[start.x][start.y] = true;

        while (!queue.isEmpty()) {

            Graph current = pickRandom(queue, random);

            List<int[]> directions = Arrays.asList(new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}});
            Collections.shuffle(directions, random);

            for (int[] dir : directions) {
                int newX = current.x + dir[0];
                int newY = current.y + dir[1];


                if (newX >= 0 && newX < GRID_WIDTH &&
                        newY >= 0 && newY < GRID_HEIGHT &&
                        !visited[newX][newY]) {

                    Graph neighbor = graphs[newX][newY];
                    current.addNeighbor(neighbor);
                    queue.add(neighbor);
                    visited[newX][newY] = true;
                }
                try {
                    Thread.sleep(10);
                    repaint();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private Graph pickRandom(Queue<Graph> queue, Random random) {
        List<Graph> list = new ArrayList<>(queue);
        Graph randomNode = list.get(random.nextInt(list.size()));
        queue.remove(randomNode);
        return randomNode;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int x = 0; x < graphs.length; x++) {
            for (int y = 0; y < graphs[x].length; y++) {
                Graph current = graphs[x][y];

                g.setColor(Color.BLACK);

                if (!hasLeftNeighbor(current) && (current != entryPoint && current != exitPoint)) {
                    g.drawLine(x * cellSize + cellSize, y * cellSize + cellSize, x * cellSize + cellSize, (y + 1) * cellSize + cellSize);
                }
                if (!hasTopNeighbor(current) && (current != entryPoint && current != exitPoint)) {
                    g.drawLine(x * cellSize + cellSize, y * cellSize + cellSize, (x + 1) * cellSize + cellSize, y * cellSize + cellSize);
                }
                if (!hasRightNeighbor(current) && (current != entryPoint && current != exitPoint)) {
                    g.drawLine((x + 1) * cellSize + cellSize, y * cellSize + cellSize, (x + 1) * cellSize + cellSize, (y + 1) * cellSize + cellSize);
                }
                if (!hasBottomNeighbor(current) && (current != entryPoint && current != exitPoint)) {
                    g.drawLine(x * cellSize + cellSize, (y + 1) * cellSize + cellSize, (x + 1) * cellSize + cellSize, (y + 1) * cellSize + cellSize);
                }

                g.setColor(Color.RED);
                if (playerPosition != null)
                    g.fillOval(playerPosition.x * cellSize + cellSize + cellSize / 4,
                            playerPosition.y * cellSize + cellSize + cellSize / 4,
                            cellSize / 2, cellSize / 2);


                g.setColor(Color.BLACK);
                g.drawString("Collected: " + collectedLetters + "/4", 8, 12);

                if (letters != null)
                    for (Letter letter : letters) {
                        if (letter.position == current) {
                            g.setColor(Color.GREEN);
                            g.drawString(String.valueOf(letter.letter),
                                    x * cellSize + cellSize + cellSize / 2,
                                    y * cellSize + cellSize + cellSize / 2);
                        }
                    }

                if (shortestPath != null && shortestPath.contains(current)) {
                    g.setColor(Color.BLUE);
                    g.fillRect(x * cellSize + cellSize / 2 + cellSize, y * cellSize + cellSize / 2 + cellSize, cellSize / 5, cellSize / 5);
                }
            }
        }
    }

    private boolean hasLeftNeighbor(Graph graph) {
        return graph.x > 0 && graphs[graph.x - 1][graph.y] != null && graphs[graph.x - 1][graph.y].neighbors.contains(graph);
    }

    private boolean hasTopNeighbor(Graph graph) {
        return graph.y > 0 && graphs[graph.x][graph.y - 1] != null && graphs[graph.x][graph.y - 1].neighbors.contains(graph);
    }

    private boolean hasRightNeighbor(Graph graph) {
        return graph.x < graphs.length - 1 && graphs[graph.x + 1][graph.y] != null && graphs[graph.x + 1][graph.y].neighbors.contains(graph);
    }

    private boolean hasBottomNeighbor(Graph graph) {
        return graph.y < graphs[graph.x].length - 1 && graphs[graph.x][graph.y + 1] != null && graphs[graph.x][graph.y + 1].neighbors.contains(graph);
    }

    private List<Graph> findShortestPath(Graph start, Graph end) {
        Queue<Graph> queue = new LinkedList<>();
        Map<Graph, Graph> parents = new HashMap<>();
        Set<Graph> visited = new HashSet<>();

        queue.offer(start);
        visited.add(start);
        parents.put(start, null);

        while (!queue.isEmpty()) {
            Graph current = queue.poll();
            if (current == end) {
                return reconstructPath(parents, current);
            }

            for (Graph neighbor : current.neighbors) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parents.put(neighbor, current);
                    queue.offer(neighbor);
                }
            }
        }

        return null;
    }

    private List<Graph> reconstructPath(Map<Graph, Graph> parents, Graph end) {
        List<Graph> path = new ArrayList<>();
        Graph current = end;
        while (current != null) {
            path.add(0, current);
            current = parents.get(current);
        }
        return path;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        Graph newPosition = playerPosition;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                if (playerPosition.y > 0 && hasTopNeighbor(playerPosition)) {
                    newPosition = graphs[playerPosition.x][playerPosition.y - 1];
                }
                break;
            case KeyEvent.VK_S:
                if (playerPosition.y < GRID_HEIGHT - 1 && hasBottomNeighbor(playerPosition)) {
                    newPosition = graphs[playerPosition.x][playerPosition.y + 1];
                }
                break;
            case KeyEvent.VK_A:
                if (playerPosition.x > 0 && hasLeftNeighbor(playerPosition)) {
                    newPosition = graphs[playerPosition.x - 1][playerPosition.y];
                }
                break;
            case KeyEvent.VK_D:
                if (playerPosition.x < GRID_WIDTH - 1 && hasRightNeighbor(playerPosition)) {
                    newPosition = graphs[playerPosition.x + 1][playerPosition.y];
                }
                break;
            case KeyEvent.VK_E:
                shortestPath = findShortestPath(entryPoint, exitPoint);
                break;
        }


        Graph finalNewPosition = newPosition;
        letters.removeIf(letter -> {
            if (letter.position == finalNewPosition) {
                collectedLetters++;
                return true;
            }
            return false;
        });

        playerPosition = newPosition;


        if (playerPosition == exitPoint && collectedLetters == 4) {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "zresetować labirynt?",
                    "Victory",
                    JOptionPane.YES_NO_OPTION
            );
            if (result == JOptionPane.YES_OPTION) {
                resetMaze();
            }
        }

        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        shortestPath = null;
        repaint();
    }

    private void resetMaze() {
        generateGraphs();
        generateMazeKruskal();
        setEntryAndExitPoints();
        placePlayerAndLetters();
        shortestPath = null;
        repaint();
    }
}
