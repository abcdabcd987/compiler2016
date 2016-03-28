package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-27.
 */
public class NewExpr extends Expr {
    public final Type type;

    public NewExpr(Type type) {
        this.type = type;
    }
}
