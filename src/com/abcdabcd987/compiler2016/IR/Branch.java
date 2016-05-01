package com.abcdabcd987.compiler2016.IR;

import java.util.*;
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
        if (cond instanceof Register) usedRegister.add((Register) cond);
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
    public void setDefinedRegister(Register newReg) {
        assert false;
    }

    @Override
    public void setUsedRegister(Map<Register, Register> regMap) {
        if (cond instanceof Register) cond = regMap.get(cond);
        updateUsedRegisterCollection(regMap);
    }

    @Override
    public void renameDefinedRegister(Function<VirtualRegister, Integer> idSupplier) {
        assert false;
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

    @Override
    public void insertSplitBlock(BasicBlock toBB, BasicBlock insertedBB) {
        if (then == toBB) then = insertedBB;
        if (otherwise == toBB) otherwise = insertedBB;
        updateConnectivity(curBB.getSucc(), toBB, insertedBB);
        updateConnectivity(toBB.getPred(), curBB, insertedBB);
    }
}
