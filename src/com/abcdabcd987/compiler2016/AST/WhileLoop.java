package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class WhileLoop extends Stmt {
    public Expr cond;
    public Stmt body;
    public SourcePosition posCond;

    public WhileLoop(Expr cond, Stmt body, SourcePosition posCond) {
        this.cond = cond;
        this.body = body;
        this.posCond = posCond;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
