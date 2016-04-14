package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class UnaryOperation extends IRNode implements IntValue {
    public enum UnaryOp {
        NEG, NOT
    }

    private UnaryOp op;
    private IntValue operand;
    private String hintName;

    public UnaryOperation(UnaryOp op, IntValue operand, String hintName) {
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

    @Override
    public int getSize() {
        return operand.getSize();
    }

    public UnaryOp getOp() {
        return op;
    }

    public IntValue getOperand() {
        return operand;
    }

    public String getHintName() {
        return hintName;
    }
}
