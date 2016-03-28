package com.abcdabcd987.compiler2016.AST;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class Program extends ASTNode {
    public final List<RecordDecl> recordDecl;
    public final List<FunctionDecl> functionDecl;
    public final List<VariableDecl> variableDecl;

    public static class Builder {
        private List<RecordDecl> recordDecl = new ArrayList<>();
        private List<FunctionDecl> functionDecl = new ArrayList<>();
        private List<VariableDecl> variableDecl = new ArrayList<>();

        public void add(Object node) {
            if (node instanceof RecordDecl) recordDecl.add((RecordDecl)node);
            else if (node instanceof FunctionDecl) functionDecl.add((FunctionDecl)node);
            else if (node instanceof VariableDecl) variableDecl.add((VariableDecl)node);
            else if (node instanceof List) ((List) node).stream().forEachOrdered(this::add);
            else throw new RuntimeException("Invalid type.");
        }

        public Program build() {
            return new Program(recordDecl, functionDecl, variableDecl);
        }
    }

    public Program(List<RecordDecl> recordDecl, List<FunctionDecl> functionDecl, List<VariableDecl> variableDecl) {
        this.recordDecl = recordDecl;
        this.functionDecl = functionDecl;
        this.variableDecl = variableDecl;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
