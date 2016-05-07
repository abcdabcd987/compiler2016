package com.abcdabcd987.compiler2016.IR;

import java.util.*;
import java.util.function.Function;

/**
 * Created by abcdabcd987 on 2016-04-11.
 */
public class Return extends BranchInstruction {
    private IntValue ret;

    public Return(BasicBlock BB, IntValue ret) {
        super(BB);
        this.ret = ret;
        reloadUsedRegisterCollection();
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
    protected void reloadUsedRegisterCollection() {
        usedRegister.clear();
        if (ret instanceof Register) usedRegister.add((Register) ret);
    }

    @Override
    public void setDefinedRegister(Register newReg) {
        assert false;
    }

    @Override
    public void setUsedRegister(Map<Register, Register> regMap) {
        if (ret instanceof Register) ret = regMap.get(ret);
        reloadUsedRegisterCollection();
    }

    @Override
    public void renameDefinedRegister(Function<VirtualRegister, Integer> idSupplier) {
        // do nothing
    }

    @Override
    public void renameUsedRegister(Function<VirtualRegister, Integer> idSupplier) {
        if (ret instanceof VirtualRegister)
            ret = ((VirtualRegister) ret).getSSARenamedRegister(idSupplier.apply((VirtualRegister) ret));
        reloadUsedRegisterCollection();
    }

    public IntValue getRet() {
        return ret;
    }

    @Override
    public void insertSplitBlock(BasicBlock toBB, BasicBlock insertedBB) {
        // do nothing
    }
}
