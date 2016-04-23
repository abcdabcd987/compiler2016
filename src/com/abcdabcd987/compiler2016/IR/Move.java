package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-23.
 */
public class Move extends IRInstruction {
    private VirtualRegister dest;
    private IntValue source;

    public Move(BasicBlock BB, VirtualRegister dest, IntValue source) {
        super(BB);
        this.dest = dest;
        this.source = source;
    }

    public VirtualRegister getDest() {
        return dest;
    }

    public IntValue getSource() {
        return source;
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }
}
