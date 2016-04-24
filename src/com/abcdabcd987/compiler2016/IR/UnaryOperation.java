package com.abcdabcd987.compiler2016.IR;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.function.*;
import java.util.function.Function;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class UnaryOperation extends IRInstruction {
    public enum UnaryOp {
        NEG, NOT
    }

    private VirtualRegister dest;
    private UnaryOp op;
    private IntValue operand;

    public UnaryOperation(BasicBlock BB, VirtualRegister dest, UnaryOp op, IntValue operand) {
        super(BB);
        this.dest = dest;
        this.op = op;
        this.operand = operand;
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
        if (operand instanceof VirtualRegister) s.add((VirtualRegister) operand);
        return s;
    }

    @Override
    public void renameDefinedRegister(Function<VirtualRegister, Integer> idSupplier) {
        dest = dest.newSSARenamedRegister(idSupplier.apply(dest));
    }

    @Override
    public void renameUsedRegister(Function<VirtualRegister, Integer> idSupplier) {
        if (operand instanceof VirtualRegister)
            operand = ((VirtualRegister) operand).newSSARenamedRegister(idSupplier.apply((VirtualRegister) operand));
    }

    public VirtualRegister getDest() {
        return dest;
    }

    public UnaryOp getOp() {
        return op;
    }

    public IntValue getOperand() {
        return operand;
    }
}
