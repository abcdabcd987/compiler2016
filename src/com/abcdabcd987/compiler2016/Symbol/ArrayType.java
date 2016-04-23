package com.abcdabcd987.compiler2016.Symbol;

import com.abcdabcd987.compiler2016.CompilerOptions;

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

    @Override
    public boolean isSameType(Type rhs) {
        if (rhs.type == Types.NULL) return true;
        if (rhs.type != Types.ARRAY) return false;
        ArrayType t = (ArrayType) rhs;
        return bodyType.isSameType(t.bodyType);
    }

    @Override
    public int getRegisterSize() {
        return CompilerOptions.getSizePointer();
    }

    @Override
    public int getMemorySize() {
        return CompilerOptions.getSizePointer();
    }

    @Override
    public boolean isPointerType() {
        return true;
    }
}
