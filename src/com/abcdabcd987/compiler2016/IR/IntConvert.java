package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-14.
 */
public class IntConvert extends IRNode implements IntValue {
    private int size;
    private IntValue source;

    public IntConvert(int size, IntValue source) {
        this.size = size;
        this.source = source;
    }

    public IntValue getSource() {
        return source;
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
        return size;
    }
}