package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public abstract class IRNode {
    public IRNode next;
    public abstract void accept(IIRVisitor visitor);
}
