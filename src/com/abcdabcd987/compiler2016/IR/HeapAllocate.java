package com.abcdabcd987.compiler2016.IR;

import java.util.*;
import java.util.function.Function;

/**
 * Created by abcdabcd987 on 2016-04-14.
 */
public class HeapAllocate extends IRInstruction {
    private Register dest;
    private IntValue allocSize;

    public HeapAllocate(BasicBlock BB, VirtualRegister dest, IntValue allocSize) {
        super(BB);
        this.dest = dest;
        this.allocSize = allocSize;
        reloadUsedRegisterCollection();
    }

    public IntValue getAllocSize() {
        return allocSize;
    }

    public Register getDest() {
        return dest;
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Register getDefinedRegister() {
        return dest;
    }

    @Override
    protected void reloadUsedRegisterCollection() {
        usedRegister.clear();
        if (allocSize instanceof Register) usedRegister.add((Register) allocSize);
        usedIntValue.clear();
        usedIntValue.add(allocSize);
    }

    @Override
    public void setDefinedRegister(Register newReg) {
        dest = newReg;
    }

    @Override
    public void setUsedRegister(Map<Register, Register> regMap) {
        if (allocSize instanceof Register) allocSize = regMap.get(allocSize);
        reloadUsedRegisterCollection();
    }

    @Override
    public void renameDefinedRegister(Function<VirtualRegister, Integer> idSupplier) {
        if (dest instanceof VirtualRegister)
            dest = ((VirtualRegister)dest).getSSARenamedRegister(idSupplier.apply((VirtualRegister) dest));
    }

    @Override
    public void renameUsedRegister(Function<VirtualRegister, Integer> idSupplier) {
        if (allocSize instanceof VirtualRegister)
            allocSize = ((VirtualRegister) allocSize).getSSARenamedRegister(idSupplier.apply((VirtualRegister) allocSize));
        reloadUsedRegisterCollection();
    }

    @Override
    public void replaceIntValueUse(IntValue oldValue, IntValue newValue) {
        if (allocSize == oldValue) allocSize = newValue;
        reloadUsedRegisterCollection();
    }

    @Override
    public IRInstruction copyAndRename(Map<Object, Object> renameMap) {
        return new HeapAllocate(
                (BasicBlock) renameMap.getOrDefault(curBB, curBB),
                (VirtualRegister) renameMap.getOrDefault(dest, dest),
                (IntValue) renameMap.getOrDefault(allocSize, allocSize)
        );
    }

}
