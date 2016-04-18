package com.abcdabcd987.compiler2016.IR;

import com.abcdabcd987.compiler2016.CompilerOptions;

/**
 * Created by abcdabcd987 on 2016-04-14.
 */
public class HeapAllocate extends IRNode implements IntValue {
    private IntValue allocSize;

    public HeapAllocate(IntValue allocSize) {
        this.allocSize = allocSize;
    }

    public IntValue getAllocSize() {
        return allocSize;
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
        return CompilerOptions.getSizePointer();
    }
}
