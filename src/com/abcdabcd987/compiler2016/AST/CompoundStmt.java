package com.abcdabcd987.compiler2016.AST;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class CompoundStmt extends Stmt {
    public final List<Decl> decls;
    public final List<Stmt> stmts;

    public static class Builder {
        private List<Decl> decls = new ArrayList<>();
        private List<Stmt> stmts = new ArrayList<>();

        public void add(Object node) {
            if      (node instanceof Decl) decls.add((Decl) node);
            else if (node instanceof Stmt) stmts.add((Stmt) node);
            else throw new RuntimeException("CompoundStmt accepts either Decl or Stmt only.");
        }

        public CompoundStmt build() { return new CompoundStmt(decls, stmts); }
    }

    public CompoundStmt(List<Decl> decls, List<Stmt> stmts) {
        this.decls = decls;
        this.stmts = stmts;
    }
}
