package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class IntComparison extends IRNode implements Int1Value {
    public enum Condition {
        EQ, NE, GT, GE, LT, LE
    }
    public Condition cond;
    public Int32Value lhs;
    public Int32Value rhs;

    public IntComparison(Condition cond, Int32Value lhs, Int32Value rhs) {
        this.cond = cond;
        this.lhs = lhs;
        this.rhs = rhs;
    }
}
