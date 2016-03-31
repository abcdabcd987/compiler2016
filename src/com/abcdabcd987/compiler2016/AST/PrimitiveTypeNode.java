package com.abcdabcd987.compiler2016.AST;

import com.abcdabcd987.compiler2016.Symbol.Type;

/**
 * Created by abcdabcd987 on 2016-03-30.
 */
public class PrimitiveTypeNode extends TypeNode {
    private Type.Types type;

    public PrimitiveTypeNode(Type.Types type) {
        this.type = type;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Type.Types getType() {
        return type;
    }
}
