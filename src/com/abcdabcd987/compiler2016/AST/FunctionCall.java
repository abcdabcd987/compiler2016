package com.abcdabcd987.compiler2016.AST;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class FunctionCall extends Expr {
    public final Symbol name;
    public final List<Expr> parameters;

    public static class Builder {
        private Symbol name;
        private List<Expr> parameters = new ArrayList<>();

        public void setName(Symbol name) {
            this.name = name;
        }

        public void addArg(Object arg) {
            if (arg instanceof Expr) parameters.add((Expr)arg);
            else throw new RuntimeException("Invalid type");
        }

        public FunctionCall build() {
            return new FunctionCall(name, parameters);
        }
    }

    public FunctionCall(Symbol name, List<Expr> parameters) {
        this.parameters = parameters;
        this.name = name;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
