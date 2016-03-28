package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class UnaryExpr extends Expr {
    public enum UnaryOp {
        INC, DEC, POS, NEG, LOGICAL_NOT, BITWISE_NOT
    }

    public final UnaryOp op;
    public final Expr body;

    public UnaryExpr(UnaryOp op, Expr body) {
        this.op = op;
        this.body = body;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
