package com.abcdabcd987.compiler2016.Symbol;

import com.abcdabcd987.compiler2016.CompilerOptions;

/**
 * Created by abcdabcd987 on 2016-03-31.
 */
public class PrimitiveType extends VariableType {
    public PrimitiveType(Types type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "<PrimitiveType>" + this.type.name();
    }


    @Override
    public String toStructureString(String indent) {
        return indent + toString() + "\n";
    }

    @Override
    public boolean isSameType(Type rhs) {
        return type == rhs.type;
    }

    @Override
    public int getRegisterSize() {
        switch (type) {
            case INT:
                return CompilerOptions.getSizeInt();
            case BOOL:
                return CompilerOptions.getSizeBool();
            default:
                return CompilerOptions.getSizePointer();
        }
    }

    @Override
    public int getMemorySize() {
        return getRegisterSize();
    }

    @Override
    public boolean isPointerType() {
        switch (type) {
            case INT:
            case BOOL:
                return false;
            default:
                return true;
        }
    }

}
