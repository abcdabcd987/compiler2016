package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class RecordAccess extends Expr {
    public final Expr record;
    public final Symbol member;

    public RecordAccess(Expr record, Symbol member) {
        this.record = record;
        this.member = member;
    }
}
