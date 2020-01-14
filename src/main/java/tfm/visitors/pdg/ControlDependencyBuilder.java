package tfm.visitors.pdg;

import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import tfm.graphs.*;

import java.util.stream.Collectors;

public class ControlDependencyBuilder extends VoidVisitorAdapter<DepVertex> {

    private ControlFlowGraph cfg;
    private DependenceGraph.Program pdg;

    public ControlDependencyBuilder(DependenceGraph.Program pdg, ControlFlowGraph cfg) {
        this.pdg = pdg;
        this.cfg = cfg;
    }

    @Override
    public void visit(ExpressionStmt expressionStmt, DepVertex parent) {
        addNodeAndControlDependency(expressionStmt, parent);
    }

    @Override
    public void visit(IfStmt ifStmt, DepVertex parent) {
        DepVertex node = addNodeAndControlDependency(ifStmt, parent);

        ifStmt.getThenStmt().accept(this, node);

        ifStmt.getElseStmt().ifPresent(statement -> statement.accept(this, node));
    }

    @Override
    public void visit(WhileStmt whileStmt, DepVertex parent) {
        DepVertex node = addNodeAndControlDependency(whileStmt, parent);

        whileStmt.getBody().accept(this, node);
    }

    @Override
    public void visit(ForStmt forStmt, DepVertex parent) {
        String initialization = forStmt.getInitialization().stream()
                .map(com.github.javaparser.ast.Node::toString)
                .collect(Collectors.joining(","));

        String update = forStmt.getUpdate().stream()
                .map(com.github.javaparser.ast.Node::toString)
                .collect(Collectors.joining(","));

        String compare = forStmt.getCompare()
                .map(com.github.javaparser.ast.Node::toString)
                .orElse("true");


        DepVertex forNode = new DepVertex(forStmt, String.format("for (%s;%s;%s)", initialization, compare, update));
        pdg.addVertex(forNode);

        pdg.addEdge(parent, forNode);

        forStmt.getBody().accept(this, forNode);
    }

    @Override
    public void visit(ForEachStmt forEachStmt, DepVertex parent) {
        DepVertex node = addNodeAndControlDependency(forEachStmt, parent);

        forEachStmt.getBody().accept(this, node);
    }

    @Override
    public void visit(SwitchStmt switchStmt, DepVertex parent) {
        DepVertex node = addNodeAndControlDependency(switchStmt, parent);

        switchStmt.getEntries().accept(this, node);
    }

    @Override
    public void visit(SwitchEntryStmt switchEntryStmt, DepVertex parent) {
        DepVertex node = addNodeAndControlDependency(switchEntryStmt, parent);

        switchEntryStmt.getStatements().accept(this, node);
    }

    private DepVertex addNodeAndControlDependency(Statement statement, DepVertex parent) {
        DepVertex cfgNode = cfg.findNodeByAst(statement);

        DepVertex node = new DepVertex(cfgNode.getAstNode(), cfgNode.getData(), cfgNode.getId());
        pdg.addVertex(node);
        pdg.addEdge(parent, node);

        return node;
    }
}
