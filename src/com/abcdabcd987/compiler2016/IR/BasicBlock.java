package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class BasicBlock {
    private IRInstruction head = null;
    private IRInstruction last = null;
    private boolean ended = false;
    private String hintName;

    public BasicBlock(String hintName) {
        this.hintName = hintName != null ? hintName : "block";
    }

    public boolean isEnded() {
        return ended;
    }

    public void append(IRInstruction next) {
        if (ended) {
            throw new RuntimeException("Cannot append instruction after a basic block ends.");
        }
        if (last != null) {
            last.linkNext(next);
            last = next;
        } else {
            head = last = next;
        }
    }

    public void end(BranchInstruction next) {
        append(next);
        ended = true;
    }

    public void setHead(IRInstruction head) {
        this.head = head;
    }

    public void setLast(IRInstruction last) {
        this.last = last;
    }

    public IRInstruction getHead() {
        return head;
    }

    public IRInstruction getLast() {
        return last;
    }

    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }

    public String getHintName() {
        return hintName;
    }
}
