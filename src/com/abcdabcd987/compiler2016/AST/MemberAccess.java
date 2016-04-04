package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class MemberAccess extends Expr {
    public final Expr record;
    public final String member;
    public final SourcePosition posRecord;
    public final SourcePosition posMember;

    public MemberAccess(Expr record, String member, SourcePosition posRecord, SourcePosition posMember) {
        this.record = record;
        this.member = member;
        this.posRecord = posRecord;
        this.posMember = posMember;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
