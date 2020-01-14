package tfm.graphs;

import org.jgrapht.graph.DefaultEdge;
import tfm.arcs.data.VariableArcData;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DepEdge extends DefaultEdge {
    public static class Ctrl extends DepEdge {
        private final boolean exec;

        public Ctrl() {
            this(true);
        }

        public Ctrl(boolean exec) {
            this.exec = exec;
        }

        public boolean isExec() {
            return exec;
        }
    }

    public static class Data extends DepEdge {
        private final List<String> vars = new LinkedList<>();

        public Data(String... vars) {
            this.vars.addAll(Arrays.asList(vars));
        }

        public List<String> getVars() {
            return vars;
        }
    }
}
