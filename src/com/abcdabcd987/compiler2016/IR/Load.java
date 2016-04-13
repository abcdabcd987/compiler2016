package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class Load extends IRNode implements WordValue {
    public WordValue address;
    public String hintName;

    public Load(WordValue address, String hintName) {
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
}
