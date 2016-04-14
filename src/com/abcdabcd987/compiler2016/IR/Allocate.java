package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-11.
 */
public class Allocate extends IRNode implements IntValue {
    private String hintName;
    private int size;

    public Allocate(int size, String hintName) {
        this.size = size;
        this.hintName = hintName != null ? hintName : "t";
    }

    @Override
    public IRNode getIRNode() {
        return this;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }

    public String getHintName() {
        return hintName;
    }
}
