package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class ArrayAccess extends Expr {
    public final Expr array;
    public final Expr subscript;

    public ArrayAccess(Expr array, Expr subscript) {
        this.array = array;
        this.subscript = subscript;
    }
}
