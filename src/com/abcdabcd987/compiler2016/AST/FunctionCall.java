package com.abcdabcd987.compiler2016.AST;

import java.util.List;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class FunctionCall extends Stmt {
    public final Symbol function;
    public final List<Expr> parameters;

    public FunctionCall(List<Expr> parameters, Symbol function) {
        this.parameters = parameters;
        this.function = function;
    }
}
