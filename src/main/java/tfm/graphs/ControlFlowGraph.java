package tfm.graphs;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.EmptyStmt;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.function.Supplier;

public class ControlFlowGraph extends DefaultDirectedGraph<DepVertex, DefaultEdge> {
    private DepVertex entryVertex, exitVertex;

    public ControlFlowGraph(Class<? extends DefaultEdge> edgeClass) {
        super(edgeClass);
    }

    public ControlFlowGraph(Supplier<DepVertex> vertexSupplier, Supplier<DefaultEdge> edgeSupplier) {
        super(vertexSupplier, edgeSupplier, false);
    }

    public DepVertex getEntryVertex() {
        if (entryVertex == null)
            entryVertex = new DepVertex(new EmptyStmt(), "Entry", 0);
        return entryVertex;
    }

    public DepVertex getExitVertex() {
        if (exitVertex == null)
            exitVertex = new DepVertex(new EmptyStmt(), "Exit", 0);
        return exitVertex;
    }

    /** @throws java.util.NoSuchElementException If the element does not exist. */
    public DepVertex findNodeByAst(Node node) {
        return vertexSet().stream()
                .filter(v -> v.getAstNode() == node)
                .findFirst().get();
    }
}
