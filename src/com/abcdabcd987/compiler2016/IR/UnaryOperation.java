package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class UnaryOperation extends IRInstruction {
    public enum UnaryOp {
        NEG, NOT
    }

    private VirtualRegister dest;
    private UnaryOp op;
    private IntValue operand;

    public UnaryOperation(BasicBlock BB, VirtualRegister dest, UnaryOp op, IntValue operand) {
        super(BB);
        this.dest = dest;
        this.op = op;
        this.operand = operand;
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }

    public VirtualRegister getDest() {
        return dest;
    }

    public UnaryOp getOp() {
        return op;
    }

    public IntValue getOperand() {
        return operand;
    }
}
