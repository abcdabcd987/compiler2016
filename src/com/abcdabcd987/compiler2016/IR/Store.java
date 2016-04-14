package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class Store extends IRNode {
    public IntValue address;
    public IntValue value;

    public Store(IntValue address, IntValue value) {
        this.address = address;
        this.value = value;
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }
}
