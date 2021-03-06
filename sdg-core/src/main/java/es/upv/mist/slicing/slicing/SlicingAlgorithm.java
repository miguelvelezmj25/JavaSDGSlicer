package es.upv.mist.slicing.slicing;

import es.upv.mist.slicing.nodes.GraphNode;

public interface SlicingAlgorithm {
    /** Obtain the nodes reached by this algorithm in a classic 2-pass interprocedural slice. */
    Slice traverse(GraphNode<?> slicingCriterion);
    /** Obtain the nodes reached by this algorithm intraprocedurally (i.e. without traversing interprocedural arcs. */
    Slice traverseProcedure(GraphNode<?> slicingCriterion);
}
