package com.abcdabcd987.compiler2016.IR;

import java.util.Set;
import java.util.function.Supplier;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public abstract class IRInstruction {
    private BasicBlock BB;
    private IRInstruction prev = null;
    private IRInstruction next = null;

    public IRInstruction(BasicBlock BB, IRInstruction prev, IRInstruction next) {
        this.BB = BB;
        this.prev = prev;
        this.next = next;
    }

    public IRInstruction(BasicBlock BB) {
        this.BB = BB;
    }

    public void linkNext(IRInstruction node) {
        assert next == null;
        assert node.prev == null;
        next = node;
        node.prev = this;
    }

    public void remove() {
        if (prev != null) prev.next = next;
        if (next != null) next.prev = prev;
        if (BB.getHead() == this) BB.setHead(next);
        if (BB.getLast() == this) BB.setLast(prev);
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
        return BB;
    }
}
