package com.abcdabcd987.compiler2016.IR;

import java.util.*;
import java.util.function.Function;

/**
 * Created by abcdabcd987 on 2016-04-23.
 */
public class Move extends IRInstruction {
    private Register dest;
    private IntValue source;

    public Move(BasicBlock BB, Register dest, IntValue source) {
        super(BB);
        this.dest = dest;
        this.source = source;
        if (source instanceof Register) usedRegister.add((Register) source);
    }

    public Register getDest() {
        return dest;
    }

    public IntValue getSource() {
        return source;
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
    public void setDefinedRegister(Register newReg) {
        dest = newReg;
    }

    @Override
    public void setUsedRegister(Map<Register, Register> regMap) {
        if (source instanceof Register) source = regMap.get(source);
        updateUsedRegisterCollection(regMap);
    }

    @Override
    public void renameDefinedRegister(Function<VirtualRegister, Integer> idSupplier) {
        if (dest instanceof VirtualRegister)
            dest = ((VirtualRegister) dest).newSSARenamedRegister(idSupplier.apply((VirtualRegister) dest));
    }

    @Override
    public void renameUsedRegister(Function<VirtualRegister, Integer> idSupplier) {
        if (source instanceof VirtualRegister)
            source = ((VirtualRegister) source).newSSARenamedRegister(idSupplier.apply((VirtualRegister) source));
    }
}
