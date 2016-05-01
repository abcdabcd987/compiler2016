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
        if (operand instanceof Register) usedRegister.add((Register) operand);
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
        if (operand instanceof Register) operand = regMap.get(operand);
        updateUsedRegisterCollection(regMap);
    }

    @Override
    public void renameDefinedRegister(Function<VirtualRegister, Integer> idSupplier) {
        if (dest instanceof VirtualRegister)
            dest = ((VirtualRegister) dest).newSSARenamedRegister(idSupplier.apply((VirtualRegister) dest));
    }

    @Override
    public void renameUsedRegister(Function<VirtualRegister, Integer> idSupplier) {
        if (operand instanceof VirtualRegister)
            operand = ((VirtualRegister) operand).newSSARenamedRegister(idSupplier.apply((VirtualRegister) operand));
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
