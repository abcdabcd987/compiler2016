package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-11.
 */
public class Return extends BranchInstruction {
    public IntValue ret;

    public Return(IntValue ret) {
        this.ret = ret;
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }
}
