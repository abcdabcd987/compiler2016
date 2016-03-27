package com.abcdabcd987.compiler2016.AST;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class FunctionDecl extends Decl {
    public final Type returnType;
    public final Symbol name;
    public final List<VariableDecl> argTypes;
    public final CompoundStmt body;

    public static class Builder {
        private Type returnType;
        private Symbol name;
        private List<VariableDecl> argTypes = new ArrayList<>();
        private CompoundStmt body;

        public void setReturnType(Type returnType) {
            this.returnType = returnType;
        }
        public void setName(Symbol name) {
            this.name = name;
        }
        public void setBody(CompoundStmt body) {
            this.body = body;
        }
        public void addArgType(Object node) {
            if (node instanceof VariableDecl) argTypes.add((VariableDecl)node);
            else throw new RuntimeException("Invalid type");
        }

        public FunctionDecl build() {
            return new FunctionDecl(returnType, name, argTypes, body);
        }
    }

    public FunctionDecl(Type returnType, Symbol name, List<VariableDecl> argTypes, CompoundStmt body) {
        this.returnType = returnType;
        this.name = name;
        this.argTypes = argTypes;
        this.body = body;
    }
}
