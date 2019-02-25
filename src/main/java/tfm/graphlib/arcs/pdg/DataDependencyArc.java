package tfm.graphlib.arcs.pdg;

import tfm.graphlib.arcs.Arc;
import tfm.graphlib.arcs.data.VariableArcData;
import tfm.graphlib.nodes.Vertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataDependencyArc extends Arc<VariableArcData> {

    public DataDependencyArc(Vertex from, Vertex to, String variable, String... variables) {
        super(from, to);

        List<String> variablesList = new ArrayList<>(variables.length + 1);

        variablesList.add(variable);
        variablesList.addAll(Arrays.asList(variables));

        VariableArcData variableArcData = new VariableArcData(variablesList);

        setData(variableArcData);
    }

    @Override
    public boolean isControlFlowArrow() {
        return false;
    }

    @Override
    public boolean isControlDependencyArrow() {
        return false;
    }

    @Override
    public boolean isDataDependencyArrow() {
        return true;
    }

    @Override
    public String toGraphvizRepresentation() {
        return String.format("%s [label=\"%s\"]", super.toString(), getData().toString());
    }
}

