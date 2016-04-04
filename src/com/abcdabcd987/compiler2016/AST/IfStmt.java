package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class IfStmt extends Stmt {
    public Expr cond;
    public Stmt then;
    public Stmt otherwise;
    public SourcePosition posCond;

    public IfStmt(Expr cond, Stmt then, Stmt otherwise, SourcePosition posCond) {
        this.cond = cond;
        this.then = then;
        this.otherwise = otherwise;
        this.posCond = posCond;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
