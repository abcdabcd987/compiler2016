package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class PhiNode extends IRNode implements IntValue {
    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public IRNode getIRNode() {
        return this;
    }

    @Override
    public int getSize() {
        return 0;
    }
}
