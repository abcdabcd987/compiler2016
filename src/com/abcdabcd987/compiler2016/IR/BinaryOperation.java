package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class BinaryOperation extends IRNode implements Int32Value {
    public enum BinaryOp {
        ADD, SUB, MUL, DIV,
        SHL, SHR, AND, OR, XOR
    }

    public BinaryOp op;
    public Int32Value lhs;
    public Int32Value rhs;

    public BinaryOperation(BinaryOp op, Int32Value lhs, Int32Value rhs) {
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
    }
}
