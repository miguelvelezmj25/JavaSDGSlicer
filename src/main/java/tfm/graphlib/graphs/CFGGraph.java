package tfm.graphlib.graphs;

import com.github.javaparser.ast.Node;
import edg.graphlib.Arrow;
import tfm.graphlib.arcs.cfg.ControlFlowArc;
import tfm.graphlib.arcs.data.ArcData;
import tfm.graphlib.arcs.data.VoidArcData;
import tfm.graphlib.nodes.CFGVertex;

public abstract class CFGGraph extends Graph<CFGVertex> {

    public CFGGraph() {
        super();
        setRootVertex(new CFGVertex(VertexId.getVertexId(), getRootNodeData()));
    }

    @Override
    public CFGVertex addVertex(String instruction) {
        CFGVertex vertex = new CFGVertex(VertexId.getVertexId(), instruction);
        this.addVertex(vertex);

        return vertex;
    }

    protected abstract String getRootNodeData();

    @SuppressWarnings("unchecked")
    public void addControlFlowEdge(CFGVertex from, CFGVertex to) {
        super.addEdge((Arrow) new ControlFlowArc(from, to));
    }
}
