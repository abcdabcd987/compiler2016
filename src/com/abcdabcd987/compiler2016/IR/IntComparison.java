package com.abcdabcd987.compiler2016.IR;

import com.abcdabcd987.compiler2016.CompilerOptions;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class IntComparison extends IRNode implements IntValue {
    public enum Condition {
        EQ, NE, GT, GE, LT, LE
    }
    public Condition cond;
    public IntValue lhs;
    public IntValue rhs;

    public IntComparison(Condition cond, IntValue lhs, IntValue rhs) {
        this.cond = cond;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public IRNode getIRNode() {
        return this;
    }

    @Override
    public int getSize() {
        return CompilerOptions.getSizeBool();
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }
}
