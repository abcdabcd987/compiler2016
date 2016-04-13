package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class UnaryOperation extends IRNode implements WordValue {
    public enum UnaryOp {
        NEG, NOT
    }

    public UnaryOp op;
    public WordValue operand;
    public String hintName;

    public UnaryOperation(UnaryOp op, WordValue operand, String hintName) {
        this.op = op;
        this.operand = operand;
        this.hintName = hintName;
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public IRNode getIRNode() {
        return this;
    }
}
