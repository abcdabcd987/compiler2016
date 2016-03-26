package com.abcdabcd987.compiler2016.AST;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class CompoundStmt extends Stmt {
    public final List<Decl> decls;
    public final List<Stmt> stmts;

    public CompoundStmt(List<Decl> decls, List<Stmt> stmts) {
        this.decls = decls;
        this.stmts = stmts;
    }
}
