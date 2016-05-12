package com.abcdabcd987.compiler2016.IR;

import java.util.*;
import java.util.function.Function;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class UnaryOperation extends IRInstruction {
    public enum UnaryOp {
        NEG, NOT
    }

    private Register dest;
    private UnaryOp op;
    private IntValue operand;

    public UnaryOperation(BasicBlock BB, VirtualRegister dest, UnaryOp op, IntValue operand) {
        super(BB);
        this.dest = dest;
        this.op = op;
        this.operand = operand;
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
        if (operand instanceof Register) usedRegister.add((Register) operand);
        usedIntValue.clear();
        usedIntValue.add(operand);
    }

    @Override
    public void setDefinedRegister(Register newReg) {
        dest = newReg;
    }

    @Override
    public void setUsedRegister(Map<Register, Register> regMap) {
        if (operand instanceof Register) operand = regMap.get(operand);
        reloadUsedRegisterCollection();
    }

    @Override
    public void renameDefinedRegister(Function<VirtualRegister, Integer> idSupplier) {
        if (dest instanceof VirtualRegister)
            dest = ((VirtualRegister) dest).getSSARenamedRegister(idSupplier.apply((VirtualRegister) dest));
    }

    @Override
    public void renameUsedRegister(Function<VirtualRegister, Integer> idSupplier) {
        if (operand instanceof VirtualRegister)
            operand = ((VirtualRegister) operand).getSSARenamedRegister(idSupplier.apply((VirtualRegister) operand));
        reloadUsedRegisterCollection();
    }

    @Override
    public void replaceIntValueUse(IntValue oldValue, IntValue newValue) {
        if (operand == oldValue) operand = newValue;
        reloadUsedRegisterCollection();
    }

    @Override
    public IRInstruction copyAndRename(Map<Object, Object> renameMap) {
        return new UnaryOperation(
                (BasicBlock) renameMap.getOrDefault(curBB, curBB),
                (VirtualRegister) renameMap.getOrDefault(dest, dest),
                op,
                (IntValue) renameMap.getOrDefault(operand, operand)
        );
    }

    public Register getDest() {
        return dest;
    }

    public UnaryOp getOp() {
        return op;
    }

    public IntValue getOperand() {
        return operand;
    }
}
