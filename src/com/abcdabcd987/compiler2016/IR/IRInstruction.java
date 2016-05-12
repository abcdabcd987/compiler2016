package com.abcdabcd987.compiler2016.IR;

import com.abcdabcd987.compiler2016.BackEnd.FunctionInliner;
import com.abcdabcd987.compiler2016.BackEnd.SSATransformer;

import java.util.*;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public abstract class IRInstruction {
    protected BasicBlock curBB;
    private IRInstruction prev = null;
    private IRInstruction next = null;
    boolean removed = false;
    protected List<Register> usedRegister = new ArrayList<>();
    protected List<IntValue> usedIntValue = new ArrayList<>();

    // liveness analysis
    public Set<VirtualRegister> liveIn = null;
    public Set<VirtualRegister> liveOut = null;

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
        else curBB.setHead(node);
        node.linkNext(this);
    }

    public void append(IRInstruction node) {
        if (next != null)  next.linkPrev(node);
        else curBB.setLast(node);
        node.linkPrev(this);
    }

    public void remove() {
        assert !removed;
        if (this instanceof BranchInstruction) curBB.cleanEnd();
        if (prev != null) prev.next = next;
        if (next != null) next.prev = prev;
        if (curBB.getHead() == this) curBB.setHead(next);
        if (curBB.getLast() == this) curBB.setLast(prev);
        removed = true;
    }

    public abstract void accept(IIRVisitor visitor);

    public final Collection<Register> getUsedRegister() {
        return Collections.unmodifiableCollection(usedRegister);
    }

    public final Collection<IntValue> getUsedIntValue() {
        return Collections.unmodifiableCollection(usedIntValue);
    }
    public abstract Register getDefinedRegister();
    protected abstract void reloadUsedRegisterCollection();
    public abstract void setDefinedRegister(Register newReg);
    public abstract void setUsedRegister(Map<Register, Register> regMap);
    public abstract void renameDefinedRegister(java.util.function.Function<VirtualRegister, Integer> idSupplier);
    public abstract void renameUsedRegister(java.util.function.Function<VirtualRegister, Integer> idSupplier);
    public abstract void replaceIntValueUse(IntValue oldValue, IntValue newValue);

    /**
     * used by {@link FunctionInliner}
     * make a copy of current instruction and rename used value
     * @param renameMap the map use to rename used value
     * @return return a renamed copy of current instruction
     */
    public abstract IRInstruction copyAndRename(Map<Object, Object> renameMap);

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
