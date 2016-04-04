package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class Identifier extends Expr {
    public final String name;
    public final SourcePosition pos;

    public Identifier(String name, SourcePosition pos) {
        this.name = name;
        this.pos = pos;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
