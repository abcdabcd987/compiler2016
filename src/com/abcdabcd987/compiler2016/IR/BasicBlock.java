package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class BasicBlock {
    private IRNode head = null;
    private IRNode last = null;
    private boolean ended = false;
    private String hintName;

    public BasicBlock(String hintName) {
        this.hintName = hintName != null ? hintName : "block";
    }

    public void append(IRNode next) {
        if (ended) {
            throw new RuntimeException("Cannot append instruction after a basic block ends.");
        }
        if (last != null) {
            last.next = next;
            last = next;
        } else {
            head = last = next;
        }
    }

    public void end(BranchInstruction next) {
        append(next);
        ended = true;
    }

    public IRNode getHead() {
        return head;
    }

    public IRNode getLast() {
        return last;
    }

    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }

    public String getHintName() {
        return hintName;
    }
}
