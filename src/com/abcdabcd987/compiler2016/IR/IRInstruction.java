package com.abcdabcd987.compiler2016.IR;

import java.util.Set;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public abstract class IRInstruction {
    protected BasicBlock curBB;
    private IRInstruction prev = null;
    private IRInstruction next = null;

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

    public void remove() {
        if (prev != null) prev.next = next;
        if (next != null) next.prev = prev;
        if (curBB.getHead() == this) curBB.setHead(next);
        if (curBB.getLast() == this) curBB.setLast(prev);
    }

    public abstract void accept(IIRVisitor visitor);

    public abstract VirtualRegister getDefinedRegister();
    public abstract Set<VirtualRegister> getUsedRegister();
    public abstract void renameDefinedRegister(java.util.function.Function<VirtualRegister, Integer> idSupplier);
    public abstract void renameUsedRegister(java.util.function.Function<VirtualRegister, Integer> idSupplier);

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
