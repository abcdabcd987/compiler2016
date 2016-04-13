package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-11.
 */
public class Alloca extends IRNode implements WordValue {
    public String hintName;

    public Alloca(String hintName) {
        this.hintName = hintName != null ? hintName : "t";
    }

    @Override
    public IRNode getIRNode() {
        return this;
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }
}
