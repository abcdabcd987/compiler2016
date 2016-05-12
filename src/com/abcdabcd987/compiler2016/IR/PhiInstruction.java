package com.abcdabcd987.compiler2016.IR;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.*;
import java.util.function.Function;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class PhiInstruction extends IRInstruction {
    private BasicBlock BB;
    public VirtualRegister dest;
    public Map<BasicBlock, IntValue> paths = new HashMap<>();

    public PhiInstruction(BasicBlock BB, VirtualRegister dest) {
        super(BB);
        this.BB = BB;
        this.dest = dest;
    }

    public void add(VirtualRegister value, BasicBlock from) {
        paths.put(from, value);
        if (value != null) usedRegister.add(value);
    }

    public IntValue getValue(BasicBlock from) {
        return paths.get(from);
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
    public void remove() {
        BB.phi.remove(dest);
    }

    @Override
    protected void reloadUsedRegisterCollection() {
        assert false;
    }

    @Override
    public void setDefinedRegister(Register newReg) {
        assert false;
    }

    @Override
    public void setUsedRegister(Map<Register, Register> regMap) {
        assert false;
    }

    @Override
    public void renameDefinedRegister(Function<VirtualRegister, Integer> idSupplier) {
        assert false;
    }

    @Override
    public void renameUsedRegister(Function<VirtualRegister, Integer> idSupplier) {
        assert false;
    }

    @Override
    public void replaceIntValueUse(IntValue oldValue, IntValue newValue) {
        for (Map.Entry<BasicBlock, IntValue> e : paths.entrySet())
            if (e.getValue() == oldValue)
                paths.put(e.getKey(), newValue);
    }

    @Override
    public IRInstruction copyAndRename(Map<Object, Object> renameMap) {
        // do nothing
        assert false;
        return null;
    }

    public BasicBlock getBasicBlock() {
        return BB;
    }
}
