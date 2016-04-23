package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class Load extends IRInstruction {
    private VirtualRegister dest;
    private int size;
    private IntValue address;

    public Load(BasicBlock BB, VirtualRegister dest, int size, IntValue address) {
        super(BB);
        this.dest = dest;
        this.size = size;
        this.address = address;
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }

    public VirtualRegister getDest() {
        return dest;
    }

    public int getSize() {
        return size;
    }

    public IntValue getAddress() {
        return address;
    }
}
