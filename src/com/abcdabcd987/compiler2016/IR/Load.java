package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class Load extends IRNode implements IntValue {
    private int size;
    private IntValue address;
    private String hintName;

    public Load(int size, IntValue address, String hintName) {
        this.size = size;
        this.address = address;
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
        return size;
    }

    public IntValue getAddress() {
        return address;
    }

    public String getHintName() {
        return hintName;
    }
}
