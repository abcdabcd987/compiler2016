package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class VariableDecl extends Decl {
    public TypeNode type;
    public String name;
    public Expr init;
    public SourcePosition posType;
    public SourcePosition posName;
    public SourcePosition posInit;

    public VariableDecl(TypeNode type, String name, Expr init, SourcePosition posType, SourcePosition posName, SourcePosition posInit) {
        this.type = type;
        this.name = name;
        this.init = init;
        this.posType = posType;
        this.posName = posName;
        this.posInit = posInit;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
