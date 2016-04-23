package com.abcdabcd987.compiler2016.IR;

import com.abcdabcd987.compiler2016.CompilerOptions;

/**
 * Created by abcdabcd987 on 2016-04-14.
 */
public class HeapAllocate extends IRInstruction {
    private VirtualRegister dest;
    private IntValue allocSize;

    public HeapAllocate(BasicBlock BB, VirtualRegister dest, IntValue allocSize) {
        super(BB);
        this.dest = dest;
        this.allocSize = allocSize;
    }

    public IntValue getAllocSize() {
        return allocSize;
    }

    public VirtualRegister getDest() {
        return dest;
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }
}
