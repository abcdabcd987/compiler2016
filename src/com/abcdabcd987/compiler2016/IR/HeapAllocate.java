package com.abcdabcd987.compiler2016.IR;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Function;

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

    @Override
    public VirtualRegister getDefinedRegister() {
        return dest;
    }

    @Override
    public Set<VirtualRegister> getUsedRegister() {
        Set<VirtualRegister> s = Collections.newSetFromMap(new HashMap<>());
        if (allocSize instanceof VirtualRegister) s.add((VirtualRegister) allocSize);
        return s;
    }

    @Override
    public void renameDefinedRegister(Function<VirtualRegister, Integer> idSupplier) {
        dest = dest.newSSARenamedRegister(idSupplier.apply(dest));
    }

    @Override
    public void renameUsedRegister(Function<VirtualRegister, Integer> idSupplier) {
        if (allocSize instanceof VirtualRegister)
            allocSize = ((VirtualRegister) allocSize).newSSARenamedRegister(idSupplier.apply((VirtualRegister) allocSize));
    }
}
