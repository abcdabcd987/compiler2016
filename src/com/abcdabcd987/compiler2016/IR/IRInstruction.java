package com.abcdabcd987.compiler2016.IR;

import java.util.*;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public abstract class IRInstruction {
    protected BasicBlock curBB;
    private IRInstruction prev = null;
    private IRInstruction next = null;
    protected List<Register> usedRegister = new ArrayList<>();

    public IRInstruction(BasicBlock curBB, IRInstruction prev, IRInstruction next) {
        this.curBB = curBB;
        this.prev = prev;
        this.next = next;
    }

    public IRInstruction(BasicBlock curBB) {
        this.curBB = curBB;
    }

    public void linkNext(IRInstruction node) {
        next = node;
        node.prev = this;
    }
    public void linkPrev(IRInstruction node) {
        prev = node;
        node.next = this;
    }

    public void prepend(IRInstruction node) {
        if (prev != null) prev.linkNext(node);
        node.linkNext(this);
    }

    public void append(IRInstruction node) {
        if (next != null)  next.linkPrev(node);
        node.linkPrev(this);
    }

    public void remove() {
        if (prev != null) prev.next = next;
        if (next != null) next.prev = prev;
        if (curBB.getHead() == this) curBB.setHead(next);
        if (curBB.getLast() == this) curBB.setLast(prev);
    }

    public abstract void accept(IIRVisitor visitor);

    public final Collection<Register> getUsedRegister() {
        return Collections.unmodifiableCollection(usedRegister);
    }
    public abstract Register getDefinedRegister();
    public abstract void setDefinedRegister(Register newReg);
    public abstract void setUsedRegister(Map<Register, Register> regMap);
    public abstract void renameDefinedRegister(java.util.function.Function<VirtualRegister, Integer> idSupplier);
    public abstract void renameUsedRegister(java.util.function.Function<VirtualRegister, Integer> idSupplier);

    /** helper function */
    protected void updateUsedRegisterCollection(Map<Register, Register> map) {
        for (int i = 0; i < usedRegister.size(); ++i) {
            Register reg = map.get(usedRegister.get(i));
            if (reg != null) usedRegister.set(i, reg);
        }
    }

    public IRInstruction getPrev() {
        return prev;
    }

    public IRInstruction getNext() {
        return next;
    }

    public BasicBlock getBasicBlock() {
        return curBB;
    }
}
