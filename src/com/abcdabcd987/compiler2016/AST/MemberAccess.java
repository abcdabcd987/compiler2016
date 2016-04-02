package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class MemberAccess extends Expr {
    public final Expr record;
    public final String member;

    public MemberAccess(Expr record, String member) {
        this.record = record;
        this.member = member;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
