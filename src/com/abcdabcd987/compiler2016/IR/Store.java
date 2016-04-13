package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class Store extends IRNode {
    public WordValue address;
    public WordValue value;

    public Store(WordValue address, WordValue value) {
        this.address = address;
        this.value = value;
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }
}
