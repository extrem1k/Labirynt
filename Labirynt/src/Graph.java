import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Graph {


    int x;
    int y;
    List<Graph> neighbors;


    public Graph(int x, int y) {
        this.x = x;
        this.y = y;
        neighbors = new ArrayList<Graph>();


    }

    public void addNeighbor(Graph neigbor) {
        if (!neighbors.contains(neigbor)) {
            neighbors.add(neigbor);
            neigbor.addNeighbor(this);

        }
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Graph graph = (Graph) o;
        return x == graph.x && y == graph.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
