package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class UnaryExpr extends Expr {
    public enum UnaryOp {
        INC, DEC, POS, NEG, LOGICAL_NOT, BITWISE_NOT
    }

    public UnaryOp op;
    public Expr body;
    public SourcePosition posOp;
    public SourcePosition posBody;

    public UnaryExpr(UnaryOp op, Expr body, SourcePosition posOp, SourcePosition posBody) {
        this.op = op;
        this.body = body;
        this.posOp = posOp;
        this.posBody = posBody;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
