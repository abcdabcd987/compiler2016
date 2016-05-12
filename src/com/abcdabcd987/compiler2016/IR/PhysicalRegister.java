package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-30.
 */
public abstract class PhysicalRegister extends Register {
    public abstract boolean isGeneral();
    public abstract boolean isCallerSave();
    public abstract boolean isCalleeSave();
    public abstract String getName();

    @Override
    public IntValue copy() {
        // do nothing
        return null;
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return getName();
    }
}
