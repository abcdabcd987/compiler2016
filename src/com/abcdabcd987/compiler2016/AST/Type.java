package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public abstract class Type extends ASTNode {
    public enum Types {
        INT, BOOL, STRING, VOID, ARRAY, STRUCT, FUNCTION
    }
    public abstract Types getType();
}
