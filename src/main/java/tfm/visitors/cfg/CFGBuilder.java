package tfm.visitors.cfg;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import tfm.graphs.ControlFlowGraph;
import tfm.graphs.DepVertex;
import tfm.utils.ASTUtils;

import java.util.*;

public class CFGBuilder extends VoidVisitorAdapter<Void> {

    private ControlFlowGraph cfg;

    private Queue<DepVertex> lastParentNodes;
    private List<DepVertex> bodyBreaks;

    public CFGBuilder(ControlFlowGraph cfg) {
        this.cfg = cfg;
        this.lastParentNodes = Collections.asLifoQueue(
                new ArrayDeque<>(
                        Collections.singletonList(cfg.getEntryVertex())
                )
        );

        this.bodyBreaks = new ArrayList<>();
    }

    @Override
    public void visit(ExpressionStmt expressionStmt, Void arg) {
        String expression = expressionStmt.toString().replace("\"", "\\\"");

        DepVertex nextNode = addNodeAndArcs(expression, expressionStmt);

        lastParentNodes.add(nextNode);
    }

    @Override
    public void visit(IfStmt ifStmt, Void arg) {
        DepVertex ifCondition = addNodeAndArcs(
                String.format("if (%s)", ifStmt.getCondition().toString()),
                ifStmt
        );

        lastParentNodes.add(ifCondition);

        // Visit "then"
        ifStmt.getThenStmt().accept(this, arg);

        Queue<DepVertex> lastThenNodes = new ArrayDeque<>(lastParentNodes);

        if (ifStmt.hasElseBranch()) {
            lastParentNodes.clear();
            lastParentNodes.add(ifCondition); // Set if nodes as root

            ifStmt.getElseStmt().get().accept(this, arg);

            lastParentNodes.addAll(lastThenNodes);
        } else {
            lastParentNodes.add(ifCondition);
        }
    }

    @Override
    public void visit(WhileStmt whileStmt, Void arg) {
        DepVertex whileCondition = addNodeAndArcs(
                String.format("while (%s)", whileStmt.getCondition().toString()),
                whileStmt
        );

        lastParentNodes.add(whileCondition);

        whileStmt.getBody().accept(this, arg);

        while (!lastParentNodes.isEmpty()) {
            cfg.addEdge(lastParentNodes.poll(), whileCondition);
        }

        lastParentNodes.add(whileCondition);
        lastParentNodes.addAll(bodyBreaks);
        bodyBreaks.clear();
    }

    @Override
    public void visit(DoStmt doStmt, Void arg) {
        BlockStmt body = ASTUtils.blockWrapper(doStmt.getBody());

        body.accept(this, arg);

        DepVertex doWhileNode = addNodeAndArcs(
                String.format("while (%s)", doStmt.getCondition()),
                doStmt
        );

        if (!body.isEmpty()) {
            Statement firstBodyStatement = body.getStatement(0);

            try {
                DepVertex node = cfg.findNodeByAst(firstBodyStatement);
                cfg.addEdge(doWhileNode, node);
            } catch (NoSuchElementException ignored) {}
        }

        lastParentNodes.add(doWhileNode);
        lastParentNodes.addAll(bodyBreaks);
        bodyBreaks.clear();
    }

    @Override
    public void visit(ForStmt forStmt, Void arg) {
        Expression comparison = forStmt.getCompare().orElse(new BooleanLiteralExpr(true));

        forStmt.getInitialization().forEach(expression -> new ExpressionStmt(expression).accept(this, null));

        DepVertex forNode = addNodeAndArcs(
                String.format("for (;%s;)", comparison),
                forStmt
        );

        lastParentNodes.add(forNode);

        BlockStmt body = ASTUtils.blockWrapper(forStmt.getBody()).clone();

        forStmt.getUpdate().forEach(body::addStatement);

        body.accept(this, arg);

        while (!lastParentNodes.isEmpty()) {
            cfg.addEdge(lastParentNodes.poll(), forNode);
        }

        lastParentNodes.add(forNode);
        lastParentNodes.addAll(bodyBreaks);
        bodyBreaks.clear();
    }

