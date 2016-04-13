package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-08.
 */
public class Jump extends BranchInstruction {
    public BasicBlock target;

    public Jump(BasicBlock target) {
        this.target = target;
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }
}
