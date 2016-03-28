package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class SelfIncrement extends Expr {
    public final Expr self;

    public SelfIncrement(Expr self) {
        this.self = self;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
