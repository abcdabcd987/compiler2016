package com.abcdabcd987.compiler2016.Symbol;

/**
 * Created by abcdabcd987 on 2016-03-31.
 */
public class ArrayType extends VariableType {
    public VariableType bodyType;
    public ArrayType(VariableType bodyType) {
        this.type = Types.ARRAY;
        this.bodyType = bodyType;
    }

    @Override
    public String toString() {
        return "<ArrayType>" + bodyType;
    }

    @Override
    public String toStructureString(String indent) {
        return toString() + "\n";
    }
}
