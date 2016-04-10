package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class UnaryOperation extends IRNode implements Int32Value {
    public enum UnaryOp {
        NEG, NOT
    }

    public UnaryOp op;
    public Int32Value operand;
    public String hintName;

    public UnaryOperation(UnaryOp op, Int32Value operand, String hintName) {
        this.op = op;
        this.operand = operand;
        this.hintName = hintName;
    }
}
