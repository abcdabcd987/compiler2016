package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-08.
 */
public class Jump extends BranchInstruction {
    private BasicBlock target;

    public Jump(BasicBlock BB, BasicBlock target) {
        super(BB);
        this.target = target;
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }

    public BasicBlock getTarget() {
        return target;
    }
}
