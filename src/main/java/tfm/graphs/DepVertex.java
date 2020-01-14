package tfm.graphs;

import com.github.javaparser.ast.Node;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DepVertex implements Comparable<DepVertex> {
    private final Node node;
    private final int id;
    private final String text;

    public DepVertex(Node node) {
        this(node, node.toString());
    }

    public DepVertex(Node node, String text) {
        this(node, text, node.getRange().get().begin.line);
    }

    public DepVertex(Node node, int id) {
        this(node, node.toString(), id);
    }

    public DepVertex(Node node, String text, int id) {
        this.node = node;
        this.text = id + ": " + text;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Node getAstNode() {
        return node;
    }

    public String getData() {
        return text;
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DepVertex &&
                Objects.equals(text, ((DepVertex) obj).text);
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public int compareTo(@NotNull DepVertex o) {
        int res = Integer.compare(id, o.id);
        if (res != 0)
            return res;
        return text.compareTo(o.text);
    }
}
