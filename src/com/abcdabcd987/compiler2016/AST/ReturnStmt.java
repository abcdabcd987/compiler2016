package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class ReturnStmt extends Stmt {
    public final Expr value;

    public ReturnStmt(Expr value) {
        this.value = value;
    }
}
