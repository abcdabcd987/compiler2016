package com.abcdabcd987.compiler2016.IR;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by abcdabcd987 on 2016-04-23.
 */
public class Move extends IRInstruction {
    private VirtualRegister dest;
    private IntValue source;

    public Move(BasicBlock BB, VirtualRegister dest, IntValue source) {
        super(BB);
        this.dest = dest;
        this.source = source;
    }

    public VirtualRegister getDest() {
        return dest;
    }

    public IntValue getSource() {
        return source;
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
        if (source instanceof VirtualRegister) s.add((VirtualRegister) source);
        return s;
    }

    @Override
    public void renameDefinedRegister(Function<VirtualRegister, Integer> idSupplier) {
        dest = dest.newSSARenamedRegister(idSupplier.apply(dest));
    }

    @Override
    public void renameUsedRegister(Function<VirtualRegister, Integer> idSupplier) {
        if (source instanceof VirtualRegister)
            source = ((VirtualRegister) source).newSSARenamedRegister(idSupplier.apply((VirtualRegister) source));
    }
}
