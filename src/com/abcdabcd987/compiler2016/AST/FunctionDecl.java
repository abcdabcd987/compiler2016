package com.abcdabcd987.compiler2016.AST;

import com.abcdabcd987.compiler2016.Symbol.FunctionType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class FunctionDecl extends Decl {
    public final TypeNode returnType;
    public final String name;
    public final List<VariableDecl> argTypes;
    public final CompoundStmt body;
    public final SourcePosition posReturnType;
    public final SourcePosition posName;
    public FunctionType functionType;

    public static class Builder {
        private TypeNode returnType;
        private String name;
        private List<VariableDecl> argTypes = new ArrayList<>();
        private CompoundStmt body;
        private SourcePosition posReturnType;
        private SourcePosition posName;

        public void setReturnType(TypeNode returnType) {
            this.returnType = returnType;
        }
        public void setName(String name) {
            this.name = name;
        }
        public void setBody(CompoundStmt body) {
            this.body = body;
        }
        public void setPosReturnType(SourcePosition posReturnType) {
            this.posReturnType = posReturnType;
        }
        public void setPosName(SourcePosition posName) {
            this.posName = posName;
        }

        public void addArgType(Object node) {
            if (node instanceof VariableDecl) argTypes.add((VariableDecl)node);
            else throw new RuntimeException("Invalid type");
        }

        public FunctionDecl build() {
            return new FunctionDecl(returnType, name, argTypes, body, posReturnType, posName);
        }
    }

    public FunctionDecl(TypeNode returnType, String name, List<VariableDecl> argTypes, CompoundStmt body, SourcePosition posReturnType, SourcePosition posName) {
        this.returnType = returnType;
        this.name = name;
        this.argTypes = argTypes;
        this.body = body;
        this.posReturnType = posReturnType;
        this.posName = posName;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
