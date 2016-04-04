package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class ContinueStmt extends Stmt {
    public SourcePosition pos;

    public ContinueStmt(SourcePosition pos) {
        this.pos = pos;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
