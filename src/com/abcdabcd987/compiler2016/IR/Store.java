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

    public Store(BasicBlock BB, int size, IntValue address, int offset, IntValue value) {
        super(BB);
        this.size = size;
        this.address = address;
        this.offset = offset;
        this.value = value;
        this.isStaticData = false;
        if (address instanceof Register) usedRegister.add((Register) address);
        if (value instanceof Register) usedRegister.add((Register) value);
    }

    public Store(BasicBlock BB, int size, StaticData address, IntValue value) {
        this(BB, size, address, 0, value);
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
    public void setDefinedRegister(Register newReg) {
        assert false;
    }

    @Override
    public void setUsedRegister(Map<Register, Register> regMap) {
        if (address instanceof Register) address = regMap.get(address);
        if (value instanceof Register) value = regMap.get(value);
        updateUsedRegisterCollection(regMap);
    }

    @Override
    public void renameDefinedRegister(Function<VirtualRegister, Integer> idSupplier) {
        // do nothing
    }

    @Override
    public void renameUsedRegister(Function<VirtualRegister, Integer> idSupplier) {
        if (address instanceof VirtualRegister)
            address = ((VirtualRegister) address).newSSARenamedRegister(idSupplier.apply((VirtualRegister) address));
        if (value instanceof VirtualRegister)
            value = ((VirtualRegister) value).newSSARenamedRegister(idSupplier.apply((VirtualRegister) value));
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
        if (this.value instanceof Register)
            usedRegister.remove(this.value);
        this.value = value;
        if (value instanceof Register)
            usedRegister.add((Register) value);
    }

    public IntValue getValue() {
        return value;
    }
}
