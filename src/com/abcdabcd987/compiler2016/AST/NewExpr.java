package com.abcdabcd987.compiler2016.AST;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abcdabcd987 on 2016-03-27.
 */
public class NewExpr extends Expr {
    public final TypeNode type;
    public final List<Expr> dim;
    public final List<SourcePosition> posDim;

    public static class Builder {
        private TypeNode type;
        private List<Expr> dim = new ArrayList<>();
        private List<SourcePosition> posDim  = new ArrayList<>();

        public void setType(TypeNode type) {
            this.type = type;
        }

        public void addDimension(Object node) {
            if (node instanceof Expr) dim.add((Expr)node);
            else if (node == null) dim.add(null);
            else throw new RuntimeException("Invalid type");
        }

        public void addPosDimension(SourcePosition pos) {
            posDim.add(pos);
        }

        public NewExpr build() {
            return new NewExpr(type, dim, posDim);
        }
    }

    public NewExpr(TypeNode type, List<Expr> dim, List<SourcePosition> posDim) {
        this.type = type;
        this.dim = dim;
        this.posDim = posDim;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
