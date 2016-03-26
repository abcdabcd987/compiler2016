package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class SelfDecrement extends Expr {
    public final Expr self;

    public SelfDecrement(Expr self) {
        this.self = self;
    }
}
