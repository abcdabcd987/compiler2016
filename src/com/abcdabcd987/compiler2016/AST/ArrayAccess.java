package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class ArrayAccess extends Expr {
    public Expr array;
    public Expr subscript;
    public SourcePosition posArray;
    public SourcePosition posSubscript;

    public ArrayAccess(Expr array, Expr subscript, SourcePosition posArray, SourcePosition posSubscript) {
        this.array = array;
        this.subscript = subscript;
        this.posArray = posArray;
        this.posSubscript = posSubscript;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
