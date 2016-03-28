package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class InitValue extends Initializer {
    public final Expr expr;

    public InitValue(Expr expr) {
        this.expr = expr;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
