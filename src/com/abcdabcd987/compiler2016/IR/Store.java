package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class Store extends IRInstruction {
    private int size;
    private IntValue address;
    private IntValue value;

    public Store(BasicBlock BB, int size, IntValue address, IntValue value) {
        super(BB);
        this.size = size;
        this.address = address;
        this.value = value;
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }

    public int getSize() {
        return size;
    }

    public IntValue getAddress() {
        return address;
    }

    public IntValue getValue() {
        return value;
    }
}
