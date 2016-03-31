package com.abcdabcd987.compiler2016.AST;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abcdabcd987 on 2016-03-27.
 */
public class NewExpr extends Expr {
    public final TypeNode type;
    public final List<Expr> dim;

    public static class Builder {
        private TypeNode type;
        private List<Expr> dim = new ArrayList<>();

        public void setType(TypeNode type) {
            this.type = type;
        }

        public void addDimension(Object node) {
            if (node instanceof Expr) dim.add((Expr)node);
            else throw new RuntimeException("Invalid type");
        }

        public NewExpr build() {
            return new NewExpr(type, dim);
        }
    }

    public NewExpr(TypeNode type, List<Expr> dim) {
        this.type = type;
        this.dim = dim;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
