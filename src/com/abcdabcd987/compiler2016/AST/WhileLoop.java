package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class WhileLoop extends Stmt {
    public final Expr cond;
    public final Stmt body;

    public WhileLoop(Expr cond, Stmt body) {
        this.cond = cond;
        this.body = body;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
