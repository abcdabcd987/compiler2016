package com.abcdabcd987.compiler2016.AST;

import java.util.List;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class FunctionDecl extends Decl {
    public final Type returnType;
    public final Symbol name;
    public final List<VariableDecl> argTypes;
    public final CompoundStmt body;

    public FunctionDecl(Type returnType, Symbol name, List<VariableDecl> argTypes, CompoundStmt body) {
        this.returnType = returnType;
        this.name = name;
        this.argTypes = argTypes;
        this.body = body;
    }
}
