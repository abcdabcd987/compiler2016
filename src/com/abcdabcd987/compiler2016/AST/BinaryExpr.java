package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class BinaryExpr extends Expr {
    public enum BinaryOp {
        ASSIGN,
        LOGICAL_OR, LOGICAL_AND,
        BITWISE_OR, BITWISE_AND, XOR,
        EQ, NE, LT, GT, LE, GE,
        SHL, SHR,
        ADD, SUB,
        MUL, DIV, MOD
    }

    public final BinaryOp op;
    public Expr lhs;
    public Expr rhs;
    public final SourcePosition posOp;
    public final SourcePosition posLhs;
    public final SourcePosition posRhs;

    public BinaryExpr(BinaryOp op, Expr lhs, Expr rhs, SourcePosition posOp, SourcePosition posLhs, SourcePosition posRhs) {
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
        this.posOp = posOp;
        this.posLhs = posLhs;
        this.posRhs = posRhs;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
