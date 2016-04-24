package com.abcdabcd987.compiler2016.IR;

import java.util.Set;
import java.util.function.*;
import java.util.function.Function;

/**
 * Created by abcdabcd987 on 2016-04-08.
 */
public class Jump extends BranchInstruction {
    private BasicBlock target;

    public Jump(BasicBlock BB, BasicBlock target) {
        super(BB);
        this.target = target;
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
        return null;
    }

    @Override
    public void renameDefinedRegister(Function<VirtualRegister, Integer> idSupplier) {
        // do nothing
    }

    @Override
    public void renameUsedRegister(Function<VirtualRegister, Integer> idSupplier) {
        // do nothing
    }

    public BasicBlock getTarget() {
        return target;
    }
}
