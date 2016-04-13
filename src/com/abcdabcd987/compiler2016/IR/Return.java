package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-11.
 */
public class Return extends BranchInstruction implements WordValue {
    public WordValue ret;

    public Return(WordValue ret) {
        this.ret = ret;
    }

    @Override
    public IRNode getIRNode() {
        return this;
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }
}
