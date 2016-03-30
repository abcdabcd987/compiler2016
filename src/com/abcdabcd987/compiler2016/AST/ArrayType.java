package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class ArrayType extends Type {
    public final Type baseType;

    public ArrayType(Type baseType) {
        this.baseType = baseType;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Types getType() {
        return Types.ARRAY;
    }
}
