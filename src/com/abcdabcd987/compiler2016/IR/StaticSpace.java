package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-30.
 */
public class StaticSpace extends StaticData {
    public int length;

    public StaticSpace(int length, String hintName) {
        super(hintName);
        this.length = length;
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int getRegisterSize() {
        return length;
    }
}
