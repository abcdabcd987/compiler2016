package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class IfStmt extends Stmt {
    public final Expr cond;
    public final Stmt then;
    public final Stmt otherwise;

    public IfStmt(Expr cond, Stmt then, Stmt otherwise) {
        this.cond = cond;
        this.then = then;
        this.otherwise = otherwise;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
