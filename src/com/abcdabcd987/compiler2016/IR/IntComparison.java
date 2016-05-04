package com.abcdabcd987.compiler2016.IR;

import java.util.*;
import java.util.function.Function;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class IntComparison extends IRInstruction {
    public enum Condition {
        EQ, NE, GT, GE, LT, LE
    }

    private Register dest;
    private Condition cond;
    private IntValue lhs;
    private IntValue rhs;

    public IntComparison(BasicBlock BB, VirtualRegister dest, Condition cond, IntValue lhs, IntValue rhs) {
        super(BB);
        this.dest = dest;
        this.cond = cond;
        this.lhs = lhs;
        this.rhs = rhs;
        if (lhs instanceof Register) usedRegister.add((Register) lhs);
        if (rhs instanceof Register) usedRegister.add((Register) rhs);
    }

    public Register getDest() {
        return dest;
    }

    public Condition getCond() {
        return cond;
    }

    public IntValue getLhs() {
        return lhs;
    }

    public IntValue getRhs() {
        return rhs;
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
        if (lhs instanceof Register) lhs = regMap.get(lhs);
        if (rhs instanceof Register) rhs = regMap.get(rhs);
        updateUsedRegisterCollection(regMap);
    }

    @Override
    public void renameDefinedRegister(Function<VirtualRegister, Integer> idSupplier) {
        if (dest instanceof VirtualRegister)
            dest = ((VirtualRegister) dest).newSSARenamedRegister(idSupplier.apply((VirtualRegister) dest));
    }

    @Override
    public void renameUsedRegister(Function<VirtualRegister, Integer> idSupplier) {
        if (lhs instanceof VirtualRegister)
            lhs = ((VirtualRegister) lhs).newSSARenamedRegister(idSupplier.apply((VirtualRegister) lhs));
        if (rhs instanceof VirtualRegister)
            rhs = ((VirtualRegister) rhs).newSSARenamedRegister(idSupplier.apply((VirtualRegister) rhs));
    }
}
