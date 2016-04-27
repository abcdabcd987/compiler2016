package com.abcdabcd987.compiler2016.IR;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class Branch extends BranchInstruction {
    private IntValue cond;
    private BasicBlock then;
    private BasicBlock otherwise;

    public Branch(BasicBlock BB, IntValue cond, BasicBlock then, BasicBlock otherwise) {
        super(BB);
        this.cond = cond;
        this.then = then;
        this.otherwise = otherwise;
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
        Set<VirtualRegister> s = Collections.newSetFromMap(new HashMap<>());
        if (cond instanceof VirtualRegister) s.add((VirtualRegister) cond);
        return s;
    }

    @Override
    public void renameDefinedRegister(Function<VirtualRegister, Integer> idSupplier) {
        // do nothing
    }

    @Override
    public void renameUsedRegister(Function<VirtualRegister, Integer> idSupplier) {
        if (cond instanceof VirtualRegister)
            cond = ((VirtualRegister) cond).newSSARenamedRegister(idSupplier.apply((VirtualRegister) cond));
    }

    public IntValue getCond() {
        return cond;
    }

    public BasicBlock getThen() {
        return then;
    }

    public BasicBlock getElse() {
        return otherwise;
    }
}
