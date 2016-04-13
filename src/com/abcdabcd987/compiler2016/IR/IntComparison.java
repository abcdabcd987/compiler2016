package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class IntComparison extends IRNode implements WordValue {
    public enum Condition {
        EQ, NE, GT, GE, LT, LE
    }
    public Condition cond;
    public WordValue lhs;
    public WordValue rhs;

    public IntComparison(Condition cond, WordValue lhs, WordValue rhs) {
        this.cond = cond;
        this.lhs = lhs;
        this.rhs = rhs;
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
