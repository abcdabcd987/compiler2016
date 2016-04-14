package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class BinaryOperation extends IRNode implements IntValue {
    public enum BinaryOp {
        ADD, SUB, MUL, DIV, MOD,
        SHL, SHR, AND, OR, XOR
    }

    public BinaryOp op;
    public IntValue lhs;
    public IntValue rhs;

    public BinaryOperation(BinaryOp op, IntValue lhs, IntValue rhs) {
        if (lhs.getSize() != rhs.getSize())
            throw new RuntimeException("Operands of a binary operation should have the same size.");
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public IRNode getIRNode() {
        return this;
    }

    @Override
    public int getSize() {
        return lhs.getSize();
    }
}
