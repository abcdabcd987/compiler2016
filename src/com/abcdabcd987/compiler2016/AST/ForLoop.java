package com.abcdabcd987.compiler2016.AST;

import java.util.List;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class ForLoop extends Stmt {
    public List<VariableDecl> initWithDecl;
    public Expr init;
    public Expr cond;
    public Expr step;
    public Stmt body;
    public SourcePosition posCond;

    public ForLoop(Expr init, Expr cond, Expr step, Stmt body, SourcePosition posCond) {
        this.initWithDecl = null;
        this.init = init;
        this.cond = cond;
        this.step = step;
        this.body = body;
        this.posCond = posCond;
    }

    public ForLoop(List<VariableDecl> initWithDecl, Expr cond, Expr step, Stmt body, SourcePosition posCond) {
        this.initWithDecl = initWithDecl;
        this.init = null;
        this.body = body;
        this.cond = cond;
        this.step = step;
        this.posCond = posCond;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
