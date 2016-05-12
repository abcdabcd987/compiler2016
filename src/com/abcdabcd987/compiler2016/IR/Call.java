package com.abcdabcd987.compiler2016.IR;

import java.util.*;

/**
 * Created by abcdabcd987 on 2016-04-14.
 */
public class Call extends IRInstruction {
    private Register dest;
    private Function func;
    private List<IntValue> args = new ArrayList<>();

    public Call(BasicBlock BB, VirtualRegister dest, Function func) {
        super(BB);
        this.dest = dest;
        this.func = func;
    }

    public Register getDest() {
        return dest;
    }

    public Function getFunc() {
        return func;
    }

    public Call appendArg(IntValue arg) {
        args.add(arg);
        if (arg instanceof Register)
            usedRegister.add((Register) arg);
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
    public Register getDefinedRegister() {
        return dest;
    }

    @Override
    protected void reloadUsedRegisterCollection() {
        usedRegister.clear();
        usedIntValue.clear();
        for (IntValue arg : args) {
            if (arg instanceof Register)
                usedRegister.add((Register) arg);
            usedIntValue.add(arg);
        }
    }

    @Override
    public void setDefinedRegister(Register newReg) {
        dest = newReg;
    }

    @Override
    public void setUsedRegister(Map<Register, Register> regMap) {
        for (int i = 0; i < args.size(); ++i)
            if (args.get(i) instanceof Register) {
                args.set(i, regMap.get(args.get(i)));
            }
        reloadUsedRegisterCollection();
    }

    @Override
    public void renameDefinedRegister(java.util.function.Function<VirtualRegister, Integer> idSupplier) {
        if (dest instanceof VirtualRegister)
            dest = ((VirtualRegister) dest).getSSARenamedRegister(idSupplier.apply((VirtualRegister) dest));
    }

    @Override
    public void renameUsedRegister(java.util.function.Function<VirtualRegister, Integer> idSupplier) {
        for (int i = 0; i < args.size(); ++i)
            if (args.get(i) instanceof VirtualRegister) {
                VirtualRegister reg = (VirtualRegister) args.get(i);
                args.set(i, reg.getSSARenamedRegister(idSupplier.apply(reg)));
            }
        reloadUsedRegisterCollection();
    }

    @Override
    public void replaceIntValueUse(IntValue oldValue, IntValue newValue) {
        for (int i = 0; i < args.size(); ++i)
            if (args.get(i) == oldValue)
                args.set(i, newValue);
        reloadUsedRegisterCollection();
    }

    @Override
    public Call copyAndRename(Map<Object, Object> renameMap) {
        Call call = new Call(
                (BasicBlock) renameMap.getOrDefault(curBB, curBB),
                (VirtualRegister) renameMap.getOrDefault(dest, dest),
                func
        );
        for (IntValue arg : args) call.appendArg((IntValue) renameMap.getOrDefault(arg, arg));
        return call;
    }

}
