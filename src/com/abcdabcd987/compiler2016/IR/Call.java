package com.abcdabcd987.compiler2016.IR;

import java.util.*;

/**
 * Created by abcdabcd987 on 2016-04-14.
 */
public class Call extends IRInstruction {
    private VirtualRegister dest;
    private Function func;
    private List<IntValue> args = new ArrayList<>();

    public Call(BasicBlock BB, VirtualRegister dest, Function func) {
        super(BB);
        this.dest = dest;
        this.func = func;
    }

    public VirtualRegister getDest() {
        return dest;
    }

    public Function getFunc() {
        return func;
    }

    public Call appendArg(IntValue arg) {
        args.add(arg);
        return this;
    }

    public List<IntValue> getArgs() {
        return args;
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
        args.stream().filter(x -> x instanceof  VirtualRegister).forEachOrdered(x -> s.add((VirtualRegister) x));
        return s;
    }

    @Override
    public void renameDefinedRegister(java.util.function.Function<VirtualRegister, Integer> idSupplier) {
        dest = dest.newSSARenamedRegister(idSupplier.apply(dest));
    }

    @Override
    public void renameUsedRegister(java.util.function.Function<VirtualRegister, Integer> idSupplier) {
        for (int i = 0; i < args.size(); ++i)
            if (args.get(i) instanceof VirtualRegister) {
                VirtualRegister reg = (VirtualRegister) args.get(i);
                args.set(i, reg.newSSARenamedRegister(idSupplier.apply(reg)));
            }
    }
}
