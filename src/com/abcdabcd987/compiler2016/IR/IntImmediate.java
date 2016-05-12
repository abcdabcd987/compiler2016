package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class IntImmediate extends IntValue {
    private int value;

    public IntImmediate(int value) {
        this.value = value;
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public IntImmediate copy() {
        return new IntImmediate(value);
    }

    public int getValue() {
        return value;
    }
}
