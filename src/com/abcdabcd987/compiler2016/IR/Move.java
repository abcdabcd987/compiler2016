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
        reloadUsedRegisterCollection();
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
    protected void reloadUsedRegisterCollection() {
        usedRegister.clear();
        if (source instanceof Register) usedRegister.add((Register) source);
        usedIntValue.clear();
        usedIntValue.add(source);
    }

    @Override
    public void setDefinedRegister(Register newReg) {
        dest = newReg;
    }

    @Override
    public void setUsedRegister(Map<Register, Register> regMap) {
        if (source instanceof Register) source = regMap.get(source);
        reloadUsedRegisterCollection();
    }

    @Override
    public void renameDefinedRegister(Function<VirtualRegister, Integer> idSupplier) {
        if (dest instanceof VirtualRegister)
            dest = ((VirtualRegister) dest).getSSARenamedRegister(idSupplier.apply((VirtualRegister) dest));
    }

    @Override
    public void renameUsedRegister(Function<VirtualRegister, Integer> idSupplier) {
        if (source instanceof VirtualRegister)
            source = ((VirtualRegister) source).getSSARenamedRegister(idSupplier.apply((VirtualRegister) source));
        reloadUsedRegisterCollection();
    }

    @Override
    public void replaceIntValueUse(IntValue oldValue, IntValue newValue) {
        if (source == oldValue) source = newValue;
        reloadUsedRegisterCollection();
    }

    @Override
    public Move copyAndRename(Map<Object, Object> renameMap) {
        return new Move(
                (BasicBlock) renameMap.getOrDefault(curBB, curBB),
                (Register) renameMap.getOrDefault(dest, dest),
                (IntValue) renameMap.getOrDefault(source, source)
        );
    }
}
