package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-30.
 */
public class FunctionType extends Type {
    @Override
    public Types getType() {
        return Types.FUNCTION;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
