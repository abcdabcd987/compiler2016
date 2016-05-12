package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-30.
 */
public class StackSlot extends Register {
    private Function parent;
    private String hintName;

    public StackSlot(Function parent, String hintName) {
        this.parent = parent;
        this.hintName = hintName;
        parent.stackSlots.add(this);
    }

    public Function getParent() {
        return parent;
    }

    public String getHintName() {
        return hintName;
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public IntValue copy() {
        // do nothing
        assert false;
        return null;
    }
}
