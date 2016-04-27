package com.abcdabcd987.compiler2016.IR;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by abcdabcd987 on 2016-04-11.
 */
public class Return extends BranchInstruction {
    private IntValue ret;

    public Return(BasicBlock BB, IntValue ret) {
        super(BB);
        this.ret = ret;
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
        if (ret instanceof VirtualRegister) s.add((VirtualRegister) ret);
        return s;
    }

    @Override
    public void renameDefinedRegister(Function<VirtualRegister, Integer> idSupplier) {
        // do nothing
    }

    @Override
    public void renameUsedRegister(Function<VirtualRegister, Integer> idSupplier) {
        if (ret instanceof VirtualRegister)
            ret = ((VirtualRegister) ret).newSSARenamedRegister(idSupplier.apply((VirtualRegister) ret));
    }

    public IntValue getRet() {
        return ret;
    }
}
