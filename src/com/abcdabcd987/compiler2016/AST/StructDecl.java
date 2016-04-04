package com.abcdabcd987.compiler2016.AST;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class StructDecl extends Decl {
    public final List<VariableDecl> members;
    public final String name;
    public final SourcePosition posName;

    public static class Builder {
        private String name;
        private List<VariableDecl> members = new ArrayList<>();
        private SourcePosition posName;

        public void setPosName(SourcePosition posName) {
            this.posName = posName;
        }

        public void add(Object node) {
            if (node instanceof VariableDecl) members.add((VariableDecl)node);
            else throw new RuntimeException("Invalid type");
        }

        public void setName(String name) {
            this.name = name;
        }

        public StructDecl build() {
            return new StructDecl(name, members, posName);
        }
    }

    public StructDecl(String name, List<VariableDecl> members, SourcePosition posName) {
        this.members = members;
        this.name = name;
        this.posName = posName;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
