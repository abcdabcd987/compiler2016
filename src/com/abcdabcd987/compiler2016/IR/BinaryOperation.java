package com.abcdabcd987.compiler2016.IR;

import java.util.*;
import java.util.function.Function;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class BinaryOperation extends IRInstruction {
    public enum BinaryOp {
        ADD, SUB, MUL, DIV, MOD,
        SHL, SHR, AND, OR, XOR
    }

    private Register dest;
    private BinaryOp op;
    private IntValue lhs;
    private IntValue rhs;

    public BinaryOperation(BasicBlock BB, Register dest, BinaryOp op, IntValue lhs, IntValue rhs) {
        super(BB);
        this.dest = dest;
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
        reloadUsedRegisterCollection();
    }

    public Register getDest() {
        return dest;
    }

    public BinaryOp getOp() {
        return op;
    }

    public IntValue getLhs() {
        return lhs;
    }

    public IntValue getRhs() {
        return rhs;
    }

    public void setLhs(IntValue value) {
        lhs = value;
        reloadUsedRegisterCollection();
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
        if (lhs instanceof Register) usedRegister.add((Register) lhs);
        if (rhs instanceof Register) usedRegister.add((Register) rhs);
        usedIntValue.clear();
        usedIntValue.add(lhs);
        usedIntValue.add(rhs);
    }

    @Override
    public void setDefinedRegister(Register newReg) {
        dest = newReg;
    }

    @Override
    public void setUsedRegister(Map<Register, Register> regMap) {
        if (lhs instanceof Register) lhs = regMap.get(lhs);
        if (rhs instanceof Register) rhs = regMap.get(rhs);
        reloadUsedRegisterCollection();
    }

    @Override
    public void renameDefinedRegister(Function<VirtualRegister, Integer> idSupplier) {
        dest = ((VirtualRegister) dest).getSSARenamedRegister(idSupplier.apply((VirtualRegister) dest));
    }

    @Override
    public void renameUsedRegister(Function<VirtualRegister, Integer> idSupplier) {
        if (lhs instanceof VirtualRegister)
            lhs = ((VirtualRegister) lhs).getSSARenamedRegister(idSupplier.apply((VirtualRegister) lhs));
        if (rhs instanceof VirtualRegister)
            rhs = ((VirtualRegister) rhs).getSSARenamedRegister(idSupplier.apply((VirtualRegister) rhs));
        reloadUsedRegisterCollection();
    }

    @Override
    public void replaceIntValueUse(IntValue oldValue, IntValue newValue) {
        if (lhs == oldValue) lhs = newValue;
        if (rhs == oldValue) rhs = newValue;
        reloadUsedRegisterCollection();
    }

    @Override
    public BinaryOperation copyAndRename(Map<Object, Object> renameMap) {
        return new BinaryOperation(
                (BasicBlock) renameMap.getOrDefault(curBB, curBB),
                (Register) renameMap.getOrDefault(dest, dest),
                op,
                (IntValue) renameMap.getOrDefault(lhs, lhs),
                (IntValue) renameMap.getOrDefault(rhs, rhs)
        );
    }

}
