package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class VariableDecl extends Decl {
    public final TypeNode type;
    public final String name;
    public final Expr init;

    public VariableDecl(TypeNode type, String name, Expr init) {
        this.type = type;
        this.name = name;
        this.init = init;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
