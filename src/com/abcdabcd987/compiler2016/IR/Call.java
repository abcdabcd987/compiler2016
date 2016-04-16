package com.abcdabcd987.compiler2016.IR;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abcdabcd987 on 2016-04-14.
 */
public class Call extends IRNode implements IntValue {
    private Function func;
    private List<IntValue> args = new ArrayList<>();

    public Call(Function func) {
        this.func = func;
    }

    public Function getFunc() {
        return func;
    }

    public Call appendArg(IntValue arg) {
        args.add(arg);
        return this;
    }

    public List<IntValue> getArgs() {
        return args;
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public IRNode getIRNode() {
        return this;
    }

    @Override
    public int getSize() {
        return func.getRetSize();
    }
}
