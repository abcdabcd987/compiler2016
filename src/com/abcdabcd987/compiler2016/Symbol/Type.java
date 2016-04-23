package com.abcdabcd987.compiler2016.Symbol;

/**
 * Created by abcdabcd987 on 2016-03-31.
 */
public abstract class Type {
    public enum Types {
        INT, BOOL, STRING, VOID, ARRAY, STRUCT, FUNCTION, NULL
    }

    public Types type;

    public abstract String toStructureString(String indent);

    public abstract boolean isSameType(Type rhs);

    public abstract int getRegisterSize();

    public abstract int getMemorySize();

    public abstract boolean isPointerType();
}
