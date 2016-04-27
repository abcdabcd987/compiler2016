package com.abcdabcd987.compiler2016.IR;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class BinaryOperation extends IRInstruction {
    public enum BinaryOp {
        ADD, SUB, MUL, DIV, MOD,
        SHL, SHR, AND, OR, XOR
    }

    private VirtualRegister dest;
    private BinaryOp op;
    private IntValue lhs;
    private IntValue rhs;

    public BinaryOperation(BasicBlock BB, VirtualRegister dest, BinaryOp op, IntValue lhs, IntValue rhs) {
        super(BB);
        this.dest = dest;
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public VirtualRegister getDest() {
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
        if (lhs instanceof VirtualRegister) s.add((VirtualRegister) lhs);
        if (rhs instanceof VirtualRegister) s.add((VirtualRegister) rhs);
        return s;
    }

    @Override
    public void renameDefinedRegister(Function<VirtualRegister, Integer> idSupplier) {
        dest = dest.newSSARenamedRegister(idSupplier.apply(dest));
    }

    @Override
    public void renameUsedRegister(Function<VirtualRegister, Integer> idSupplier) {
        if (lhs instanceof VirtualRegister)
            lhs = ((VirtualRegister) lhs).newSSARenamedRegister(idSupplier.apply((VirtualRegister) lhs));
        if (rhs instanceof VirtualRegister)
            rhs = ((VirtualRegister) rhs).newSSARenamedRegister(idSupplier.apply((VirtualRegister) rhs));
    }

}
