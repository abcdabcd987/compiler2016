package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class ReturnStmt extends Stmt {
    public Expr value;
    public SourcePosition pos;
    public SourcePosition posValue;

    public ReturnStmt(Expr value, SourcePosition pos, SourcePosition posValue) {
        this.value = value;
        this.pos = pos;
        this.posValue = posValue;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
