package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-30.
 */
public class StackSlot extends Register {
    private String hintName;

    public StackSlot(String hintName) {
        this.hintName = hintName;
    }

    public String getHintName() {
        return hintName;
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }
}
