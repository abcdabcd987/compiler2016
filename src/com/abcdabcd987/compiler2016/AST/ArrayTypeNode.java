package com.abcdabcd987.compiler2016.AST;

import com.abcdabcd987.compiler2016.Symbol.Type;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class ArrayTypeNode extends TypeNode {
    public final TypeNode baseType;

    public ArrayTypeNode(TypeNode baseType) {
        this.baseType = baseType;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Type.Types getType() {
        return Type.Types.ARRAY;
    }
}
