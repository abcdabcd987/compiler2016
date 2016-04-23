package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class BinaryOperation extends IRInstruction {
    public enum BinaryOp {
        ADD, SUB, MUL, DIV, MOD,
        SHL, SHR, AND, OR, XOR
    }

    private VirtualRegister dest;
    private BinaryOp op;
    private IntValue lhs;
    private IntValue rhs;

    public BinaryOperation(BasicBlock BB, VirtualRegister dest, BinaryOp op, IntValue lhs, IntValue rhs) {
        super(BB);
        this.dest = dest;
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public VirtualRegister getDest() {
        return dest;
    }

    public BinaryOp getOp() {
        return op;
    }

    public IntValue getLhs() {
        return lhs;
    }

    public IntValue getRhs() {
        return rhs;
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }

}
