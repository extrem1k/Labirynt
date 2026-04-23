public class Edge implements Comparable<Edge> {
    Graph graph1, graph2;
    double weight;

    Edge(Graph g1, Graph g2) {
        this.graph1 = g1;
        this.graph2 = g2;
        this.weight = Math.random(); // Random weight for shuffling
    }

    @Override
    public int compareTo(Edge other) {
        return Double.compare(this.weight, other.weight);
    }
}
