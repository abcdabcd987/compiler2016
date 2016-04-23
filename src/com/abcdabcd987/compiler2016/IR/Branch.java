package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class Branch extends BranchInstruction {
    private IntValue cond;
    private BasicBlock then;
    private BasicBlock otherwise;

    public Branch(BasicBlock BB, IntValue cond, BasicBlock then, BasicBlock otherwise) {
        super(BB);
        this.cond = cond;
        this.then = then;
        this.otherwise = otherwise;
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }

    public IntValue getCond() {
        return cond;
    }

    public BasicBlock getThen() {
        return then;
    }

    public BasicBlock getElse() {
        return otherwise;
    }
}
