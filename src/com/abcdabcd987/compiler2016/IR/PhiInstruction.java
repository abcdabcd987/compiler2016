package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class PhiInstruction extends IRInstruction {
    public PhiInstruction(BasicBlock BB) {
        super(BB);
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }
}
