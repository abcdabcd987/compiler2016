package com.abcdabcd987.compiler2016.IR;

import com.abcdabcd987.compiler2016.CompilerOptions;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class IntComparison extends IRInstruction {
    public enum Condition {
        EQ, NE, GT, GE, LT, LE
    }

    private VirtualRegister dest;
    private Condition cond;
    private IntValue lhs;
    private IntValue rhs;

    public IntComparison(BasicBlock BB, VirtualRegister dest, Condition cond, IntValue lhs, IntValue rhs) {
        super(BB);
        this.dest = dest;
        this.cond = cond;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public VirtualRegister getDest() {
        return dest;
    }

    public Condition getCond() {
        return cond;
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
