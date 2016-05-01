package com.abcdabcd987.compiler2016.AST;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class CompoundStmt extends Stmt {
    public final List<Stmt> stmts;

    public static class Builder {
        private List<Stmt> stmts = new LinkedList<>();

        public void add(Object node) {
            if      (node instanceof Stmt) stmts.add((Stmt) node);
            else if (node instanceof List) ((List) node).stream().forEachOrdered(this::add);
            else throw new RuntimeException("CompoundStmt accepts Stmt only.");
        }

        public CompoundStmt build() { return new CompoundStmt(stmts); }
    }

    public CompoundStmt(List<Stmt> stmts) {
        this.stmts = stmts;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
