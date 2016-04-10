package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class Branch extends BranchInstruction {
    public Int1Value cond;
    public BasicBlock then;
    public BasicBlock otherwise;

    public Branch(Int1Value cond, BasicBlock then, BasicBlock otherwise) {
        this.cond = cond;
        this.then = then;
        this.otherwise = otherwise;
    }
}
