package tfm.graphs;

import com.github.javaparser.ast.stmt.EmptyStmt;
import org.jgrapht.graph.DefaultDirectedGraph;
import tfm.nodes.GraphNode;

import java.util.NoSuchElementException;
import java.util.function.Supplier;

public class DependenceGraph extends DefaultDirectedGraph<DepVertex, DepEdge> {
    private DepVertex entryVertex;

    public DependenceGraph(Class<? extends DepEdge> edgeClass) {
        super(edgeClass);
    }

    public DependenceGraph(Supplier<DepVertex> vertexSupplier, Supplier<DepEdge> edgeSupplier) {
        super(vertexSupplier, edgeSupplier, false);
    }

    public DepVertex getEntryVertex() {
        if (entryVertex == null)
            entryVertex = new DepVertex(new EmptyStmt(), "Entry", 0);
        return entryVertex;
    }

    /**
     * Returns the Vertex with the associated id.
     * @throws NoSuchElementException When it can't be found.
     */
    public DepVertex findNodeById(int id) {
        return vertexSet().stream()
                .filter(v -> v.getId() == id)
                .findFirst().get();
    }

    public static class Program extends DependenceGraph {
        public Program(Class<? extends DepEdge> edgeClass) {
            super(edgeClass);
        }

        public Program(Supplier<DepVertex> vertexSupplier, Supplier<DepEdge> edgeSupplier) {
            super(vertexSupplier, edgeSupplier);
        }
    }

    public static class System extends DependenceGraph {
        public System(Class<? extends DepEdge> edgeClass) {
            super(edgeClass);
        }

        public System(Supplier<DepVertex> vertexSupplier, Supplier<DepEdge> edgeSupplier) {
            super(vertexSupplier, edgeSupplier);
        }
    }
}
