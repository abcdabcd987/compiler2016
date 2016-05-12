package com.abcdabcd987.compiler2016.IR;

import java.util.*;
import java.util.function.Function;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class Store extends IRInstruction {
    private int size;
    public IntValue address;
    public int offset;
    private IntValue value;
    public boolean isStaticData;

    public Store(BasicBlock BB, IntValue value, int size, IntValue address, int offset) {
        super(BB);
        this.size = size;
        this.address = address;
        this.offset = offset;
        this.value = value;
        this.isStaticData = false;
        reloadUsedRegisterCollection();
    }

    public Store(BasicBlock BB, IntValue value, int size, StaticData address) {
        this(BB, value, size, address, 0);
        isStaticData = true;
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public VirtualRegister getDefinedRegister() {
        return null;
    }

    @Override
    protected void reloadUsedRegisterCollection() {
        usedRegister.clear();
        // ignore StackSlot address in regalloc
        if (address instanceof Register && !(address instanceof StackSlot)) usedRegister.add((Register) address);
        if (value instanceof Register) usedRegister.add((Register) value);
        assert !(value instanceof StackSlot);
        usedIntValue.clear();
        usedIntValue.add(value);
        usedIntValue.add(address);
    }

    @Override
    public void setDefinedRegister(Register newReg) {
        assert false;
    }

    @Override
    public void setUsedRegister(Map<Register, Register> regMap) {
        if (address instanceof Register && !(address instanceof StackSlot)) address = regMap.get(address);
        if (value instanceof Register) value = regMap.get(value);
        reloadUsedRegisterCollection();
    }

    @Override
    public void renameDefinedRegister(Function<VirtualRegister, Integer> idSupplier) {
        // do nothing
    }

    @Override
    public void renameUsedRegister(Function<VirtualRegister, Integer> idSupplier) {
        if (address instanceof VirtualRegister)
            address = ((VirtualRegister) address).getSSARenamedRegister(idSupplier.apply((VirtualRegister) address));
        if (value instanceof VirtualRegister)
            value = ((VirtualRegister) value).getSSARenamedRegister(idSupplier.apply((VirtualRegister) value));
        reloadUsedRegisterCollection();
    }

    @Override
    public void replaceIntValueUse(IntValue oldValue, IntValue newValue) {
        if (address == oldValue) address = newValue;
        if (value == oldValue) value = newValue;
        reloadUsedRegisterCollection();
    }

    @Override
    public Store copyAndRename(Map<Object, Object> renameMap) {
        if (isStaticData)
            return new Store(
                    (BasicBlock) renameMap.getOrDefault(curBB, curBB),
                    (IntValue) renameMap.getOrDefault(value, value),
                    size,
                    (StaticData) address
            );
        else
            return new Store(
                    (BasicBlock) renameMap.getOrDefault(curBB, curBB),
                    (IntValue) renameMap.getOrDefault(value, value),
                    size,
                    (IntValue) renameMap.getOrDefault(address, address),
                    offset
            );
    }

    public int getSize() {
        return size;
    }

    public int getOffset() {
        return offset;
    }

    public IntValue getAddress() {
        return address;
    }

    public void setValue(IntValue value) {
        this.value = value;
        reloadUsedRegisterCollection();
    }

    public IntValue getValue() {
        return value;
    }
}
