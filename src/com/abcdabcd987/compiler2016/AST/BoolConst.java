package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class BoolConst extends Expr {
    public final boolean value;

    public BoolConst(boolean value) {
        this.value = value;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
