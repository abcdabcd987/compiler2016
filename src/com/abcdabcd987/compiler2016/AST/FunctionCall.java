package com.abcdabcd987.compiler2016.AST;

import com.abcdabcd987.compiler2016.Symbol.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class FunctionCall extends Expr {
    public final Expr name;
    public final List<Expr> parameters;
    public final SourcePosition posName;
    public final List<SourcePosition> posArgs;
    public Expr argThis = null; // for builtin string & array function

    public static class Builder {
        private Expr name;
        private List<Expr> parameters = new ArrayList<>();
        private SourcePosition posName;
        private List<SourcePosition> posArgs = new ArrayList<>();

        public void setName(Expr name) {
            this.name = name;
        }

        public void setPosName(SourcePosition posName) {
            this.posName = posName;
        }

        public void addArg(Object arg) {
            if (arg instanceof Expr) parameters.add((Expr)arg);
            else throw new RuntimeException("Invalid type");
        }

        public void addPosArg(SourcePosition pos) {
            posArgs.add(pos);
        }

        public FunctionCall build() {
            return new FunctionCall(name, parameters, posName, posArgs);
        }
    }

    public FunctionCall(Expr name, List<Expr> parameters, SourcePosition posName, List<SourcePosition> posArgs) {
        this.name = name;
        this.parameters = parameters;
        this.posName = posName;
        this.posArgs = posArgs;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
