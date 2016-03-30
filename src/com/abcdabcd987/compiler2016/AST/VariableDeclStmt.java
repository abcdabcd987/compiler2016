package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-30.
 */
public class VariableDeclStmt extends Stmt {
    public final VariableDecl decl;

    public VariableDeclStmt(VariableDecl decl) {
        this.decl = decl;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
