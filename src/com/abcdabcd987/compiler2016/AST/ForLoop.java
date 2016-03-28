package com.abcdabcd987.compiler2016.AST;

import java.util.List;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class ForLoop extends Stmt {
    public final List<VariableDecl> initWithDecl;
    public final Expr init;
    public final Expr cond;
    public final Expr step;
    public final Stmt body;

    public ForLoop(Expr init, Expr cond, Expr step, Stmt body) {
        this.initWithDecl = null;
        this.init = init;
        this.cond = cond;
        this.step = step;
        this.body = body;
    }

    public ForLoop(List<VariableDecl> initWithDecl, Expr cond, Expr step, Stmt body) {
        this.initWithDecl = initWithDecl;
        this.init = null;
        this.body = body;
        this.cond = cond;
        this.step = step;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
