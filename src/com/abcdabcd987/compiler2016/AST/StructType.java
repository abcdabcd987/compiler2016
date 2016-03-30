package com.abcdabcd987.compiler2016.AST;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class StructType extends Type {
    public final String name;

    public StructType(String name) {
        this.name = name;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Types getType() {
        return Types.STRUCT;
    }
}
