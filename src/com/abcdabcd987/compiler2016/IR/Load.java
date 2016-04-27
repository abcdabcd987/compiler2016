package com.abcdabcd987.compiler2016.IR;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class Load extends IRInstruction {
    private VirtualRegister dest;
    private int size;
    private IntValue address;

    public Load(BasicBlock BB, VirtualRegister dest, int size, IntValue address) {
        super(BB);
        this.dest = dest;
        this.size = size;
        this.address = address;
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
        if (address instanceof VirtualRegister) s.add((VirtualRegister) address);
        return s;
    }

    @Override
    public void renameDefinedRegister(Function<VirtualRegister, Integer> idSupplier) {
        dest = dest.newSSARenamedRegister(idSupplier.apply(dest));
    }

    @Override
    public void renameUsedRegister(Function<VirtualRegister, Integer> idSupplier) {
        if (address instanceof VirtualRegister)
            address = ((VirtualRegister) address).newSSARenamedRegister(idSupplier.apply((VirtualRegister) address));
    }

    public VirtualRegister getDest() {
        return dest;
    }

    public int getSize() {
        return size;
    }

    public IntValue getAddress() {
        return address;
    }
}
