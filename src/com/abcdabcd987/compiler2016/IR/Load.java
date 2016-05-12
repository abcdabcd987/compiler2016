package com.abcdabcd987.compiler2016.IR;

import java.util.*;
import java.util.function.Function;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class Load extends IRInstruction {
    private Register dest;
    private int size;
    public IntValue address;
    public int offset;
    public boolean isStaticData;
    public boolean isLoadAddress;

    public Load(BasicBlock BB, Register dest, int size, IntValue address, int offset) {
        super(BB);
        this.dest = dest;
        this.size = size;
        this.address = address;
        this.offset = offset;
        this.isStaticData = false;
        reloadUsedRegisterCollection();
    }

    public Load(BasicBlock BB, Register dest, int size, StaticData address, boolean isLoadAddress) {
        this(BB, dest, size, address, 0);
        this.isLoadAddress = isLoadAddress;
        isStaticData = true;
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
        // ignore StackSlot address in regalloc
        if (address instanceof Register && !(address instanceof StackSlot)) usedRegister.add((Register) address);
        usedIntValue.clear();
        usedIntValue.add(address);
    }

    @Override
    public void setDefinedRegister(Register newReg) {
        dest = newReg;
    }

    @Override
    public void setUsedRegister(Map<Register, Register> regMap) {
        if (address instanceof Register && !(address instanceof StackSlot)) address = regMap.get(address);
        reloadUsedRegisterCollection();
    }

    @Override
    public void renameDefinedRegister(Function<VirtualRegister, Integer> idSupplier) {
        if (dest instanceof VirtualRegister)
            dest = ((VirtualRegister) dest).getSSARenamedRegister(idSupplier.apply((VirtualRegister) dest));
    }

    @Override
    public void renameUsedRegister(Function<VirtualRegister, Integer> idSupplier) {
        if (address instanceof VirtualRegister)
            address = ((VirtualRegister) address).getSSARenamedRegister(idSupplier.apply((VirtualRegister) address));
        reloadUsedRegisterCollection();
    }

    @Override
    public void replaceIntValueUse(IntValue oldValue, IntValue newValue) {
        if (address == oldValue) address = newValue;
        reloadUsedRegisterCollection();
    }

    @Override
    public IRInstruction copyAndRename(Map<Object, Object> renameMap) {
        if (isStaticData)
            return new Load(
                    (BasicBlock) renameMap.getOrDefault(curBB, curBB),
                    (Register) renameMap.getOrDefault(dest, dest),
                    size,
                    (StaticData) address,
                    isLoadAddress
            );
        else
            return new Load(
                    (BasicBlock) renameMap.getOrDefault(curBB, curBB),
                    (Register) renameMap.getOrDefault(dest, dest),
                    size,
                    (IntValue) renameMap.getOrDefault(address, address),
                    offset
            );
    }

    public Register getDest() {
        return dest;
    }

    public int getSize() {
        return size;
    }

    public IntValue getAddress() {
        return address;
    }

    public int getOffset() {
        return offset;
    }
}