    @Override
    public void visit(ForEachStmt forEachStmt, Void arg) {
        DepVertex foreachNode = addNodeAndArcs(
                String.format("for (%s : %s)", forEachStmt.getVariable(), forEachStmt.getIterable()),
                forEachStmt
        );

        lastParentNodes.add(foreachNode);

        forEachStmt.getBody().accept(this, arg);

        while (!lastParentNodes.isEmpty()) {
            cfg.addEdge(lastParentNodes.poll(), foreachNode);
        }

        lastParentNodes.add(foreachNode);
        lastParentNodes.addAll(bodyBreaks);
        bodyBreaks.clear();
    }

    @Override
    public void visit(SwitchStmt switchStmt, Void arg) {
        DepVertex switchNode = addNodeAndArcs(
                String.format("switch (%s)", switchStmt.getSelector()),
                switchStmt
        );

        lastParentNodes.add(switchNode);

        List<DepVertex> allEntryBreaks = new ArrayList<>();

        List<DepVertex> lastEntryStatementsWithNoBreak = new ArrayList<>();

        switchStmt.getEntries().forEach(switchEntryStmt -> {
            String label = switchEntryStmt.getLabel()
                    .map(expression -> "case " + expression)
                    .orElse("default");

            DepVertex switchEntryNode = addNodeAndArcs(label, switchEntryStmt);

            lastParentNodes.add(switchEntryNode);
            lastParentNodes.addAll(lastEntryStatementsWithNoBreak);
            lastEntryStatementsWithNoBreak.clear();

            switchEntryStmt.getStatements().accept(this, null);

            if (!bodyBreaks.isEmpty()) { // means it has break
                allEntryBreaks.addAll(bodyBreaks); // save breaks of entry

                lastParentNodes.clear();
                lastParentNodes.add(switchEntryNode); // Set switch as the only parent

                bodyBreaks.clear(); // Clear breaks
            } else {
                lastEntryStatementsWithNoBreak.addAll(lastParentNodes);
                lastParentNodes.clear();
                lastParentNodes.add(switchEntryNode);
            }
        });

        lastParentNodes.addAll(allEntryBreaks);
    }

    @Override
    public void visit(BreakStmt breakStmt, Void arg) {
        bodyBreaks.addAll(lastParentNodes);
    }

    @Override
    public void visit(ContinueStmt continueStmt, Void arg) {
        Statement continuableStatement = ASTUtils.findFirstAncestorStatementFrom(continueStmt, ASTUtils::isLoop);

        DepVertex continuableNode = cfg.findNodeByAst(continuableStatement);

        lastParentNodes.forEach(parentNode -> cfg.addEdge(parentNode, continuableNode));
    }

    @Override
    public void visit(ReturnStmt returnStmt, Void arg) {
        DepVertex node = addNodeAndArcs(
                returnStmt.toString(),
                returnStmt
        );

        lastParentNodes.add(node);
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration, Void arg) {
        if (!lastParentNodes.isEmpty() && Objects.equals(lastParentNodes.peek().getData(), "Stop")) {
            throw new IllegalStateException("CFG is only allowed for one method, not multiple!");
        }

        super.visit(methodDeclaration, arg);

        lastParentNodes.add(addNodeAndArcs("Stop", new EmptyStmt()));
    }

    private DepVertex addNodeAndArcs(String nodeData, Statement statement) {
        DepVertex node = new DepVertex(statement, nodeData);
        cfg.addVertex(node);

        assert !lastParentNodes.isEmpty();
        while (!lastParentNodes.isEmpty())
            cfg.addEdge(lastParentNodes.poll(), node);

        return node;
    }


}
