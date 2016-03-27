package com.abcdabcd987.compiler2016.AST;

import java.util.List;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class Program extends ASTNode {
    public final List<RecordDecl> recordDecl;
    public final List<FunctionDecl> functionDecl;
    public final List<VariableDecl> variableDecl;

    public Program(List<RecordDecl> recordDecl, List<FunctionDecl> functionDecl, List<VariableDecl> variableDecl) {
        this.recordDecl = recordDecl;
        this.functionDecl = functionDecl;
        this.variableDecl = variableDecl;
    }
}
