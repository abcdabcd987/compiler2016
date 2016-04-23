package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-23.
 */
public class VirtualRegister extends IntValue {
    private String hintName;

    public VirtualRegister(String hintName) {
        this.hintName = hintName;
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }

    public String getHintName() {
        return hintName;
    }
}
