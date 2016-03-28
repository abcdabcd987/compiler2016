package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class StringType extends BasicType {

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
