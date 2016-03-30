package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-30.
 */
public class PrimitiveType extends Type {
    private Types type;

    public PrimitiveType(Types type) {
        this.type = type;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Types getType() {
        return type;
    }
}
