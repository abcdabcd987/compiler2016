package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class ArrayType extends Type {
    public final Type baseType;
    public final Expr size;

    public ArrayType(Type baseType, Expr size) {
        this.baseType = baseType;
        this.size = size;
    }
}
