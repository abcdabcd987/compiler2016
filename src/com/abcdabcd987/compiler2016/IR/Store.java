package com.abcdabcd987.compiler2016.IR;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Function;

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

    @Override
    public VirtualRegister getDefinedRegister() {
        return null;
    }

    @Override
    public Set<VirtualRegister> getUsedRegister() {
        Set<VirtualRegister> s = Collections.newSetFromMap(new HashMap<>());
        if (address instanceof VirtualRegister) s.add((VirtualRegister) address);
        if (value instanceof VirtualRegister) s.add((VirtualRegister) value);
        return s;
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

    public IntValue getAddress() {
        return address;
    }

    public IntValue getValue() {
        return value;
    }
}
